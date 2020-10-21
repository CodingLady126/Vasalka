package co.gergely.vasalka.activity.login

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.activity.BasicActivity
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SmsLoginActivity : BasicActivity() {

    private val TAG = "SmsLoginActiviy"
    private val debug = true;
    private lateinit var parent: View;
    private lateinit var info: TextView
    private lateinit var tel: TextInputEditText
    private lateinit var telLayout: TextInputLayout
    private lateinit var btn: Button
    private lateinit var imm: InputMethodManager
    private lateinit var mySecurePreferences: MySecurePreferences
    private lateinit var progressBar: ProgressBar
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_login)

        init()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "onVerificationCompleted:$credential")
                }

                signInWithPhoneAuthCredential(credential)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                reset()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (BuildConfig.DEBUG && debug) {
                    Log.w(TAG, "onVerificationFailed", e)
                }

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Utils.showMessage(parent, resources.getString(R.string.invalid_request))
                    reset()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    reset()
                    Utils.showMessage(
                        parent,
                        resources.getString(R.string.sms_quota_exceeded)
                    )
                }
            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "onCodeSent:" + verificationId!!)
                }
                hideLoading(progressBar)

                mySecurePreferences.putBoolean(Constants.SMS_LOGIN_IN_PROGRESS_KEY, true)
                mySecurePreferences.putString(Constants.SMS_VERIFICATION_ID_KEY, verificationId)
                mySecurePreferences.putLong(Constants.SMS_VERIFICATION_TIME_KEY, Date().getTime())

                info.text = resources.getString(R.string.sms_info_text_code)
                btn.text = resources.getString(R.string.sms_verify)
                tel.setText("")
                tel.hint = resources.getString(R.string.tel_hint_code)

                btn.setOnClickListener {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    progressBar.visibility = View.VISIBLE
                    val code = tel.text.toString()
                    val verificationId = mySecurePreferences.getString(Constants.SMS_VERIFICATION_ID_KEY, "");
                    if (!verificationId.isEmpty()) {
                        val credential = PhoneAuthProvider.getCredential(verificationId, code)
                        signInWithPhoneAuthCredential(credential)
                        it.isEnabled = false
                    }
                }
                Utils.showMessage(parent, resources.getString(R.string.sms_check_your_messages))
            }
        }
    }

    fun init() {
        mySecurePreferences = MySecurePreferences(application)
        tel = findViewById(R.id.tel)
        telLayout = findViewById(R.id.tel_layout)
        info = findViewById(R.id.sms_login_info_text)
        btn = findViewById(R.id.sendBtn)
        progressBar = findViewById(R.id.sms_login_progressbar)
        parent = findViewById(R.id.parent)
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onStart() {
        super.onStart()
        if (mySecurePreferences.getBoolean(Constants.SMS_LOGIN_IN_PROGRESS_KEY, false)) {

            val verificationId = mySecurePreferences.getString(Constants.SMS_VERIFICATION_ID_KEY, "")
            val verificationTime = mySecurePreferences.getLong(Constants.SMS_VERIFICATION_TIME_KEY, 0)
            val now = Date().time
            val diff = (now - verificationTime) / 60

            if (diff > 2) {
                reset()
                btn.setOnClickListener {
                    if (isPhoneNumberValid(tel.text.toString())) {
                        telLayout.error = null
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        progressBar.visibility = View.VISIBLE
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            tel.text.toString(),      // Phone number to verify
                            60,               // Timeout duration
                            TimeUnit.SECONDS, // Unit of timeout
                            this,             // Activity (for callback binding)
                            callbacks
                        ) // OnVerificationStateChangedCallbacks
                    } else {
                        telLayout.error = resources.getString(R.string.phone_not_valid)
                    }
                }
            } else {
                info.text = resources.getString(R.string.sms_info_text_code)
                btn.text = resources.getString(R.string.sms_verify)
                tel.hint = resources.getString(R.string.tel_hint_code)

                val code = tel.text.toString()
                if (!verificationId.isEmpty()) {
                    btn.setOnClickListener {
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        val credential = PhoneAuthProvider.getCredential(verificationId, code)
                        progressBar.visibility = View.VISIBLE
                        signInWithPhoneAuthCredential(credential)
                        it.isEnabled = false
                    }
                }
            }
        } else {
            btn.setOnClickListener {
                if (isPhoneNumberValid(tel.text.toString())) {
                    telLayout.error = null
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    progressBar.visibility = View.VISIBLE
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        tel.text.toString(),      // Phone number to verify
                        60,               // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this,             // Activity (for callback binding)
                        callbacks
                    ) // OnVerificationStateChangedCallbacks
                } else {
                    telLayout.error = resources.getString(R.string.phone_not_valid)
                }
            }
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        reset()
        var auth = FirebaseAuth.getInstance()

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "signInWithCredential:success")
                    }
                    val user = task.result?.user
                    Answers.getInstance().logLogin(
                        LoginEvent().putCustomAttribute(
                            Constants.SIGN_IN_TYPE_KEY,
                            Constants.SIGN_TYPE_SMS_STRING
                        )
                    )
                    Utils.login(this, application, user!!, Constants.SIGN_TYPE_SMS, progressBar)
                } else {
                    // Sign in failed, display a message and update the UI
                    if (BuildConfig.DEBUG && debug) {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Utils.showMessage(parent, resources.getString(R.string.wrong_code))
                    }
                }
                progressBar.visibility = View.GONE
            }
    }

    fun reset() {
        mySecurePreferences.remove(Constants.SMS_LOGIN_IN_PROGRESS_KEY)
        mySecurePreferences.remove(Constants.SMS_VERIFICATION_ID_KEY)
        mySecurePreferences.remove(Constants.SMS_VERIFICATION_TIME_KEY)
    }

    fun isPhoneNumberValid(number: String): Boolean {
        val pattern = Pattern.compile("^\\+[0-9]{10,13}\$")
        val matcher = pattern.matcher(number)
        return matcher.matches()
    }
}
