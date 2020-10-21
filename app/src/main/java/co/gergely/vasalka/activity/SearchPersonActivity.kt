package co.gergely.vasalka.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.adapter.PersonListAdapter
import co.gergely.vasalka.api.ApiPerson
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.model.Person
import co.gergely.vasalka.model.SearchPerson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class SearchPersonActivity() : AppCompatActivity(), OnHandlePerson {

    private val debug = false;
    private val TAG = "SearchPrsnActivity";
    private lateinit var searchText: TextInputEditText
    private lateinit var searchResultList: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_person)


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



        searchText = findViewById(R.id.person_search_text)
        searchResultList = findViewById(R.id.person_search_result_list)
        progressBar = findViewById(R.id.progressBar)

        val itemDecorator = DividerItemDecoration(this@SearchPersonActivity, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(getResources().getDrawable(R.drawable.item_decorator))

        var personService = NetworkClient.getRetrofit(null).create(ApiPerson::class.java)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                progressBar.visibility = View.VISIBLE
                if (!searchText.text.toString().isEmpty()) {
                    personService.searchByName(VasalkaApp.getSession().myPerson.id, searchText.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<List<Person>> {
                            override fun onSubscribe(d: Disposable) {
                                progressBar.visibility = View.GONE
                            }

                            override fun onNext(people: List<Person>) {
                                val adapter = PersonListAdapter(people, this@SearchPersonActivity)
                                searchResultList.setAdapter(adapter)
                                searchResultList.addItemDecoration(itemDecorator)
                                searchResultList.setLayoutManager(LinearLayoutManager(this@SearchPersonActivity))
                                progressBar.visibility = View.GONE
                            }

                            override fun onError(e: Throwable) {
                                progressBar.visibility = View.GONE
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, e.localizedMessage)
                                    e.printStackTrace()
                                }
                            }

                            override fun onComplete() {
                                progressBar.visibility = View.GONE
                            }
                        })
                }
            }
        }
        searchText.addTextChangedListener(textWatcher)
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


    override fun onPersonClicked(person: Person?) {
        var data = Intent();
        data.putExtra(Constants.SEARCH_RESULT_PERSON_KEY, person);
        setResult(Activity.RESULT_OK, data)
        finish();
    }

    override fun onPersonClicked(person: SearchPerson?) {}
    override fun onShowSearchResult(searchResultList: MutableList<SearchPerson>?) {}
}
