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
import co.gergely.vasalka.adapter.TenderAdapter
import co.gergely.vasalka.api.ApiTender
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.inerfaces.OnHandleTender
import co.gergely.vasalka.model.Tender
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tender_list.*


class TenderListFragment : Fragment() {

    private var listener: OnHandleTender? = null
    private lateinit var tenderListRecycler: RecyclerView
    private lateinit var noResultsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_tender_list, container, false)

        tenderListRecycler = v.findViewById<RecyclerView>(R.id.tenderListRecycler)
        var progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        noResultsTextView = v.findViewById(R.id.noResultsText)

        progressBar.visibility = View.VISIBLE

        if (!VasalkaApp.getSession().tenderList.isEmpty()) {
            updateUI(VasalkaApp.getSession().tenderList)
            progressBar.visibility = View.GONE
        }


        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java)

                tenderService.getAffectedTenderList(VasalkaApp.getSession().myPerson.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.size > 0) {
                            VasalkaApp.getSession().tenderList = it
                            updateUI(it)
                        } else {
                            noResultsText.visibility = View.VISIBLE
                        }
                        progressBar.visibility = View.GONE
                    }, {
                        Utils.unknownError(activity!!)
                    });
            }
        }

        return v;
    }

    fun updateUI(list: List<Tender>) {

        var itemDecorator = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL);
        AppCompatResources.getDrawable(activity!!, R.drawable.item_decorator)?.let { itemDecorator.setDrawable(it) }

        var adapter = TenderAdapter(listener, list)
        tenderListRecycler.adapter = adapter
        tenderListRecycler.addItemDecoration(itemDecorator)
        tenderListRecycler.setLayoutManager(LinearLayoutManager(activity));
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandleTender) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandleTender")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}
