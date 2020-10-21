package co.gergely.vasalka.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.adapter.PersonServiceAdapter
import co.gergely.vasalka.api.ApiTender
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.dialog.ShowImageDialog
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.inerfaces.OnHandleTender
import co.gergely.vasalka.model.Tender
import co.gergely.vasalka.tools.Utils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "tender"

class TenderShowFragment : Fragment() {
    private var tender: Tender? = null

    private var listener: OnHandleTender? = null
    private var personListener: OnHandlePerson? = null

    private val debug = true
    private val TAG = "TenderShowFrgmnt"

    private lateinit var profileImage: CircleImageView
    private lateinit var profileName: TextView
    private lateinit var tenderCity: TextView
    private lateinit var tenderDate: TextView
    private lateinit var tenderDesc: TextView
    private lateinit var tenderAlerted: TextView
    private lateinit var tenderSaw: TextView
    private lateinit var tenderApplied: TextView
    private lateinit var ironingImage: ImageView
    private lateinit var washingImage: ImageView
    private lateinit var cleaningImage: ImageView
    private lateinit var gardeningImage: ImageView
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var applyBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var bookmarkImgBtn: ImageView
    private lateinit var appliedLayout: LinearLayout
    private lateinit var appliedRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tender = it.get(ARG_PARAM1) as Tender
        }
    }

    fun init(v: View) {
        profileImage = v.findViewById(R.id.profileImage)
        profileName = v.findViewById(R.id.profileName)
        tenderCity = v.findViewById(R.id.tenderCity)
        tenderDate = v.findViewById(R.id.tenderDate)
        tenderDesc = v.findViewById(R.id.tenderDescription)
        tenderAlerted = v.findViewById(R.id.tenderAlerted)
        tenderSaw = v.findViewById(R.id.tenderSaw)
        tenderApplied = v.findViewById(R.id.tenderApplied)
        ironingImage = v.findViewById(R.id.ironingImage)
        washingImage = v.findViewById(R.id.washingImage)
        cleaningImage = v.findViewById(R.id.cleaningImage)
        gardeningImage = v.findViewById(R.id.gardeningImage)
        image1 = v.findViewById(R.id.image1)
        image2 = v.findViewById(R.id.image2)
        image3 = v.findViewById(R.id.image3)
        applyBtn = v.findViewById(R.id.tenderApplyBtn)
        progressBar = v.findViewById(R.id.progressBar)
        bookmarkImgBtn = v.findViewById(R.id.bookmarkImgBtn)
        appliedLayout = v.findViewById(R.id.appliedLayout)
        appliedRecycler = v.findViewById(R.id.appliedRecycler)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_tender_show, container, false)
        init(v)
        setUpImageViews()
        profileName.text = tender!!.person.name
        tenderDate.text = tender!!.createdAt
        tenderCity.text = tender!!.city.name
        tenderDesc.text = tender!!.description

        profileName.setOnClickListener {
            personListener!!.onPersonClicked(tender!!.person)
        }

        profileImage.setOnClickListener {
            personListener!!.onPersonClicked(tender!!.person)
        }

        if (tender!!.person.id == VasalkaApp.getSession().myPerson.id) {
            applyBtn.visibility = View.GONE
            appliedLayout.visibility = View.VISIBLE
        } else {
            applyBtn.visibility = View.VISIBLE
            appliedLayout.visibility = View.GONE
        }

        applyBtn.setOnClickListener {
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java);
                    tenderService.applyForTender(tender!!.id).enqueue(object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            //TODO AlertDialog

                        }

                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                //TODO AlertDialog
                            }
                        }
                    })
                }
            }
        }

        bookmarkImgBtn.setOnClickListener(View.OnClickListener {
            var dialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.addTenderToBookmark)
                .setMessage(R.string.areYouSure)
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "TENDER: " + tender!!)
                    }
                    Utils.addTenderToMyBookmark(activity!!, tender!!)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        })

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tender!!.person.id.equals(VasalkaApp.getSession().myPerson.id)) {
            progressBar.visibility = View.VISIBLE
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java);

                    tenderService.getAlertSummary(tender!!.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            tenderAlerted.visibility = View.VISIBLE
                            tenderSaw.visibility = View.VISIBLE
                            tenderApplied.visibility = View.VISIBLE

                            tenderAlerted.text = activity!!.getString(R.string.tenderAlerted, it.alerted)
                            tenderSaw.text = activity!!.getString(R.string.tenderSaw, it.saw)
                            tenderApplied.text = activity!!.getString(R.string.tenderApplied, it.applied)
                            progressBar.visibility = View.GONE
                        }, {
                            progressBar.visibility = View.GONE
                            Utils.unknownError(activity!!)
                        })

                    val itemDecorator = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
                    itemDecorator.setDrawable(resources.getDrawable(R.drawable.item_decorator))

                    tenderService.getAppliedPersonList(tender!!.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            val adapter = PersonServiceAdapter(activity!!, it, personListener)
                            appliedRecycler.adapter = adapter
                            appliedRecycler.addItemDecoration(itemDecorator)
                            appliedRecycler.layoutManager = LinearLayoutManager(activity)
                        }, {
                            if (BuildConfig.DEBUG || debug) {
                                Log.d(TAG, "Applied list error: " + it.localizedMessage)
                                it.printStackTrace()
                            }
                            Utils.unknownError(activity!!)
                        })
                }
            }
        } else {
            tenderAlerted.visibility = View.GONE
            tenderSaw.visibility = View.GONE
            tenderApplied.visibility = View.GONE
        }
    }

    fun setUpImageViews() {
        Glide.with(activity!!).load(tender!!.person.photo).into(profileImage)

        if (tender!!.image1 != null && !tender!!.image1.isEmpty()) {
            image1.visibility = View.VISIBLE
            Glide.with(activity!!).load(tender!!.image1).into(image1)
            image1.setOnClickListener {
                ShowImageDialog(activity!!, tender!!.image1).show()
            }
        }
        if (tender!!.image2 != null && !tender!!.image2.isEmpty()) {
            image2.visibility = View.VISIBLE
            Glide.with(activity!!).load(tender!!.image2).into(image2)
            image2.setOnClickListener {
                ShowImageDialog(activity!!, tender!!.image2).show()
            }
        }
        if (tender!!.image3 != null && !tender!!.image3.isEmpty()) {
            image3.visibility = View.VISIBLE
            Glide.with(activity!!).load(tender!!.image3).into(image3)
            image3.setOnClickListener {
                ShowImageDialog(activity!!, tender!!.image3).show()
            }
        }

        for (service in tender!!.getServiceList()) {
            if (service.id.equals(Constants.SERVICE_IRONING_ID)) {
                ironingImage.visibility = View.VISIBLE
            }
            if (service.id.equals(Constants.SERVICE_WASHING_ID)) {
                washingImage.visibility = View.VISIBLE
            }
            if (service.id.equals(Constants.SERVICE_CLEANING_ID)) {
                cleaningImage.visibility = View.VISIBLE
            }
            if (service.id.equals(Constants.SERVICE_GARDENING_ID)) {
                gardeningImage.visibility = View.VISIBLE
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandleTender) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandleTender")
        }

        if (context is OnHandlePerson) {
            personListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandlePerson")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param tender Parameter 1.
         * @return A new instance of fragment TenderShowFragment.
         */
        @JvmStatic
        fun newInstance(tender: Tender) =
            TenderShowFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, tender)
                }
            }
    }
}
