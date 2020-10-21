package co.gergely.vasalka.tools

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.MainActivity
import co.gergely.vasalka.api.*
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.model.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.michael.easydialog.EasyDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern
import kotlin.math.roundToInt



class Utils {

    companion object {
        val debug = true
        val TAG = "UTILS"

        var cityCacheDone = false
        var serviceCacheDone = false
        var chatListCahceDone = false
        var newsListCacheDone = false
        var providerListCacheDone = false
        var satisfactionListCacheDone = false;
        var tenderListCacheDone = false;

        fun loadMyImage(context: Context, myImage: ImageView) {

            var myPerson = VasalkaApp.getSession().myPerson

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
                    //TODO
                    return false
                }
            }

            if (myPerson.photo == null || myPerson.photo.isEmpty()) {
                when {
                    myPerson.loginType == Constants.SIGN_TYPE_GOOGLE -> {
                        if (BuildConfig.DEBUG && debug) {
                            Log.d("Utils", "GLIDE URL: " + FirebaseAuth.getInstance().currentUser?.photoUrl)
                        }
                        Glide.with(context)
                            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                            .listener(glideRequestListener)
                            .into(myImage)
                    }
                    myPerson.loginType == Constants.SIGN_TYPE_FACEBOOK -> {
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
                                Log.d("Utils", "GLIDE FACEBOOK URL: " + photoUrl)
                            }
                            Glide.with(context)
                                .load(photoUrl)
                                .listener(glideRequestListener)
                                .into(myImage)
                        } else {
                            if (BuildConfig.DEBUG && debug) {
                                Log.d("Utils", "GLIDE URL: " + FirebaseAuth.getInstance().currentUser?.photoUrl)
                            }
                            Glide.with(context)
                                .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                                .listener(glideRequestListener)
                                .into(myImage)
                        }
                    }
                }
            } else {
                Glide.with(context).load(getImageUrl(myPerson.photo)).listener(glideRequestListener).into(myImage)
            }
        }

        fun getImageUrl(value: String): String {
            Log.d("UTILS", "GOT URL:" + value)
            var url: String
            if (value.contains("http://")) {
                url = value.replace("http://", "https://", true)
            } else if (value.contains("https://")) {
                url = value
            } else {
                url = Constants.BASE_URL + value
            }
            Log.d("UTILS", "RETURN URL:" + url)
            return url;
        }

        fun refreshMyPersonFromServer(token: String) {
            var personService = NetworkClient.getRetrofit(token)
                .create(ApiPerson::class.java)
            personService.getPerson(VasalkaApp.getSession().myPerson.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().myPerson = it
                    VasalkaApp.getSession().myPerson.save()
                })
        }

        fun updateMyPerson() {
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;

                    var personService = NetworkClient.getRetrofit(token)
                        .create(ApiPerson::class.java)

                    personService.updatePerson(VasalkaApp.getSession().myPerson.id, VasalkaApp.getSession().myPerson)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            VasalkaApp.getSession().myPerson = it
                            VasalkaApp.getSession().myPerson.save()
                        });
                }
            }
        }

        fun easyDialog(activity: Activity, v: View, message: String) {
            val view = activity.layoutInflater.inflate(R.layout.layout_tip_content_horizontal, null)
            view.findViewById<TextView>(R.id.easy_message).text = message
            EasyDialog(activity)
                // .setLayoutResourceId(R.layout.layout_tip_content_horizontal)//layout resource id
                .setLayout(view)
                .setBackgroundColor(activity.getResources().getColor(R.color.background_color_black))
                // .setLocation(new location[])//point in screen
                .setLocationByAttachedView(v)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setAnimationTranslationShow(
                    EasyDialog.DIRECTION_X,
                    1000,
                    -600.toFloat(),
                    100.toFloat(),
                    -50.toFloat(),
                    50.toFloat(),
                    0.toFloat()
                )
                .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50.toFloat(), 800.toFloat())
                .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                .setTouchOutsideDismiss(true)
                .setMatchParent(true)
                .setMarginLeftAndRight(24, 24)
                .setOutsideColor(activity.getResources().getColor(R.color.outside_color_trans))
                .show()
        }

        fun showMessage(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }

        fun pxToDp(context: Context, pixel: Int): Int {
            var scale = context.resources.displayMetrics.density
            return (pixel * scale).roundToInt()
        }

        fun isEmailValid(email: String): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

        fun hasService(person: Person, service: Service): Boolean {
            if (person.serviceList == null) {
                return false;
            } else {
                for (pService in person.serviceList) {
                    if (pService.id == service.id) {
                        return true;
                    }
                }
                return false;
            }
        }

        fun hasService(person: Person, serviceId: Long): Boolean {
            if (person.serviceList == null) {
                return false;
            } else {
                for (pService in person.serviceList) {
                    if (pService.id == serviceId) {
                        return true;
                    }
                }
                return false;
            }
        }

        fun getService(serviceId: Long): Service? {
            for (service in VasalkaApp.getSession().serviceList) {
                if (serviceId == service.id) {
                    return service
                }
            }
            return null
        }

        fun getCity(cityId: Long): City? {
            for (city in VasalkaApp.getSession().cityList) {
                if (cityId == city.id) {
                    return city
                }
            }
            return null
        }

        fun doCacheTenderList(token: String) {
            var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java)
            tenderService.getAffectedTenderList(VasalkaApp.getSession().myPerson.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().tenderList = it
                    tenderListCacheDone = true
                }, {
                    tenderListCacheDone = true
                })

        }

        fun doCacheSatisfactionList(token: String) {
            var satisfactionService =
                NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java)
            satisfactionService.getSatisfactionsFor(VasalkaApp.getSession().myPerson.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().satisfactionList = it;
                    satisfactionListCacheDone = true;
                }, {
                    if (BuildConfig.DEBUG && debug) {
                        it.printStackTrace()
                    }
                    satisfactionListCacheDone = true;
                })
        }

        fun doCacheProviderList(token: String) {
            var searchService = NetworkClient.getRetrofit(token).create(ApiSearch::class.java);

            searchService.getListFor(VasalkaApp.getSession().myPerson.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().providerList = it;
                    providerListCacheDone = true;
                }, {
                    if (BuildConfig.DEBUG && debug) {
                        it.printStackTrace()
                    }
                    providerListCacheDone = true;
                })
        }

        fun doCacheLatestNews(token: String) {
            var newsService = NetworkClient.getRetrofit(token).create(ApiNews::class.java)
            newsService.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().newsList = it;
                    newsListCacheDone = true;
                }, {
                    if (BuildConfig.DEBUG && debug) {
                        it.printStackTrace()
                        newsListCacheDone = true;
                    }
                })


        }

        fun doCacheCityList(token: String) {
            var apiCity = NetworkClient.getRetrofit(token)
                .create(ApiCity::class.java)

            apiCity.cities
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().setCityListList(it)
                    Log.d(TAG, "CITY CACHE DONE")
                    cityCacheDone = true;
                })

        }

        fun doCacheServiceList(token: String) {
            var apiServices = NetworkClient.getRetrofit(token)
                .create(ApiServices::class.java)

            apiServices.serviceList
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().setServiceList(it)
                    serviceCacheDone = true;
                })
        }

        fun doCacheChatRoomList(token: String) {
            var chatRoomService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)

            chatRoomService.getList(VasalkaApp.getSession().myPerson.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    VasalkaApp.getSession().setChatRoomList(it)
                    Log.d(TAG, "CHAT CACHE DONE")
                    chatListCahceDone = true;
                })
        }

        fun getBookMarkTypes(): List<BookmarkObjectType> {
            var lista = ArrayList<BookmarkObjectType>();

            var bookmarkObjectType1 = BookmarkObjectType()
            bookmarkObjectType1.id = Constants.BOOKMARK_OBJECT_TYPE_PERSON_ID;
            bookmarkObjectType1.name = Constants.BOOKMARK_OBJECT_TYPE_PERSON_NAME

            lista.add(bookmarkObjectType1)

            var bookmarkObjectType2 = BookmarkObjectType()
            bookmarkObjectType2.id = Constants.BOOKMARK_OBJECT_TYPE_TENDER_ID;
            bookmarkObjectType2.name = Constants.BOOKMARK_OBJECT_TYPE_TENDER_NAME

            lista.add(bookmarkObjectType2)

            var bookmarkObjectType3 = BookmarkObjectType()
            bookmarkObjectType3.id = Constants.BOOKMARK_OBJECT_TYPE_CHAT_ID;
            bookmarkObjectType3.name = Constants.BOOKMARK_OBJECT_TYPE_CHAT_NAME

            lista.add(bookmarkObjectType3)

            return lista

        }

        fun addPersonToMyBookmark(context: Context, person: Person) {
            var bookmark = BookmarkForSend(Constants.BOOKMARK_OBJECT_TYPE_PERSON_ID, person.id);
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var bookmarkService = NetworkClient.getRetrofit(token).create(ApiBookmark::class.java);
                    bookmarkService.createBookmark(VasalkaApp.getSession().myPerson.id, bookmark)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            var dialog = AlertDialog.Builder(context)
                                .setTitle(R.string.alertTitleAttention)
                                .setMessage(R.string.bookmarkAdded)
                                .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                })
                                .create()
                            dialog.show()
                        }, {
                            if (it.message!!.contains("406")) {
                                var dialog = AlertDialog.Builder(context)
                                    .setTitle(R.string.alertTitleAttention)
                                    .setMessage(R.string.bookmarkAlreadyExist)
                                    .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                        dialog.dismiss()
                                    })
                                    .create()
                                dialog.show()
                            } else {
                                var dialog = AlertDialog.Builder(context)
                                    .setTitle(R.string.alertTitleAttention)
                                    .setMessage(it.localizedMessage)
                                    .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                        dialog.dismiss()
                                    })
                                    .create()
                                dialog.show()
                            }
                        })
                }
            }
        }

        fun addTenderToMyBookmark(context: Context, tender: Tender) {
            var bookmark = BookmarkForSend(Constants.BOOKMARK_OBJECT_TYPE_TENDER_ID, tender.id);

            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var bookmarkService = NetworkClient.getRetrofit(token).create(ApiBookmark::class.java);
                    bookmarkService.createBookmark(VasalkaApp.getSession().myPerson.id, bookmark)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            var dialog = AlertDialog.Builder(context)
                                .setTitle(R.string.alertTitleAttention)
                                .setMessage(R.string.bookmarkAdded)
                                .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                })
                                .create()
                            dialog.show()
                        }, {
                            var dialog = AlertDialog.Builder(context)
                                .setTitle(R.string.alertTitleAttention)
                                .setMessage(it.localizedMessage)
                                .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                })
                                .create()
                            dialog.show()
                        })
                }
            }
        }

        fun addChatToMyBookmark(context: Context, chatRoom: ChatRoom) {
            var bookmark = BookmarkForSend(Constants.BOOKMARK_OBJECT_TYPE_CHAT_ID, chatRoom.id);

            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;

                    var bookmarkService = NetworkClient.getRetrofit(token).create(ApiBookmark::class.java);
                    bookmarkService.createBookmark(VasalkaApp.getSession().myPerson.id, bookmark)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            var dialog = AlertDialog.Builder(context)
                                .setTitle(R.string.alertTitleAttention)
                                .setMessage(R.string.bookmarkAdded)
                                .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                })
                                .create()
                            dialog.show()
                        }, {
                            if (BuildConfig.DEBUG && debug) {
                                it.printStackTrace()
                            }
                            var dialog = AlertDialog.Builder(context)
                                .setTitle(R.string.alertTitleAttention)
                                .setMessage(it.localizedMessage)
                                .setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                })
                                .create()
                            dialog.show()
                        })
                }
            }
        }

        fun isAuthenticated(): Boolean {
            return FirebaseAuth.getInstance().currentUser != null
        }

        fun unknownError(context: Context) {
            var dialog: android.support.v7.app.AlertDialog

            dialog = android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alertTitleAttention))
                .setMessage(context.getString(R.string.unknownError))
                .setNeutralButton(R.string.ok, { dialog, which ->
                    dialog.dismiss()
                })
                .create()
            dialog.show()
        }

        fun login(
            context: Activity,
            application: Application,
            user: FirebaseUser,
            loginType: Int,
            progressBar: ProgressBar
        ) {
            progressBar.visibility = View.VISIBLE
            val securePreferences = MySecurePreferences(application)
            var policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            user!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    securePreferences.putString(Constants.JWT_TOKEN_KEY, token)
                    securePreferences.putInt(Constants.SIGN_TYPE_KEY, loginType)

                    var apiLogin = NetworkClient.getRetrofit(token).create(ApiLogin::class.java);

                    apiLogin.person
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d("UTILS", " PERSON:" + it)
                            Log.d("UTILS", " PERSON TOKEN: '" + "MYFBS " + token + "'")

                            VasalkaApp.getSession().myPerson = it
                            VasalkaApp.getSession().myPerson.loginType = loginType
                            VasalkaApp.getSession().myPerson.token = token
                            VasalkaApp.getSession().myPerson.save()

                            val requests = ArrayList<Observable<*>>()

                            var apiServices = NetworkClient.getRetrofit(token).create(ApiServices::class.java)
                            var apiCity = NetworkClient.getRetrofit(token).create(ApiCity::class.java)
                            var chatRoomService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)
                            var newsService = NetworkClient.getRetrofit(token).create(ApiNews::class.java)
                            var searchService = NetworkClient.getRetrofit(token).create(ApiSearch::class.java)
                            var satisfactionService =
                                NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java)
                            var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java)

                            requests.add(apiServices.serviceList.subscribeOn(Schedulers.newThread()))
                            requests.add(apiCity.cities.subscribeOn(Schedulers.newThread()))
                            requests.add(
                                chatRoomService.getList(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                    Schedulers.newThread()
                                )
                            )
                            requests.add(newsService.getList().subscribeOn(Schedulers.newThread()))
                            requests.add(
                                searchService.getListFor(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                    Schedulers.newThread()
                                )
                            )
                            requests.add(
                                satisfactionService.getSatisfactionsFor(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                    Schedulers.newThread()
                                )
                            )
                            requests.add(
                                tenderService.getAffectedTenderList(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                    Schedulers.newThread()
                                )
                            )

                            Observable.zip(requests) {
                                VasalkaApp.getSession().serviceList = it[0] as MutableList<Service>?
                                VasalkaApp.getSession().setCityListList(it[1] as MutableList<City>?)
                                VasalkaApp.getSession().chatRoomList = it[2] as MutableList<ChatRoom>?
                                VasalkaApp.getSession().newsList = it[3] as MutableList<News>?
                                VasalkaApp.getSession().providerList = it[4] as MutableList<SearchPerson>?
                                VasalkaApp.getSession().satisfactionList = it[5] as MutableList<Satisfaction>?
                                VasalkaApp.getSession().tenderList = it[6] as MutableList<Tender>?

                            }.subscribe({
                                Log.i(TAG, "ALL CACHE DONE")
                                application.applicationContext.startActivity(
                                    Intent(
                                        application.applicationContext,
                                        MainActivity::class.java
                                    ).setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )

                            }, {
                                Log.e(TAG, "CACHE ERROR: " + it.localizedMessage)
                                it.printStackTrace()
                            })
                        }, {
                            var builder = android.support.v7.app.AlertDialog.Builder(context)
                                .setTitle(R.string.timeOutTitle)
                                .setMessage(R.string.timeOutMessage)
                                .setCancelable(true)
                                .setNegativeButton(
                                    R.string.timeOutTryAgain,
                                    DialogInterface.OnClickListener({ dialog, which ->

                                        val requests = ArrayList<Observable<*>>()

                                        var apiServices =
                                            NetworkClient.getRetrofit(token).create(ApiServices::class.java)
                                        var apiCity = NetworkClient.getRetrofit(token).create(ApiCity::class.java)
                                        var chatRoomService =
                                            NetworkClient.getRetrofit(token).create(ApiChat::class.java)
                                        var newsService = NetworkClient.getRetrofit(token).create(ApiNews::class.java)
                                        var searchService =
                                            NetworkClient.getRetrofit(token).create(ApiSearch::class.java)
                                        var satisfactionService =
                                            NetworkClient.getRetrofit(token).create(ApiSatisfaction::class.java)
                                        var tenderService =
                                            NetworkClient.getRetrofit(token).create(ApiTender::class.java)

                                        requests.add(apiServices.serviceList.subscribeOn(Schedulers.newThread()))
                                        requests.add(apiCity.cities.subscribeOn(Schedulers.newThread()))
                                        requests.add(
                                            chatRoomService.getList(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                                Schedulers.newThread()
                                            )
                                        )
                                        requests.add(newsService.getList().subscribeOn(Schedulers.newThread()))
                                        requests.add(
                                            searchService.getListFor(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                                Schedulers.newThread()
                                            )
                                        )
                                        requests.add(
                                            satisfactionService.getSatisfactionsFor(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                                Schedulers.newThread()
                                            )
                                        )
                                        requests.add(
                                            tenderService.getAffectedTenderList(VasalkaApp.getSession().myPerson.id).subscribeOn(
                                                Schedulers.newThread()
                                            )
                                        )


                                        Observable.zip(requests) {
                                            VasalkaApp.getSession().serviceList = it[0] as MutableList<Service>?
                                            VasalkaApp.getSession().setCityListList(it[1] as MutableList<City>?)
                                            VasalkaApp.getSession().chatRoomList = it[2] as MutableList<ChatRoom>?
                                            VasalkaApp.getSession().newsList = it[3] as MutableList<News>?
                                            VasalkaApp.getSession().providerList = it[4] as MutableList<SearchPerson>?
                                            VasalkaApp.getSession().satisfactionList =
                                                it[5] as MutableList<Satisfaction>?
                                            VasalkaApp.getSession().tenderList = it[6] as MutableList<Tender>?
                                        }.subscribe({
                                            application.applicationContext.startActivity(
                                                Intent(
                                                    application.applicationContext,
                                                    MainActivity::class.java
                                                ).setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )
                                            progressBar.visibility = View.GONE
                                        }, {
                                            it.printStackTrace()
                                        })

                                        dialog.dismiss()
                                    })
                                )
                                .setPositiveButton(
                                    R.string.timeOutTryLater,
                                    DialogInterface.OnClickListener({ dialog, which ->
                                        context.finish()
                                    })
                                )

                            var dialog = builder.create()
                            dialog.show()

                        })
                }
            }
        }

        fun sendSupportEmail(context: Context) {

            var emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@vasalka.hu", null
                )
            );
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Vasalka kapcsolat");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Vasalka alkalmazásból küldve");
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        fun getDeviceName(): String {
            var manufacturer = Build.MANUFACTURER;
            var model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return capitalize(model);
            }
            return capitalize(manufacturer) + " " + model;
        }

        fun capitalize(str: String): String {
            if (TextUtils.isEmpty(str)) {
                return str;
            }
            var arr = str.toCharArray();
            var capitalizeNext = true;

            var phrase = StringBuilder();
            for (c in arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    phrase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                    continue;
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true;
                }
                phrase.append(c);
            }

            return phrase.toString();
        }

    }
}