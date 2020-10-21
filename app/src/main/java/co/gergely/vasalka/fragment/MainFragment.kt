package co.gergely.vasalka.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.MainActivity
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.inerfaces.OnClickNews
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.inerfaces.OnHandleTender
import co.gergely.vasalka.model.*
import co.gergely.vasalka.tools.Utils
import com.bumptech.glide.Glide
import java.util.*


class MainFragment : Fragment() {

    private val TAG = "MainFragment";
    private val debug = true
    private var onClickNewsListener: OnClickNews? = null
    private var onTenderListener: OnHandleTender? = null
    private var onPersonListener: OnHandlePerson? = null
    private lateinit var combinedList: LinearLayout
    private lateinit var tenderList: List<Tender>;
    private lateinit var satisfactionList: List<Satisfaction>
    private lateinit var noContentTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)

        noContentTextView = v.findViewById(R.id.noContentTextView)
        combinedList = v.findViewById(R.id.combinedList)

        // NEWS
        if (!VasalkaApp.getSession().newsList.isEmpty()) {
            updateNewsList(VasalkaApp.getSession().newsList)
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "NEWS NOT EMPTY")
            }
            if (VasalkaApp.getSession().myPerson.isHasService) { //Szolgáltató
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "SZOLGÁLTATÓ")
                }
                if (!VasalkaApp.getSession().tenderList.isEmpty()) {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "TENDER NOT EMPTY")
                    }
                    updateTenderList(VasalkaApp.getSession().tenderList)
                }

                if (!VasalkaApp.getSession().satisfactionList.isEmpty()) {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "SATISFACTION NOT EMPTY")
                    }
                    updateSatisfactionList(VasalkaApp.getSession().satisfactionList)
                } else if (VasalkaApp.getSession().tenderList.isEmpty()) {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "HIDE")
                    }
                    noContentTextView.visibility = View.VISIBLE
                }
            } else { // Ügyfél
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "ÜGYFÉL")
                }
                if (!VasalkaApp.getSession().providerList.isEmpty()) {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "SZOLGÁLTATAÓ LISTA NEM ÜRES")
                    }
                    updateProviderList(VasalkaApp.getSession().providerList)
                } else {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "HIDE")
                    }
                    noContentTextView.visibility = View.VISIBLE
                }
            }
        }

        /*
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                token = it.getResult()!!.token;
                var newsService = NetworkClient.getRetrofit(token).create(ApiNews::class.java)
                newsService.getList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        updateNewsList(it)
                    }, {
                        if (BuildConfig.DEBUG && debug) {
                            it.printStackTrace()
                        }
                    })
            }
        }

        if (VasalkaApp.getSession().myPerson.isHasService) {// Szolgáltató
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "SZOLGÁLTATÓ")
            }
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    token = it.getResult()!!.token;
                    var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java)
                    tenderService.getAffectedTenderList(VasalkaApp.getSession().myPerson.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.isEmpty()) { // NINCS tender
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "NINCS Tender")
                                }
                                var satisfactionService =
                                    NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java)
                                satisfactionService.getSatisfactionsFor(VasalkaApp.getSession().myPerson.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it.isEmpty()) {
                                            if (BuildConfig.DEBUG && debug) {
                                                Log.d(TAG, "NINCS ÉRTÉKELÉS")
                                            }
                                            hideList()
                                        } else { // Van satisfaction
                                            if (BuildConfig.DEBUG && debug) {
                                                Log.d(TAG, "VAN ÉRTÉKELÉS")
                                            }
                                            updateSatisfactionList(it)
                                        }
                                    }, {
                                        if (BuildConfig.DEBUG && debug) {
                                            it.printStackTrace()
                                        }
                                    })
                            } else { // VAN tender
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "VAN Tender")
                                }

                                updateTenderList(it)

                                var satisfactionService =
                                    NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java)
                                satisfactionService.getSatisfactionsFor(VasalkaApp.getSession().myPerson.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (!it.isEmpty()) { // VAN értékelés
                                            if (BuildConfig.DEBUG && debug) {
                                                Log.d(TAG, "VAN ÉRTÉKELÉS")
                                            }
                                            updateSatisfactionList(it)
                                        }
                                    }, {
                                        if (BuildConfig.DEBUG && debug) {
                                            it.printStackTrace()
                                        }
                                    })
                            }
                        }, {
                            if (BuildConfig.DEBUG && debug) {
                                it.printStackTrace()
                            }
                        })
                }
            }
        } else {// Ügyfél
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "ÜGYFÉL")
            }
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    token = it.getResult()!!.token;
                    var searchService = NetworkClient.getRetrofit(token).create(ApiSearch::class.java);

                    searchService.getListFor(VasalkaApp.getSession().myPerson.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.isEmpty()) {// NINCS Szolgáltató
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "NINCS SZOLGÁLTATÓ")
                                }
                                hideList()
                            } else {// VAN szolgáltató
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "VAN SZOLGÁLTATÓ")
                                }
                                // Szolgáltató listát mutatni
                                for (person in it) {
                                    combinedList.addView(buildServicerView(person))
                                    combinedList.addView(buildSpaceView())
                                }
                            }
                        }, {
                            if (BuildConfig.DEBUG && debug) {
                                it.printStackTrace()
                            }
                        })
                }
            }
        }
        */

        return v;
    }

    fun buildSpaceView(): View {
        var params = LinearLayout.LayoutParams(Utils.pxToDp(activity!!, 8), Utils.pxToDp(activity!!, 8))
        var v = View(activity!!)
        v.layoutParams = params
        return v;
    }

    fun updateNewsList(list: List<News>) {
        // CLEAR THE LIST
        combinedList.removeAllViews()

        for (news in list) {
            if (news.lang.toUpperCase().equals(Locale.getDefault().getLanguage().toUpperCase())) {
                combinedList.addView(buildNewsView(news))
                combinedList.addView(buildSpaceView())
            }
        }
    }

    fun updateTenderList(list: List<Tender>) {
        if (list.size > 5) {
            tenderList = list.subList(0, 4)
        } else {
            tenderList = list;
        }
        for (tender in tenderList) {
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "Tender: " + tender.person.name)
            }
            combinedList.addView(buildTenderView(tender))
            combinedList.addView(buildSpaceView())
        }
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "Tender Views Added")
        }
    }

    fun updateSatisfactionList(list: List<Satisfaction>) {
        if (list.size >= 3) {
            satisfactionList = list.subList(0, 3)
        } else {
            satisfactionList = list;
        }
        for (satisfaction in satisfactionList) {
            combinedList.addView(buildSatisfactionView(satisfaction))
            combinedList.addView(buildSpaceView())
        }
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "Satisfaction Views Added")
        }
    }

    fun updateProviderList(list: List<SearchPerson>) {
        for (person in list) {
            combinedList.addView(buildServicerView(person))
            combinedList.addView(buildSpaceView())
        }
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "Provider Views Added")
        }
    }

    fun buildSatisfactionView(satisfaction: Satisfaction): View {
        // val v = layoutInflater.inflate(R.layout.item_satisfaction_profile, null)
        val v = layoutInflater.inflate(R.layout.item_main_satisfaction, null)

        val personNameLabel: TextView
        val descTextView: TextView
        val serviceLabel: TextView
        val photo: ImageView
        val vasalasIcon: ImageView
        val mosasIcon: ImageView
        val takaritasIcon: ImageView
        val kerteszkedesIcon: ImageView

        personNameLabel = v.findViewById(R.id.name);
        photo = v.findViewById(R.id.photo)
        descTextView = v.findViewById(R.id.desc);
        vasalasIcon = v.findViewById(R.id.vasalas)
        mosasIcon = v.findViewById(R.id.mosas)
        takaritasIcon = v.findViewById(R.id.takaritas)
        kerteszkedesIcon = v.findViewById(R.id.kerteszkedes)

        vasalasIcon.visibility = View.GONE
        mosasIcon.visibility = View.GONE
        takaritasIcon.visibility = View.GONE
        kerteszkedesIcon.visibility = View.GONE

        when {
            satisfaction.serviceId == Constants.SERVICE_IRONING_ID -> {
                vasalasIcon.visibility = View.VISIBLE
            }
            satisfaction.serviceId == Constants.SERVICE_WASHING_ID -> {
                mosasIcon.visibility = View.VISIBLE
            }
            satisfaction.serviceId == Constants.SERVICE_CLEANING_ID -> {
                takaritasIcon.visibility = View.VISIBLE
            }
            satisfaction.serviceId == Constants.SERVICE_GARDENING_ID -> {
                kerteszkedesIcon.visibility = View.VISIBLE
            }
        }

        if (VasalkaApp.getSession().myPerson.id === satisfaction.dstPerson.id) {
            personNameLabel.text = satisfaction.srcPerson.name
            Glide.with(photo).load(satisfaction.srcPerson.photo).into(photo)
        } else {
            personNameLabel.text = satisfaction.dstPerson.name
            Glide.with(photo).load(satisfaction.dstPerson.photo).into(photo)
        }

        tintStars(v, satisfaction.rate)
        descTextView.text = satisfaction.description

        v.setOnClickListener {
            (activity!! as MainActivity).goToSatisfactionList()
        }

        return v;
    }

    fun buildNewsView(news: News): View {
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "buildNewsView STARTED")
        }

        val v = layoutInflater.inflate(R.layout.item_main_news, null)
        var title = v.findViewById<TextView>(R.id.sectionTitle)
        var intro = v.findViewById<TextView>(R.id.intro)
        var date = v.findViewById<TextView>(R.id.date)

        date.setText(news.getCreatedAt());
        title.setText(news.getTitle());
        intro.setText(news.getIntro());

        v.setOnClickListener(View.OnClickListener() {
            onClickNewsListener!!.onClickNews(news.id)
        })

        return v;

    }

    fun buildTenderView(tender: Tender): View {
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "buildTenderView STARTED tender: " + tender)
        }
        val v = layoutInflater.inflate(R.layout.item_main_tender, null)

        val photo = v.findViewById<ImageView>(R.id.photo);
        val city = v.findViewById<TextView>(R.id.city)
        val content = v.findViewById<TextView>(R.id.content)
        val vasalasIcon = v.findViewById<ImageView>(R.id.vasalas)
        val mosasIcon = v.findViewById<ImageView>(R.id.mosas)
        val takaritasIcon = v.findViewById<ImageView>(R.id.takaritas)
        val kerteszkedesIcon = v.findViewById<ImageView>(R.id.kerteszkedes)

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "buildTenderView All View component initiated")
        }

        vasalasIcon.visibility = View.GONE
        mosasIcon.visibility = View.GONE
        takaritasIcon.visibility = View.GONE
        kerteszkedesIcon.visibility = View.GONE

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "buildTenderView SERVICE ICONS HIDE")
        }

        city.setText(tender.getCity().getName() + " + " + tender.getServiceArea() + "km");
        content.setText(tender.description)

        for (service in tender.getServiceList()) {
            if (service.getId().equals(Constants.SERVICE_IRONING_ID)) {
                vasalasIcon.visibility = View.VISIBLE
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "buildTenderView VASALAS ICON SHOW")
                }
            }
            if (service.getId().equals(Constants.SERVICE_WASHING_ID)) {
                mosasIcon.visibility = View.VISIBLE
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "buildTenderView MOSAS ICON SHOW")
                }
            }
            if (service.getId().equals(Constants.SERVICE_CLEANING_ID)) {
                takaritasIcon.visibility = View.VISIBLE
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "buildTenderView TAKARITAS ICON SHOW")
                }
            }
            if (service.getId().equals(Constants.SERVICE_GARDENING_ID)) {
                kerteszkedesIcon.visibility = View.VISIBLE
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "buildTenderView KERTESZKEDES ICON SHOW")
                }
            }
        }

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "buildTenderView PERSON PHOTO START")
        }

        Glide.with(activity!!).load(tender.person.photo).into(photo)

        v.setOnClickListener(View.OnClickListener() {
            onTenderListener!!.onTenderClicked(tender)
        })

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "buildTenderView ON CLICK LISTENER SET\n RETURN")
        }

        return v
    }

    fun buildServicerView(person: SearchPerson): View {
        val v = layoutInflater.inflate(R.layout.item_main_service, null)
        val personCard: CardView

        val photo: ImageView
        val name: TextView
        val city: TextView

        val serviceIronig: ImageView
        val serviceWashing: ImageView
        val serviceCleaning: ImageView
        val serviceGardening: ImageView
        val scoreLabel: TextView

        personCard = v.findViewById(R.id.card)

        photo = v.findViewById(R.id.photo)
        name = v.findViewById(R.id.name)
        city = v.findViewById(R.id.city)
        scoreLabel = v.findViewById(R.id.scoreLabel)

        serviceIronig = v.findViewById(R.id.vasalas)
        serviceWashing = v.findViewById(R.id.mosas)
        serviceCleaning = v.findViewById(R.id.takaritas)
        serviceGardening = v.findViewById(R.id.kerteszkedes)

        Glide.with(photo).load(person.photo).into(photo)
        name.setText(person.name)
        city.setText(person.city.name)
        tintStars(v, person.satisfactionSummary)
        scoreLabel.setText("( " + person.satisfactionSummary.avg + "/" + person.satisfactionSummary.count + " )")

        if (person.distance > 0.0) {
            city.setText(
                person.city.name + " " + Math.round(person.distance).toInt().toString() + activity!!.getString(
                    R.string.distanceEndLabel
                )
            )
        }

        if (!Utils.hasService(person, Constants.SERVICE_IRONING_ID)) {
            serviceIronig.setVisibility(View.GONE)
        }
        if (!Utils.hasService(person, Constants.SERVICE_WASHING_ID)) {
            serviceWashing.setVisibility(View.GONE)
        }
        if (!Utils.hasService(person, Constants.SERVICE_CLEANING_ID)) {
            serviceCleaning.setVisibility(View.GONE)
        }
        if (!Utils.hasService(person, Constants.SERVICE_GARDENING_ID)) {
            serviceGardening.setVisibility(View.GONE)
        }

        personCard.setOnClickListener(View.OnClickListener {
            onPersonListener!!.onPersonClicked(person)
        })

        return v;
    }

    private fun tintStars(v: View, score: Int) {

        val star1 = v.findViewById<ImageView>(R.id.star1)
        val star2 = v.findViewById<ImageView>(R.id.star2)
        val star3 = v.findViewById<ImageView>(R.id.star3)
        val star4 = v.findViewById<ImageView>(R.id.star4)
        val star5 = v.findViewById<ImageView>(R.id.star5)

        if (score == null) {
            return
        } else if (score < 2) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (score >= 2 && score < 3) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (score >= 3 && score < 4) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star3.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (score >= 4 && score < 5) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star3.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star4.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star3.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star4.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star5.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )

        }

    }

    private fun tintStars(v: View, summary: SatisfactionSummary) {
        val star1 = v.findViewById<ImageView>(R.id.star1)
        val star2 = v.findViewById<ImageView>(R.id.star2)
        val star3 = v.findViewById<ImageView>(R.id.star3)
        val star4 = v.findViewById<ImageView>(R.id.star4)
        val star5 = v.findViewById<ImageView>(R.id.star5)

        if (summary.avg == null) {
            return
        } else if (summary.avg < 2) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (summary.avg >= 2 && summary.avg < 3) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (summary.avg >= 3 && summary.avg < 4) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star3.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else if (summary.avg >= 4 && summary.avg < 5) {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star3.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star4.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            star1.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star2.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star3.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star4.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            star5.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.smsBtn),
                android.graphics.PorterDuff.Mode.SRC_IN
            )

        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnClickNews) {
            onClickNewsListener = context
        } else {
            throw java.lang.RuntimeException(context.toString() + "must implement onClickNews")
        }

        if (context is OnHandleTender) {
            onTenderListener = context
        } else {
            throw java.lang.RuntimeException(context.toString() + "must implement OnHandleTender")
        }

        if (context is OnHandlePerson) {
            onPersonListener = context
        } else {
            throw java.lang.RuntimeException(context.toString() + "must implement OnHandlePerson")
        }


    }

    override fun onDetach() {
        super.onDetach()
        onClickNewsListener = null
    }
}
