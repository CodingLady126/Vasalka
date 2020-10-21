package co.gergely.vasalka.activity.login

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
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
import com.crashlytics.android.answers.SignUpEvent
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class EmailSignupActivity : BasicActivity() {

    private var TAG = "EmailSignupActivity";
    private lateinit var auth: FirebaseAuth
    private lateinit var emailLayout: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var password: TextInputEditText
    private lateinit var passwordLayout2: TextInputLayout
    private lateinit var password2: TextInputEditText
    private lateinit var signupBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var title: TextView
    private lateinit var mySecurePreferences: MySecurePreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_signup)

        init()

        var typeFace = Typeface.createFromAsset(assets, "fonts/Luna.ttf")
        title.typeface = typeFace

        findViewById<View>(R.id.loginBtn).setOnClickListener {
            if(isValid()) {
                showLoading(progressBar)
                val imm:InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    if(it.isSuccessful) {
                        val user = auth.currentUser;
                        if(BuildConfig.DEBUG) {
                            Log.d(TAG, "createUserWithEmail:success")
                        }
                        // Utils.login(application, user!!, Constants.SIGN_TYPE_EMAIL)
                        user!!.sendEmailVerification()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Utils.showMessage(
                                        findViewById(R.id.parent),
                                        resources.getString(R.string.verify_email_sent_to) + user.email +
                                                "\n" + resources.getString(R.string.verify_email)
                                    )
                                    mySecurePreferences.putString(Constants.EMAIL_KEY, user.email)
                                    mySecurePreferences.putString(Constants.PASSWORD_KEY, password.text.toString())
                                    Answers.getInstance().logSignUp(
                                        SignUpEvent().putCustomAttribute(
                                            Constants.SIGN_IN_TYPE_KEY,
                                            Constants.SIGN_TYPE_EMAIL_STRING
                                        )
                                    )
                                }
                                val handler = Handler()
                                handler.postDelayed(Runnable {
                                    onBackPressed()
                                }, 3000)
                            }
                    } else {
                        if(BuildConfig.DEBUG) {
                            Log.w(TAG, "createUserWithEmail:failure", it.exception)
                            Utils.showMessage(findViewById(R.id.parent), it.exception!!.localizedMessage);
                        }
                    }
                        hideLoading(progressBar)
                }
            }
        }
    }

    fun init() {
        mySecurePreferences = MySecurePreferences(application);
        auth = FirebaseAuth.getInstance();
        emailLayout = findViewById(R.id.email_layout)
        passwordLayout = findViewById(R.id.passwordLayout)
        passwordLayout2 = findViewById(R.id.passwordLayout2)
        email = findViewById(R.id.email)
        password = findViewById(R.id.passwordEditText)
        password2 = findViewById(R.id.passwordEditText2)
        signupBtn = findViewById(R.id.loginBtn)
        progressBar = findViewById(R.id.progressBar)
        title = findViewById(R.id.pick_image_dialog_title)
    }

    fun isValid(): Boolean {

        val imm:InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (email.text!!.isEmpty()) {
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.email_cant_empty))
            emailLayout.error = resources.getString(R.string.email_cant_empty)
            return false
        } else {
            emailLayout.error = null
        }

        if(!Utils.isEmailValid(email.text.toString())) {
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.email_not_valid))
            emailLayout.error = resources.getString(R.string.email_not_valid)
            return false
        } else {
            emailLayout.error = null
        }

        if (password.text!!.isEmpty()) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.password_cant_be_empty))
            passwordLayout.error = resources.getString(R.string.password_cant_be_empty)
            return false
        } else {
            passwordLayout.error = null
        }

        if (password2.text!!.isEmpty()) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.password_cant_be_empty))
            passwordLayout2.error = resources.getString(R.string.password_cant_be_empty)
            return false
        } else {
            passwordLayout2.error = null
        }

        if (!password.text.toString().equals(password2.text.toString())) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.password_not_match))
            passwordLayout2.error = resources.getString(R.string.password_not_match)
            passwordLayout.error = resources.getString(R.string.password_not_match)
            password.text!!.clear()
            password2.text!!.clear()
            return false
        } else {
            passwordLayout2.error = null
            passwordLayout.error = null
        }

        if (password.text.toString().length < 8) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.password_too_short))
            passwordLayout.error = resources.getString(R.string.password_too_short)
            return false
        } else {
            passwordLayout.error = null
        }

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        var pattern: Pattern? = null
        try {
            // pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\\\d).*(?<=.{6,})$")
            pattern = Pattern.compile(PASSWORD_PATTERN)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        if (!pattern!!.matcher(password.text.toString()).matches()) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            Utils.showMessage(findViewById(R.id.parent), resources.getString(R.string.password_not_complex))
            passwordLayout.error = resources.getString(R.string.password_not_complex)
            return false
        } else {
            passwordLayout.error = null
        }
        return true;
    }
}
