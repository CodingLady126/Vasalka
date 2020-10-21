package co.gergely.vasalka.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.adapter.SatisfactionAdapter
import co.gergely.vasalka.api.ApiSatisfaction
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.model.Satisfaction
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SatisfactionListFragment : Fragment() {

    private val TAG = "SatisfactionFrgmnt"
    private val debug = true;
    private lateinit var satisfactionRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_satisfaction_list, container, false)

        satisfactionRecycler = v.findViewById(R.id.satisfactionFrgmntRecycler)
        progressBar = v.findViewById(R.id.progressBar)
        noResult = v.findViewById(R.id.noResultsTextView)

        if (!VasalkaApp.getSession().satisfactionList.isEmpty()) {
            updateUi(VasalkaApp.getSession().satisfactionList)
            progressBar.visibility = View.GONE
        }

        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var satisfactionService = NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java);
                progressBar.visibility = View.VISIBLE
                satisfactionService.getSatisfactionsFor(VasalkaApp.getSession().myPerson.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        progressBar.visibility = View.VISIBLE
                        if (it.size > 0) {
                            updateUi(it)
                            progressBar.visibility = View.GONE
                        } else {
                            noResult.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
                    }, {
                        if (BuildConfig.DEBUG && debug) {
                            it.printStackTrace()
                        }
                        progressBar.visibility = View.GONE
                        noResult.visibility = View.VISIBLE
                        Utils.unknownError(activity!!)
                    })
            }
        }

        return v;
    }


    fun updateUi(list: List<Satisfaction>) {
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "updateui: List size: " + list.size)
        }

        val itemDecorator = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        AppCompatResources.getDrawable(activity!!, R.drawable.item_decorator)
            ?.let { itemDecorator.setDrawable(it) }

        var adapter = SatisfactionAdapter(list, VasalkaApp.getSession().myPerson)
        satisfactionRecycler.adapter = adapter
        satisfactionRecycler.addItemDecoration(itemDecorator)
        satisfactionRecycler.setLayoutManager(LinearLayoutManager(activity));
        adapter.notifyDataSetChanged()
    }


}
