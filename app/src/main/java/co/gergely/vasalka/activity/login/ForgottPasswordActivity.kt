package co.gergely.vasalka.activity.login

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.R
import co.gergely.vasalka.activity.BasicActivity
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth


class ForgottPasswordActivity : BasicActivity() {

    private lateinit var mySecurePreferences: MySecurePreferences
    private lateinit var parent: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var title: TextView
    private lateinit var sendBtn: Button
    private lateinit var emailLayout: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var imm: InputMethodManager
    private val TAG = "ForgottPasswordAct"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgott_password)

        init()
        updateUI()
        sendBtn.setOnClickListener {
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            if (Utils.isEmailValid(email.text.toString())) {
                showLoading(progressBar)
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Utils.showMessage(parent, resources.getString(R.string.email_sent))
                            val handler = Handler()
                            handler.postDelayed(Runnable {
                                onBackPressed()
                            }, 3000)
                        } else {
                            Utils.showMessage(parent, resources.getString(R.string.email_not_exist))
                        }
                        hideLoading(progressBar)
                    }
            } else {
                emailLayout.error = resources.getString(R.string.email_not_valid)
            }
        }
    }

    fun init() {
        mySecurePreferences = MySecurePreferences(application)
        parent = findViewById(R.id.parent)
        progressBar = findViewById(R.id.progressBar)
        emailLayout = findViewById(R.id.email_layout)
        email = findViewById(R.id.email)
        title = findViewById(R.id.pick_image_dialog_title)
        sendBtn = findViewById(R.id.sendBtn)
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun updateUI() {
        var typeFace = Typeface.createFromAsset(assets, "fonts/Luna.ttf")
        title.typeface = typeFace

        var myEmail = mySecurePreferences.getString(Constants.EMAIL_KEY, "")
        if (!myEmail.isEmpty()) {
            email.setText(myEmail)
        }

    }
}
