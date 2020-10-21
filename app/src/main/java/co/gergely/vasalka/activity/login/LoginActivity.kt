package co.gergely.vasalka.activity.login

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.activity.AboutActivity
import co.gergely.vasalka.activity.BasicActivity
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*


class LoginActivity : BasicActivity() {
    private val TAG = "LoginActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var mySecurePreferences: MySecurePreferences
    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbLoginBtn: LoginButton
    private lateinit var parentLayout: View
    private lateinit var dataProtectionBtn: Button
    private lateinit var contactBtn: Button
    private lateinit var aboutBtn: Button

    private lateinit var progressBar: ProgressBar
    private val debug = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
        progressBar.visibility = View.GONE

        val vasalka_label = findViewById<View>(R.id.login_vassalka_label) as TextView
        var typeFace = Typeface.createFromAsset(assets, "fonts/Luna.ttf")
        vasalka_label.typeface = typeFace


        findViewById<View>(R.id.emailBtn).setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
        }

        findViewById<View>(R.id.googleBtn).setOnClickListener {
            googleSignIn();
        }

        findViewById<View>(R.id.facebookBtn).setOnClickListener {
            facebookLogin()
        }

        findViewById<View>(R.id.smsBtn).setOnClickListener {
            startActivity(Intent(this, SmsLoginActivity::class.java))
        }

        dataProtectionBtn.setOnClickListener {
            var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dataprotectionUrl)));
            startActivity(browserIntent);
        }

        aboutBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, AboutActivity::class.java))
        }

        contactBtn.setOnClickListener {
            Utils.sendSupportEmail(this@LoginActivity)
        }



    }

    fun init() {
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            mySecurePreferences = MySecurePreferences(application, auth.currentUser!!.uid)
        }
        fbLoginBtn = findViewById(R.id.fb_login_button)
        parentLayout = findViewById(R.id.parent)
        progressBar = findViewById(R.id.progressBar)
        dataProtectionBtn = findViewById(R.id.dataProtectionBtn)
        contactBtn = findViewById(R.id.contactBtn)
        aboutBtn = findViewById(R.id.aboutBtn)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, " onActivity Result REQUEST_CODE:" + requestCode)
        }
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                if (auth.currentUser != null) {
                    mySecurePreferences = MySecurePreferences(application, auth.currentUser!!.uid)
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google login error: " + e.localizedMessage)
                e.printStackTrace()
                Snackbar.make(
                    findViewById(R.id.parent),
                    resources.getText(R.string.google_login_error).toString() + ": " + e.localizedMessage,
                    Snackbar.LENGTH_SHORT
                ).show()
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, resources.getText(R.string.google_login_error).toString() + ": " + e.stackTrace)
                }
            }
        } else {
            fbCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun facebookLogin() {

        fbCallbackManager = CallbackManager.Factory.create();
        fbLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile"))
        fbLoginBtn.registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "Facebook token: " + loginResult.accessToken.token)
                }
                handleFacebookAccessToken(loginResult.accessToken)
                if (auth.currentUser != null) {
                    mySecurePreferences = MySecurePreferences(application, auth.currentUser!!.uid)
                }
            }

            override fun onCancel() {
                Snackbar.make(
                    findViewById(R.id.parent),
                    resources.getText(R.string.facebook_login_canceled),
                    Snackbar.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE

            }

            override fun onError(error: FacebookException) {
                Snackbar.make(
                    findViewById(R.id.parent),
                    resources.getText(R.string.facebook_login_error).toString() + error.localizedMessage,
                    Snackbar.LENGTH_SHORT
                ).show()
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, resources.getText(R.string.facebook_login_error).toString() + error.stackTrace)
                }
                progressBar.visibility = View.GONE

            }
        })
        fbLoginBtn.performClick()
    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso);
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Answers.getInstance().logLogin(
                        LoginEvent().putCustomAttribute(
                            Constants.SIGN_IN_TYPE_KEY,
                            Constants.SIGN_TYPE_GOOGLE_STRING
                        )
                    )
                    Utils.login(this, application, user!!, Constants.SIGN_TYPE_GOOGLE, progressBar)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(
                        findViewById(R.id.parent),
                        resources.getText(R.string.authentication_failed).toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        progressBar.visibility = View.VISIBLE
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Answers.getInstance().logLogin(
                        LoginEvent().putCustomAttribute(
                            Constants.SIGN_IN_TYPE_KEY,
                            Constants.SIGN_TYPE_FACEBOOK_STRING
                        )
                    )
                    Utils.login(this, application, user!!, Constants.SIGN_TYPE_FACEBOOK, progressBar)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(
                        findViewById(R.id.parent),
                        resources.getText(R.string.authentication_failed).toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }
/*
    override fun onResume() {
        super.onResume()
        if (Utils.isAuthenticated()) {
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
            startActivity(intent)
            this.finish()
        }
    }
    */
}
