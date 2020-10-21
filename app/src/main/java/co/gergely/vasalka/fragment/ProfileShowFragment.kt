package co.gergely.vasalka.fragment

import android.animation.Animator
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.adapter.SatisfactionAdapter
import co.gergely.vasalka.api.ApiChat
import co.gergely.vasalka.api.ApiSatisfaction
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.dialog.RatePersonDialog
import co.gergely.vasalka.inerfaces.OnHandleChat
import co.gergely.vasalka.model.Person
import co.gergely.vasalka.model.Satisfaction
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class ProfileShowFragment : Fragment() {

    private val TAG = "PrflShowFrgmnt"
    private val debug = true
    private var mCurrentAnimator: Animator? = null
    private var mShortAnimationDuration: Int = 0

    private lateinit var person: Person;
    private lateinit var source: String;
    private val PERSON = "person"
    private var photoCompleted = false;
    private lateinit var mySecurePreferences: MySecurePreferences
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileCity: TextView
    private lateinit var profileBio: TextView
    private lateinit var actionChatBtn: ImageView
    private lateinit var actionTelBtn: ImageView
    private lateinit var actionRateBtn: ImageView
    private lateinit var actionBookmark: ImageView
    private lateinit var profileRateLabel: TextView
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    private lateinit var serviceLayout: ConstraintLayout
    private lateinit var ironingDisabler: View
    private lateinit var washingDisabler: View
    private lateinit var cleaningDisabler: View
    private lateinit var gardeningDisabler: View
    private lateinit var satisfactionRecycler: RecyclerView
    private lateinit var stamp: ImageView
    private lateinit var largeImageView: ImageView
    private lateinit var tenderActionImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var ironingServiceAreaLabel: TextView
    private lateinit var washingServiceAreaLabel: TextView
    private lateinit var cleaningServiceAreaLabel: TextView
    private lateinit var gardeningServiceAreaLabel: TextView


    private var listener: OnTenderListAction? = null
    private var chatListener: OnHandleChat? = null

    companion object {
        fun newInstance(person: Person) = ProfileShowFragment().apply {
            val args: Bundle = Bundle()
            args.putSerializable(PERSON, person)
            val fragment = ProfileShowFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_profile_show, container, false)
        init(v)

        var zoomOut = true
        mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        profileImage.setOnClickListener {
            if (zoomOut) {
                profileImage
                    .animate()
                    .scaleX(2f)
                    .scaleY(2f)
                    .setInterpolator(AccelerateInterpolator())
                    .setDuration(500).start()
                zoomOut = false
            } else {
                profileImage
                    .animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(AccelerateInterpolator())
                    .start()
                zoomOut = true
            }
        }

        if (person.tel != null && !person.tel.isEmpty()) {
            actionTelBtn.setOnClickListener {
                var intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + person.tel));
                try {
                    activity!!.startActivity(intent)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG && debug) {
                        e.printStackTrace()
                    }
                    actionTelBtn.isEnabled = false
                }
            }
        } else {
            actionTelBtn.isEnabled = false
        }

        actionRateBtn.setOnClickListener {
            var dialog = RatePersonDialog(context, person)
            dialog.show()
        }

        if (person.id == VasalkaApp.getSession().myPerson.id) {
            tenderActionImageView.setOnClickListener {
                listener!!.onTenderListAction()
            }
        } else {
            tenderActionImageView.visibility = View.GONE
        }

        actionBookmark.setOnClickListener {
            var dialog = AlertDialog.Builder(context)
                .setTitle(R.string.alertTitleAttention)
                .setMessage(R.string.doYouWantToAddToBookmark)
                .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    Utils.addPersonToMyBookmark(activity!!, person)
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .create()
            dialog.show()
        }

        actionChatBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var chatService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)
                    chatService.getRoomByPartner(VasalkaApp.getSession().myPerson.id, person.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            progressBar.visibility = View.GONE
                            chatListener!!.onChatOpen(it)
                        }, {
                            if (BuildConfig.DEBUG && debug) {
                                Log.d(TAG, "Error: " + it.localizedMessage)
                                it.printStackTrace()
                            }
                            progressBar.visibility = View.GONE
                            Utils.unknownError(activity!!)
                        })
                }
            }
        }

        updateUi()
        return v
    }

    fun init(v: View) {
        person = arguments?.getSerializable(PERSON) as Person
        mySecurePreferences = MySecurePreferences(activity!!.application)
        profileImage = v.findViewById(R.id.profileImage)
        profileName = v.findViewById(R.id.profileName)
        profileCity = v.findViewById(R.id.profileCity)
        actionChatBtn = v.findViewById(R.id.profileChatActionImageView)
        actionTelBtn = v.findViewById(R.id.profileCallActionImageView)
        actionRateBtn = v.findViewById(R.id.profileRateActionImageView)
        actionBookmark = v.findViewById(R.id.profileShowActionBookmark)
        profileRateLabel = v.findViewById(R.id.countRateLabel)
        profileBio = v.findViewById(R.id.profileBio)
        star1 = v.findViewById(R.id.profileStar1)
        star2 = v.findViewById(R.id.profileStar2)
        star3 = v.findViewById(R.id.profileStar3)
        star4 = v.findViewById(R.id.profileStar4)
        star5 = v.findViewById(R.id.profileStar5)
        serviceLayout = v.findViewById(R.id.serviceLayout)
        ironingDisabler = v.findViewById(R.id.ironing_disabler)
        washingDisabler = v.findViewById(R.id.washnig_disabler)
        cleaningDisabler = v.findViewById(R.id.cleaning_disabler)
        gardeningDisabler = v.findViewById(R.id.gardening_disabler)
        satisfactionRecycler = v.findViewById(R.id.satisfactionRecycler)
        largeImageView = v.findViewById(R.id.largeImageView)
        stamp = v.findViewById(R.id.stamp)
        tenderActionImageView = v.findViewById(R.id.profileTenderActionImageView)
        progressBar = v.findViewById(R.id.progressBar)
        ironingServiceAreaLabel = v.findViewById(R.id.ironing_service_area)
        washingServiceAreaLabel = v.findViewById(R.id.washing_service_area)
        cleaningServiceAreaLabel = v.findViewById(R.id.cleaning_service_area)
        gardeningServiceAreaLabel = v.findViewById(R.id.gardening_service_area)
    }

    private fun updateUi() {
        loadProfilePhoto()

        profileName.text = person.name
        if (person.city != null) {
            profileCity.text = person.city.name
        }
        profileBio.text = person.bio

        ironingServiceAreaLabel.text = activity!!.getString(R.string.serviceAreaTextKm, person.serviceArea)
        cleaningServiceAreaLabel.text = activity!!.getString(R.string.serviceAreaTextKm, person.serviceArea)
        washingServiceAreaLabel.text = activity!!.getString(R.string.serviceAreaTextKm, person.serviceArea)
        gardeningServiceAreaLabel.text = activity!!.getString(R.string.serviceAreaTextKm, person.serviceArea)

        //Satisfaction label and stars
        if (person.satisfactionSummary == null || person.satisfactionSummary.avg == null) {
            profileRateLabel.text = activity?.getString(R.string.show_profile_count_rate_empty)
        } else {
            profileRateLabel.text =
                "" + person.satisfactionSummary.avg + " / (" + person.satisfactionSummary.count + ")";

            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "SATISFACTION LIST")
            }

            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var satisfactionService = NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java);

                    satisfactionService.getSatisfactionsFor(person.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            createSatisfactionRecyclerList(it)
                        }, {
                            if (BuildConfig.DEBUG && debug) {
                                Log.d(TAG, "Satisfaction list: " + it.localizedMessage)
                            }
                            it.printStackTrace()
                            Utils.unknownError(activity!!)
                        });
                }
            }

            if (person.satisfactionSummary.avg > 1 && person.satisfactionSummary.avg < 2) {
                star1.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (person.satisfactionSummary.avg > 2 && person.satisfactionSummary.avg < 3) {
                star1.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star2.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (person.satisfactionSummary.avg > 3 && person.satisfactionSummary.avg < 4) {
                star1.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star2.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star3.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (person.satisfactionSummary.avg > 4 && person.satisfactionSummary.avg < 5) {
                star1.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star2.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star3.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star4.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (person.satisfactionSummary.avg == 5.0) {
                star1.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star2.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star3.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star4.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                star5.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.smsBtn),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            }


        }


        //Service List
        if (person.serviceList == null || person.serviceList.isEmpty()) {
            serviceLayout.visibility = View.GONE

            if (Locale.getDefault().getLanguage().equals("hu")) {
                stamp.setImageResource(R.mipmap.client_stamp_hu)
            } else {
                stamp.setImageResource(R.mipmap.client_stamp_en)
            }
        } else {
            for (service in person.serviceList) {
                if (service.id == Constants.SERVICE_IRONING_ID) {
                    ironingDisabler.visibility = View.GONE
                }
                if (service.id == Constants.SERVICE_WASHING_ID) {
                    washingDisabler.visibility = View.GONE
                }
                if (service.id == Constants.SERVICE_CLEANING_ID) {
                    cleaningDisabler.visibility = View.GONE
                }
                if (service.id == Constants.SERVICE_GARDENING_ID) {
                    gardeningDisabler.visibility = View.GONE
                }
            }

            if (Locale.getDefault().getLanguage().equals("hu")) {
                stamp.setImageResource(R.mipmap.service_stamp_hu)
            } else {
                stamp.setImageResource(R.mipmap.service_stamp_en)
            }
        }
        stamp.rotation = -40f
        var animation = AnimationUtils.loadAnimation(activity, R.anim.stamp)
        animation.interpolator = AccelerateInterpolator()
        addShake(stamp, animation)
        stamp.animation = animation
    }

    private fun createSatisfactionRecyclerList(list: List<Satisfaction>) {
        var itemDecorator = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        AppCompatResources.getDrawable(activity!!, R.drawable.item_decorator)?.let { itemDecorator.setDrawable(it) }

        var satisfactionAdapter = SatisfactionAdapter(list, person)
        satisfactionRecycler.adapter = satisfactionAdapter
        satisfactionRecycler.addItemDecoration(itemDecorator)
        satisfactionRecycler.setLayoutManager(LinearLayoutManager(activity));
    }

    private fun addShake(image: ImageView, animation: Animation) {

        var shake = AnimationUtils.loadAnimation(activity, R.anim.stamp_shake)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                image.startAnimation(shake)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })

    }

    private fun loadProfilePhoto() {
        var glideRequestListener = object : RequestListener<Drawable> {

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                if (BuildConfig.DEBUG && debug) {
                    e?.printStackTrace()
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                photoCompleted = true
                return false
            }
        }

        if (person.photo == null || person.photo.isEmpty()) {
            when {
                mySecurePreferences.getInt(Constants.SIGN_TYPE_KEY, 0) == Constants.SIGN_TYPE_GOOGLE -> {

                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "GLIDE URL: " + FirebaseAuth.getInstance().currentUser?.photoUrl)
                    }

                    Glide.with(this)
                        .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                        .listener(glideRequestListener)
                        .into(profileImage)
                }
                mySecurePreferences.getInt(Constants.SIGN_TYPE_KEY, 0) == Constants.SIGN_TYPE_FACEBOOK -> {
                    var user = FirebaseAuth.getInstance().currentUser
                    var facebookUserId = ""
                    for (profile in user!!.getProviderData()) {
                        // check if the provider id matches "facebook.com"
                        if (FacebookAuthProvider.PROVIDER_ID == profile.getProviderId()) {
                            facebookUserId = profile.getUid()
                        }
                    }
                    if (!facebookUserId.isEmpty()) {
                        val photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500"
                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "GLIDE FACEBOOK URL: " + photoUrl)
                        }
                        Glide.with(this)
                            .load(photoUrl)
                            .listener(glideRequestListener)
                            .into(profileImage)
                    } else {

                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "GLIDE URL: " + FirebaseAuth.getInstance().currentUser?.photoUrl)
                        }

                        Glide.with(this)
                            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                            .listener(glideRequestListener)
                            .into(profileImage)
                    }
                }
            }
        } else {
            var url = Utils.getImageUrl(person.photo)
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "GLIDE URL: " + url)
            }
            Glide.with(this).load(url).listener(glideRequestListener).into(profileImage)

            photoCompleted = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTenderListAction) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnTenderListAction")
        }

        if (context is OnHandleChat) {
            chatListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandleChat")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        chatListener = null
    }

    interface OnTenderListAction {
        fun onTenderListAction()
    }

}
