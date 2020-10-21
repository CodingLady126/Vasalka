package co.gergely.vasalka.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.MainActivity
import co.gergely.vasalka.adapter.PersonServiceAdapter
import co.gergely.vasalka.api.ApiSearch
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ServicerListFragment : Fragment() {

    private var listener: OnHandlePerson? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_servicer_list, container, false)

        val progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        var noResultsTextView = v.findViewById<TextView>(R.id.noResultsTextView);
        var recycler = v.findViewById<RecyclerView>(R.id.servicerRecyclerView);

        noResultsTextView.visibility = View.GONE
        var itemDecorator = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        AppCompatResources.getDrawable(activity!!, R.drawable.item_decorator)?.let { itemDecorator.setDrawable(it) }

        progressBar.visibility = View.VISIBLE;

        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var searchService = NetworkClient.getRetrofit(token).create(ApiSearch::class.java);

                searchService.getListFor(VasalkaApp.getSession().myPerson.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.size > 0) {
                            Collections.sort(it);
                            var adapter =
                                PersonServiceAdapter(activity?.applicationContext, it, activity as MainActivity);
                            recycler.adapter = adapter
                            recycler.addItemDecoration(itemDecorator)
                            recycler.setLayoutManager(LinearLayoutManager(activity));
                            progressBar.visibility = View.GONE;

                        } else {
                            progressBar.visibility = View.GONE;
                            noResultsTextView.visibility = View.VISIBLE
                        }

                    }, {
                        noResultsTextView.visibility = View.VISIBLE
                        recycler.visibility = View.GONE
                        progressBar.visibility = View.GONE;
                        Utils.unknownError(activity!!)
                    })
            }
        }
        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandlePerson) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandlePerson")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}
