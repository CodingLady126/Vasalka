package co.gergely.vasalka.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.MainActivity
import co.gergely.vasalka.api.ApiPerson
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.dialog.PickImageDialog
import co.gergely.vasalka.model.City
import co.gergely.vasalka.model.Person
import co.gergely.vasalka.model.Service
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.xw.repo.BubbleSeekBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MyProfileFragment : Fragment() {

    private val debug = true
    private val TAG = "MyProfileFrgmnt"
    private lateinit var mySecurePreferences: MySecurePreferences
    private lateinit var myPerson: Person
    private lateinit var myImage: ImageView
    private lateinit var addImage: ImageView
    private lateinit var isServiceBtn: ToggleButton
    private lateinit var serviceLayout: ConstraintLayout
    private lateinit var isServiceText: TextView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var nameInfo: ImageView
    private lateinit var citySpinner: Spinner
    private lateinit var cityInfo: ImageView
    private lateinit var emailEditText: TextInputEditText
    private lateinit var emailInfo: ImageView
    private lateinit var telEditText: TextInputEditText
    private lateinit var telInfo: ImageView
    private lateinit var saveBtn: Button
    private lateinit var helpInfo: ImageView
    private lateinit var seekBar: BubbleSeekBar
    private lateinit var cityAdapter: ArrayAdapter<City>
    lateinit var progressBar: ProgressBar
    private lateinit var cityList: List<City>
    private lateinit var pickImageDialog: PickImageDialog
    private lateinit var cleaningBtn: ToggleButton
    private lateinit var ironingBtn: ToggleButton
    private lateinit var washingBtn: ToggleButton
    private lateinit var gardeningBtn: ToggleButton
    private lateinit var bioEditText: EditText
    private var photoCompleted = false
    var photoUrl: String = ""
    private lateinit var alertOnTender: ToggleButton
    private lateinit var alertOnTenderLabel: TextView
    private lateinit var alertOnChat: ToggleButton
    private lateinit var alertOnChatLabel: TextView
    private lateinit var isServiceLabel: TextView

    private lateinit var ironingServiceLabel: TextView
    private lateinit var washingServiceLabel: TextView
    private lateinit var cleaningServiceLabel: TextView
    private lateinit var gardeningServiceLabel: TextView

    private lateinit var tenderInfo: ImageView
    private lateinit var chatInfo: ImageView
    private lateinit var serviceInfo: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.fragment_my_profile, container, false)
        init(v)

        myPerson = VasalkaApp.getSession().myPerson

        cityList = VasalkaApp.getSession().getCityList()
        cityAdapter = ArrayAdapter<City>(context, android.R.layout.simple_spinner_item, cityList)
        citySpinner.adapter = cityAdapter

        addImage.setOnClickListener {
            pickImageDialog.show()
        }

        isServiceBtn.setOnClickListener {
            isServiceClicker()
        }
        isServiceLabel.setOnClickListener {
            if (isServiceBtn.isChecked) {
                isServiceBtn.isChecked = false
            } else {
                isServiceBtn.isChecked = true
            }
            isServiceClicker()
        }


        isServiceBtn.isChecked = myPerson.isHasService

        saveBtn.setOnClickListener {
            if (isValid()) {
                (activity as MainActivity).showLoading(progressBar)

                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "IS SERVICE BUTTON STATUS:" + isServiceBtn.isChecked)

                }

                myPerson.name = nameEditText.text.toString()
                myPerson.email = emailEditText.text.toString()
                myPerson.tel = telEditText.text.toString()
                myPerson.setHasService(isServiceBtn.isChecked)
                myPerson.serviceArea = seekBar.progress as Int
                myPerson.bio = bioEditText.text.toString()
                myPerson.city = citySpinner.selectedItem as City
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "CITY:" + citySpinner.selectedItem)
                }

                if (!isServiceBtn.isChecked) {
                    myPerson.serviceList = ArrayList<Service>()
                    myPerson.bio = ""
                    myPerson.serviceArea = 0
                }

                myPerson.save()
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "SENDING Person:" + myPerson)
                }

                var personService =
                    NetworkClient.getRetrofit(mySecurePreferences.getString(Constants.JWT_TOKEN_KEY, ""))
                        .create(ApiPerson::class.java)
                personService.updatePerson(myPerson.id, myPerson)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.photo = photoUrl
                        VasalkaApp.getSession().myPerson = it
                        VasalkaApp.getSession().myPerson.save()
                        (activity as MainActivity).hideLoading(progressBar)
                        (activity as MainActivity).goToShowProfile(myPerson)
                    }, {
                        if (BuildConfig.DEBUG) {
                            it.printStackTrace()
                            Log.w("GETJWT", "RETROFIT ERROR:" + it.localizedMessage)
                        }
                        (activity as MainActivity).hideLoading(progressBar)
                        Utils.unknownError(activity!!)
                    })
            }
        }

        alertOnTender.setOnClickListener {
            alertOnTenderClicker(it)
        }
        alertOnTenderLabel.setOnClickListener {
            if (alertOnTender.isChecked) {
                alertOnTender.isChecked = false
            } else {
                alertOnTender.isChecked = true
            }
            alertOnTenderClicker(alertOnTender)
        }

        alertOnChat.setOnClickListener {
            alertForChatClicker(it)
        }
        alertOnChatLabel.setOnClickListener {
            if (alertOnChat.isChecked) {
                alertOnChat.isChecked = false
            } else {
                alertOnChat.isChecked = true
            }
            alertForChatClicker(alertOnChat)
        }

        ironingBtn.setOnClickListener {
            ironingServiceClicker(it)
        }

        ironingServiceLabel.setOnClickListener {
            if (ironingBtn.isChecked) {
                ironingBtn.isChecked = false
            } else {
                ironingBtn.isChecked = true
            }
            ironingServiceClicker(ironingBtn)
        }

        washingBtn.setOnClickListener {
            washingServiceClicker(it)
        }

        washingServiceLabel.setOnClickListener {
            if (washingBtn.isChecked) {
                washingBtn.isChecked = false
            } else {
                washingBtn.isChecked = true
            }
            washingServiceClicker(washingBtn)
        }

        cleaningBtn.setOnClickListener {
            cleaningServiceClicker(it)
        }

        cleaningServiceLabel.setOnClickListener {
            if (cleaningBtn.isChecked) {
                cleaningBtn.isChecked = false
            } else {
                cleaningBtn.isChecked = true
            }
            cleaningServiceClicker(cleaningBtn)
        }

        gardeningBtn.setOnClickListener {
            gardeningServiceClicker(it)
        }

        gardeningServiceLabel.setOnClickListener {
            if (gardeningBtn.isChecked) {
                gardeningBtn.isChecked = false
            } else {
                gardeningBtn.isChecked = true
            }
            gardeningServiceClicker(gardeningBtn)
        }

        nameInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_name_info))
        }
        emailInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_email_info))
        }
        telInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_tel_info))
        }
        helpInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_about))
        }
        cityInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_city_info))
        }
        tenderInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_tender_info))
        }
        chatInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_chat_info))
        }
        serviceInfo.setOnClickListener {
            Utils.easyDialog(activity!!, it, resources.getString(R.string.my_profile_is_Service_info))
        }

        return v
    }

    fun init(v: View) {
        myPerson = VasalkaApp.getSession().myPerson
        mySecurePreferences = MySecurePreferences(activity!!.application)
        citySpinner = v.findViewById(R.id.city)
        nameEditText = v.findViewById(R.id.name)
        nameInfo = v.findViewById(R.id.name_info)
        emailEditText = v.findViewById(R.id.email)
        emailInfo = v.findViewById(R.id.email_info)
        telEditText = v.findViewById(R.id.tel)
        telInfo = v.findViewById(R.id.tel_info)
        saveBtn = v.findViewById(R.id.saveBtn)
        isServiceBtn = v.findViewById(R.id.is_service_image)
        serviceLayout = v.findViewById(R.id.services_layout)
        isServiceText = v.findViewById(R.id.is_service_text)
        helpInfo = v.findViewById(R.id.help_info)
        myImage = v.findViewById(R.id.my_image)
        addImage = v.findViewById(R.id.add_image)
        progressBar = v.findViewById(R.id.progressBar)
        ironingBtn = v.findViewById(R.id.myProfileIronServiceBtn)
        washingBtn = v.findViewById(R.id.myProfileWashingServiceBtn)
        cleaningBtn = v.findViewById(R.id.myProfileCleaningServiceBtn)
        gardeningBtn = v.findViewById(R.id.myProfileGardeningServiceBtn)
        bioEditText = v.findViewById(R.id.bio)
        pickImageDialog = PickImageDialog(activity, Constants.PHOTO_FOR_PERSON)
        alertOnTender = v.findViewById(R.id.alertOnTender)
        alertOnChat = v.findViewById(R.id.alertOnChat)
        tenderInfo = v.findViewById(R.id.my_profile_tender_info)
        chatInfo = v.findViewById(R.id.my_profile_chat_info)
        serviceInfo = v.findViewById(R.id.my_profile_is_Service_info)
        cityInfo = v.findViewById(R.id.city_info)
        alertOnTenderLabel = v.findViewById(R.id.alertOnTenderLabel)
        alertOnChatLabel = v.findViewById(R.id.alertOnChatLabel)
        isServiceLabel = v.findViewById(R.id.is_service_text)
        ironingServiceLabel = v.findViewById(R.id.ironingServiceLabel)
        washingServiceLabel = v.findViewById(R.id.washingServiceLabel)
        cleaningServiceLabel = v.findViewById(R.id.cleaningServiceLabel)
        gardeningServiceLabel = v.findViewById(R.id.gardeningServiceLabel)
        seekBar = v.findViewById(R.id.bubble_seek_bar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    fun updateUI() {
        (activity as MainActivity).showLoading(progressBar)

        if (cityAdapter != null && !cityAdapter.isEmpty) {
            citySpinner.adapter = cityAdapter
        }

        if (myPerson != null) {
            loadProfilePhoto()

            if (myPerson.email != null && !myPerson.email.isEmpty()) {
                emailEditText.setText(myPerson.email)
            }

            if (myPerson.name != null && !myPerson.name.isEmpty()) {
                nameEditText.setText(myPerson.name)
            }

            if (myPerson.tel != null && !myPerson.tel.isEmpty()) {
                telEditText.setText(myPerson.tel)
            }

            if (myPerson.city != null && cityAdapter != null && !cityAdapter.isEmpty) {
                var i = 0;
                citySpinner.post(Runnable {
                    for (city in cityList) {
                        if (city.id == myPerson.city.id) {
                            citySpinner.setSelection(i)
                        }
                        i++
                    }
                })
            }

            alertOnTender.isChecked = myPerson.isAlertOnTender
            alertOnChat.isChecked = myPerson.isAlertOnChat

            if (myPerson.isAlertOnTender) {
                alertOnTender.setBackgroundResource(R.drawable.circle_background_gold)
            } else {
                alertOnTender.setBackgroundResource(R.drawable.circle_background_dark)
            }

            if (myPerson.isAlertOnChat) {
                alertOnChat.setBackgroundResource(R.drawable.circle_background_gold)
            } else {
                alertOnChat.setBackgroundResource(R.drawable.circle_background_dark)
            }

            seekBar.post(Runnable {
                seekBar.setProgress(myPerson.serviceArea.toFloat())
            })

            if (myPerson.serviceList != null && !myPerson.serviceList.isEmpty()) {
                var i = 0
                for (service in myPerson.serviceList) {

                    when {
                        service.id == Constants.SERVICE_IRONING_ID -> {
                            ironingBtn.setBackgroundResource(R.drawable.circle_background_gold)
                            ironingBtn.isChecked = true
                        }
                        service.id == Constants.SERVICE_WASHING_ID -> {
                            washingBtn.setBackgroundResource(R.drawable.circle_background_gold)
                            washingBtn.isChecked = true
                        }
                        service.id == Constants.SERVICE_CLEANING_ID -> {
                            cleaningBtn.setBackgroundResource(R.drawable.circle_background_gold)
                            cleaningBtn.isChecked = true
                        }
                        service.id == Constants.SERVICE_GARDENING_ID -> {
                            gardeningBtn.setBackgroundResource(R.drawable.circle_background_gold)
                            gardeningBtn.isChecked = true
                        }
                    }
                }
            }

            if (myPerson.bio != null && !myPerson.bio.isEmpty()) {
                bioEditText.setText(myPerson.bio)
            } else {
                bioEditText.setText("")
            }

            if (myPerson.isHasService) {
                isServiceBtn.setBackgroundResource(R.drawable.circle_background_gold)
                isServiceText.text = resources.getString(R.string.my_profile_im_provider)
                serviceLayout.visibility = View.VISIBLE
            } else {
                serviceLayout.visibility = View.GONE
                isServiceText.text = resources.getString(R.string.my_profile_im_not_provider)
                isServiceBtn.setBackgroundResource(R.drawable.circle_background_dark)
            }

            if (myPerson.serviceList != null && !myPerson.serviceList.isEmpty()) {
                var ironing = Utils.getService(Constants.SERVICE_IRONING_ID);
                var washing = Utils.getService(Constants.SERVICE_WASHING_ID);
                var cleaning = Utils.getService(Constants.SERVICE_CLEANING_ID);
                var gardening = Utils.getService(Constants.SERVICE_GARDENING_ID);

                if (Utils.hasService(myPerson, ironing!!)) {
                    ironingBtn.setBackgroundResource(R.drawable.circle_background_gold)
                    ironingBtn.isChecked = true
                } else {
                    ironingBtn.setBackgroundResource(R.drawable.circle_background_dark)
                    ironingBtn.isChecked = false
                }

                if (Utils.hasService(myPerson, washing!!)) {
                    washingBtn.setBackgroundResource(R.drawable.circle_background_gold)
                    washingBtn.isChecked = true
                } else {
                    washingBtn.setBackgroundResource(R.drawable.circle_background_dark)
                    washingBtn.isChecked = false
                }

                if (Utils.hasService(myPerson, cleaning!!)) {
                    cleaningBtn.setBackgroundResource(R.drawable.circle_background_gold)
                    cleaningBtn.isChecked = true
                } else {
                    cleaningBtn.setBackgroundResource(R.drawable.circle_background_dark)
                    cleaningBtn.isChecked = false
                }

                if (Utils.hasService(myPerson, gardening!!)) {
                    gardeningBtn.setBackgroundResource(R.drawable.circle_background_gold)
                    gardeningBtn.isChecked = true
                } else {
                    gardeningBtn.setBackgroundResource(R.drawable.circle_background_dark)
                    gardeningBtn.isChecked = false
                }
            }
        } else {
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "updateUI: MyPERSON is NULL")
            }
        }

        (activity as MainActivity).hideLoading(progressBar)
    }

    fun hidePickImageDialog() {
        if (pickImageDialog != null) {
            pickImageDialog.dismiss()
        }
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

        if (myPerson.photo == null || myPerson.photo.isEmpty()) {
            when {
                mySecurePreferences.getInt(Constants.SIGN_TYPE_KEY, 0) == Constants.SIGN_TYPE_GOOGLE -> {

                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "GLIDE URL: " + FirebaseAuth.getInstance().currentUser?.photoUrl)
                    }
                    myPerson.photo = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
                    photoUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
                    Glide.with(this)
                        .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                        .listener(glideRequestListener)
                        .into(myImage)
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
                        val photoUrl1 = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500"
                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "GLIDE FACEBOOK URL: " + photoUrl1)
                        }
                        myPerson.photo = photoUrl1
                        photoUrl = photoUrl1
                        Glide.with(this)
                            .load(photoUrl1)
                            .listener(glideRequestListener)
                            .into(myImage)
                    } else {

                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "GLIDE URL: " + FirebaseAuth.getInstance().currentUser?.photoUrl)
                        }

                        myPerson.photo = FirebaseAuth.getInstance().currentUser?.photoUrl as String;
                        photoUrl = FirebaseAuth.getInstance().currentUser?.photoUrl as String;
                        Glide.with(this)
                            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                            .listener(glideRequestListener)
                            .into(myImage)
                    }
                }
            }
        } else {
            var url = Utils.getImageUrl(myPerson.photo)
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "GLIDE URL: " + url)
            }
            Glide.with(this).load(url).listener(glideRequestListener).into(myImage)
            photoUrl = myPerson.photo
            photoCompleted = true
        }
    }

    private fun isValid(): Boolean {
        var result = true;

        // Name
        if (nameEditText.text == null || nameEditText.text!!.isEmpty() || nameEditText.text!!.length < 4) {
            result = false;
            nameEditText.error = resources.getString(R.string.my_profile_error_required)
            // nameLayout.error

        }

        //Email
        if (emailEditText == null || emailEditText.text!!.isEmpty() || emailEditText.text!!.length < 4) {
            result = false;
            emailEditText.error = resources.getString(R.string.my_profile_error_required)
        } else {
            if (!Utils.isEmailValid(emailEditText.text.toString())) {
                result = false;
                emailEditText.error = resources.getString(R.string.email_not_valid)
            }
        }

        //City
        if (citySpinner.selectedItemPosition == 0) {
            result = false;
            Utils.showMessage(view!!, resources.getString(R.string.my_profile_error_select_city))
        }
        return result;
    }

    private fun alertForChatClicker(it: View) {
        it as ToggleButton
        if (it.isChecked) {
            myPerson.isAlertOnChat = true;
            alertOnChat.setBackgroundResource(R.drawable.circle_background_gold)
        } else {
            myPerson.isAlertOnChat = false;
            alertOnChat.setBackgroundResource(R.drawable.circle_background_dark)
        }
    }

    private fun alertOnTenderClicker(it: View) {
        it as ToggleButton
        if (it.isChecked) {
            myPerson.isAlertOnTender = true;
            alertOnTender.setBackgroundResource(R.drawable.circle_background_gold)
        } else {
            myPerson.isAlertOnTender = false;
            alertOnTender.setBackgroundResource(R.drawable.circle_background_dark)
        }
    }

    private fun isServiceClicker() {
        if (isServiceBtn.isChecked) {
            isServiceBtn.setBackgroundResource(R.drawable.circle_background_gold)
            isServiceText.text = resources.getString(R.string.my_profile_im_provider)
            serviceLayout.visibility = View.VISIBLE
            myPerson.isHasService = true
        } else {
            serviceLayout.visibility = View.GONE
            isServiceText.text = resources.getString(R.string.my_profile_im_not_provider)
            isServiceBtn.setBackgroundResource(R.drawable.circle_background_dark)
            myPerson.isHasService = false
        }
    }

    private fun ironingServiceClicker(it: View) {
        it as ToggleButton
        var service = Utils.getService(Constants.SERVICE_IRONING_ID);
        if (it.isChecked) {
            myPerson.addServiceToList(service)
            ironingBtn.setBackgroundResource(R.drawable.circle_background_gold)
        } else {
            ironingBtn.setBackgroundResource(R.drawable.circle_background_dark)
            if (myPerson.serviceList != null && myPerson.serviceList.contains(service)) {
                myPerson.removeServiceFromList(service)
            }
        }
    }

    private fun washingServiceClicker(it: View) {
        it as ToggleButton
        var service = Utils.getService(Constants.SERVICE_WASHING_ID);
        if (myPerson.serviceList != null && myPerson.serviceList.contains(service)) {
            myPerson.removeServiceFromList(service)
            washingBtn.setBackgroundResource(R.drawable.circle_background_dark)
        } else {
            myPerson.addServiceToList(service)
            washingBtn.setBackgroundResource(R.drawable.circle_background_gold)
        }
    }

    private fun cleaningServiceClicker(it: View) {
        it as ToggleButton
        var service = Utils.getService(Constants.SERVICE_CLEANING_ID);
        if (it.isChecked) {
            myPerson.addServiceToList(service)
            cleaningBtn.setBackgroundResource(R.drawable.circle_background_gold)
        } else {
            cleaningBtn.setBackgroundResource(R.drawable.circle_background_dark)
            if (myPerson.serviceList != null && myPerson.serviceList.contains(service)) {
                myPerson.removeServiceFromList(service)
            }
        }
    }

    private fun gardeningServiceClicker(it: View) {
        it as ToggleButton
        var service = Utils.getService(Constants.SERVICE_GARDENING_ID);
        if (it.isChecked) {
            myPerson.addServiceToList(service)
            gardeningBtn.setBackgroundResource(R.drawable.circle_background_gold)
        } else {
            gardeningBtn.setBackgroundResource(R.drawable.circle_background_dark)
            if (myPerson.serviceList != null && myPerson.serviceList.contains(service)) {
                myPerson.removeServiceFromList(service)
            }
        }
    }

}
