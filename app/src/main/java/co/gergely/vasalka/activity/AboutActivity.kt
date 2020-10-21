package co.gergely.vasalka.activity

import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.tools.Utils


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        var toolbar = findViewById<Toolbar>(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        val actionbar: ActionBar? = supportActionBar
        actionbar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar?.setCustomView(R.layout.app_bar_layout)
        actionbar?.setDisplayShowCustomEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        var titleId = getResources().getIdentifier(
            "title", "id",
            "android"
        );
        findViewById<TextView>(titleId)?.typeface = Typeface.createFromAsset(assets, "fonts/Luna.ttf")

        findViewById<Button>(R.id.contactBtn).setOnClickListener {
            Utils.sendSupportEmail(this@AboutActivity)
        }

        try {
            val pInfo = getPackageManager().getPackageInfo(packageName, 0)
            val versionName = pInfo.versionName
            val versionCode = pInfo.versionCode
            var str: String?
            if (BuildConfig.DEBUG) {
                str = getString(R.string.app_name) + "-debug-" + versionName + " (" + versionCode + ")"
            } else {
                str = getString(R.string.app_name) + "-release-" + versionName + " (" + versionCode + ")"
            }
            findViewById<TextView>(R.id.buildVersionText).text = str
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            findViewById<View>(R.id.buildVersionLabel).visibility = View.GONE
            findViewById<TextView>(R.id.buildVersionText).visibility = View.GONE
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item!!.itemId == android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }
}
