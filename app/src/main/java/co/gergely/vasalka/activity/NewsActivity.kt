package co.gergely.vasalka.activity

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.R
import co.gergely.vasalka.api.ApiNews
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.model.News
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class NewsActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var date: TextView
    private lateinit var title: TextView
    private lateinit var author: TextView
    private lateinit var subtitle: TextView
    private lateinit var contentLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

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

        init();

        var id = intent.getLongExtra(Constants.NEWS_ID_KEY, 0)
        if (!id.equals(0)) {
            progressBar.visibility = View.VISIBLE
            var newsService = NetworkClient.getRetrofit(null).create(ApiNews::class.java)
            newsService.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updateUi(it);
                }, {
                    progressBar.visibility = View.GONE

                })
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

    fun init() {
        date = findViewById(R.id.date)
        title = findViewById(R.id.sectionTitle)
        author = findViewById(R.id.author)
        subtitle = findViewById(R.id.subtitle)
        contentLayout = findViewById(R.id.sectionLayout)
        progressBar = findViewById(R.id.progressBar)
    }

    fun updateUi(news: News) {
        date.text = news.createdAt
        title.text = news.title
        author.text = news.person.name
        subtitle.text = news.intro

        Collections.sort(news.sections)

        for (section in news.sections) {
            val v = layoutInflater.inflate(R.layout.item_news_section, null)
            val sectionTitle = v.findViewById<TextView>(R.id.sectionTitle)
            val sectionContent = v.findViewById<TextView>(R.id.sectionContent)
            if (section.title == null || section.title.isEmpty()) {
                sectionTitle.visibility = View.GONE
            } else {
                sectionTitle.text = section.title.capitalize()
            }
            sectionContent.text = Html.fromHtml(section.content)
            contentLayout.addView(v)
        }

        progressBar.visibility = View.GONE
    }

}
