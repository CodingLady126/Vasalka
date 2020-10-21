package co.gergely.vasalka.activity.login

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import co.gergely.vasalka.R
import co.gergely.vasalka.activity.BasicActivity
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class EmailLoginActivity : BasicActivity() {

    private var TAG = "EmailLoginActivity";
    private lateinit var parent: View;
    private lateinit var auth: FirebaseAuth
    private lateinit var imm: InputMethodManager
    private lateinit var mySecurePreferences: MySecurePreferences
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var title: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        mySecurePreferences = MySecurePreferences(application!!);

        init()
        updateUI()

        findViewById<View>(R.id.loginBtn).setOnClickListener {
            if(isValid()) {
                showLoading(progressBar)
                mySecurePreferences.putString(Constants.EMAIL_KEY, emailEditText.text.toString())
                mySecurePreferences.putString(Constants.PASSWORD_KEY, passwordEditText.text.toString())

                auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if (currentFocus != null) {
                            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        }
                        if(it.isSuccessful) {
                            Answers.getInstance().logLogin(
                                LoginEvent().putCustomAttribute(
                                    Constants.SIGN_IN_TYPE_KEY,
                                    Constants.SIGN_TYPE_EMAIL_STRING
                                )
                            )
                            Utils.login(this, application, auth.currentUser!!, Constants.SIGN_TYPE_EMAIL, progressBar)
                            hideLoading(progressBar)
                            this.finish()
                        } else {
                            Utils.showMessage(parent, resources.getString(R.string.authentication_failed))
                        }
                        hideLoading(progressBar)
                    }
            }
        }

        findViewById<View>(R.id.signupTextView).setOnClickListener {
            startActivity(Intent(this@EmailLoginActivity, EmailSignupActivity::class.java))
        }

        findViewById<View>(R.id.forgott_password).setOnClickListener {
            startActivity(Intent(this@EmailLoginActivity, ForgottPasswordActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    fun init() {
        auth = FirebaseAuth.getInstance()
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        emailLayout = findViewById(R.id.email_layout)
        emailEditText = findViewById(R.id.email);
        passwordLayout = findViewById(R.id.passwordLayout)
        passwordEditText = findViewById(R.id.passwordEditText);
        parent = findViewById(R.id.parent)
        title = findViewById(R.id.pick_image_dialog_title)
    }
    
    fun isValid(): Boolean {
        val email = emailEditText.text.toString()
        val pw = passwordEditText.text.toString()
        
        if(!Utils.isEmailValid(email)) {
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            emailLayout.error = resources.getString(R.string.email_not_valid)
            return false
        }

        if(pw.isEmpty()) {
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            passwordLayout.error = resources.getString(R.string.password_cant_be_empty)
            return false;
        }

        return true;
    }

    fun updateUI() {

        var typeFace = Typeface.createFromAsset(assets, "fonts/Luna.ttf")
        title.typeface = typeFace

        var myEmail = mySecurePreferences.getString(Constants.EMAIL_KEY, "")
        if (!myEmail.isEmpty()) {
            emailEditText.setText(myEmail)
        }
        var myPassword = mySecurePreferences.getString(Constants.PASSWORD_KEY, "")
        if (!myPassword.isEmpty()) {
            passwordEditText.setText(myPassword)
        }

    }
    
}
