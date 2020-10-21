package co.gergely.vasalka.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.login.LoginActivity
import co.gergely.vasalka.api.*
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.model.*
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private val debug = true
    private lateinit var progressBar: ProgressBar
    private lateinit var securePreferences: MySecurePreferences
    private var writeExternalStorageCheck: Int = 0;
    private var cameraCheck: Int = 0
    private var messageType: Int? = 0
    private var messageId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(co.gergely.vasalka.R.layout.activity_splash)

        securePreferences = MySecurePreferences(application)

        val myTextView = findViewById<View>(R.id.splash_title) as TextView
        val typeFace = Typeface.createFromAsset(assets, "fonts/Luna.ttf")
        myTextView.typeface = typeFace
        progressBar = findViewById(R.id.splash_progressbar)
    }

    override fun onResume() {
        super.onResume()

        progressBar.visibility = View.VISIBLE

        writeExternalStorageCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        cameraCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (writeExternalStorageCheck != PackageManager.PERMISSION_GRANTED) { // External storage check
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.REQUEST_WRITE_EXTERNAL_STORAGE_STATE
            );
        } else if (cameraCheck != PackageManager.PERMISSION_GRANTED) { // Camera permission check
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Constants.REQUEST_CAMERA_STATE
            );
        } else { //We have all permissions

            if (Utils.isAuthenticated()) { //Authenticated by FireBase
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "OnResume: Is Authenticated")
                }

                if (checkExtras()) {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "OnResume: THERE IS EXTRAS")
                    }
                    goWithExtras()
                } else {
                    if (BuildConfig.DEBUG && debug) {
                        Log.d(TAG, "OnResume: GO TO MAIN")
                    }
                    doCache()
                }
            } else {
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "OnResume: IS NOT Authenticated")
                }
                goToLogin()
            }
        }
    }

    fun checkExtras(): Boolean {
        var extras = getIntent().getExtras()

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "EXTRAS:" + extras)
        }
        if (extras == null) {
            return false;
        }
        messageType = Integer.parseInt(extras.getString(Constants.PUSH_MESSAGE_TYPE_KEY, "0"));
        messageId = Integer.parseInt(extras.getString(Constants.PUSH_MESSAGE_ID_KEY, "0"));

        if (messageType != 0 && messageId != 0) {
            return true;
        } else {
            return false;
        }
    }

    fun doCache() {
        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "OnResume: CACHE STARTED")
        }
        if (Utils.isAuthenticated()) {
            var user = FirebaseAuth.getInstance().currentUser
            var policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            user!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token; //<--- TOKEN FOR AUTH HEADER
                    if (token != null && !token.isEmpty()) {
                        VasalkaApp.getSession().setJwtToken(token!!)

                        var apiLogin = NetworkClient.getRetrofit(token).create(ApiLogin::class.java);
                        apiLogin.person
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                VasalkaApp.getSession().myPerson = it
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
                                    runOnUiThread {
                                        progressBar.visibility = View.GONE
                                    }
                                    goToMain()
                                }, {
                                    if (BuildConfig.DEBUG && debug) {
                                        Log.e(TAG, "CACHE THREAD ERROR1: " + it.localizedMessage)
                                    }
                                    it.printStackTrace()
                                })

                            }, {
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "CACHE THREAD ERROR: " + it.localizedMessage)
                                }
                                it.printStackTrace()

                                var builder = AlertDialog.Builder(this@SplashActivity)
                                    .setTitle(R.string.timeOutTitle)
                                    .setMessage(R.string.timeOutMessage)
                                    .setCancelable(true)
                                    .setNegativeButton(
                                        R.string.timeOutTryAgain,
                                        DialogInterface.OnClickListener({ dialog, which ->

                                            dialog.dismiss()
                                            progressBar.visibility = View.VISIBLE

                                            val requests = ArrayList<Observable<*>>()

                                            var apiServices =
                                                NetworkClient.getRetrofit(token).create(ApiServices::class.java)
                                            var apiCity = NetworkClient.getRetrofit(token).create(ApiCity::class.java)
                                            var chatRoomService =
                                                NetworkClient.getRetrofit(token).create(ApiChat::class.java)
                                            var newsService =
                                                NetworkClient.getRetrofit(token).create(ApiNews::class.java)
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
                                                VasalkaApp.getSession().providerList =
                                                    it[4] as MutableList<SearchPerson>?
                                                VasalkaApp.getSession().satisfactionList =
                                                    it[5] as MutableList<Satisfaction>?
                                                VasalkaApp.getSession().tenderList = it[6] as MutableList<Tender>?
                                            }.subscribe({
                                                runOnUiThread {
                                                    progressBar.visibility = View.GONE
                                                }
                                                goToMain()
                                            }, {
                                                if (BuildConfig.DEBUG && debug) {
                                                    Log.e(TAG, "CACHE THREAD ERROR1: " + it.localizedMessage)
                                                }
                                                it.printStackTrace()
                                            })
                                        })
                                    )
                                    .setPositiveButton(
                                        R.string.timeOutTryLater,
                                        DialogInterface.OnClickListener({ dialog, which ->
                                            this.finish()
                                        })
                                    )
                                var dialog = builder.create()
                                dialog.show()
                            })
                    }
                }
            }
        }
    }

    fun goWithExtras() {
        if (Utils.isAuthenticated() && messageType != 0) {
            val intent = Intent(
                application.applicationContext,
                MainActivity::class.java
            )
            intent.putExtra(Constants.PUSH_MESSAGE_TYPE_KEY, messageType!!)
            intent.putExtra(Constants.PUSH_MESSAGE_ID_KEY, messageId!!)
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            when {
                messageType == Constants.PUSH_MESSAGE_TYPE_CHAT -> {

                    var room: ChatRoom? = null;
                    if (VasalkaApp.getSession().chatRoomList.isEmpty()) {
                        for (listRoom in VasalkaApp.getSession().chatRoomList) {
                            if (listRoom.id == messageId!!.toLong()) {
                                room = listRoom;
                            }
                        }
                    }

                    if (room == null) {
                        var chatService = NetworkClient.getRetrofit(null).create(ApiChat::class.java)
                        chatService.getRoomByPartner(VasalkaApp.getSession().myPerson.id, messageId!!.toLong())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                intent.putExtra(Constants.CHAT_ROOM_EXTRAS_KEY, it)
                            }, {
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, it.localizedMessage)
                                    it.printStackTrace()
                                }
                            })
                    } else {
                        intent.putExtra(Constants.CHAT_ROOM_EXTRAS_KEY, room)
                    }

                }

                messageType == Constants.PUSH_MESSAGE_TYPE_NEWS -> {
                    intent.putExtra(Constants.NEWS_ID_KEY, messageId!!.toLong())
                }

                messageType == Constants.PUSH_MESSAGE_TYPE_TENDER -> {
                    intent.putExtra(Constants.TENDER_ID_KEY, messageId!!.toLong())
                }

                messageType == Constants.PUSH_MESSAGE_TYPE_TENDER_APPLY -> {
                    intent.putExtra(Constants.PERSON_ID_KEY, messageId!!.toLong())
                }
            }
            application.applicationContext.startActivity(intent)
        }
    }

    fun goToLogin() {
        startActivity(
            Intent(
                this@SplashActivity,
                LoginActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        )
        this.finish()
    }

    fun goToMain() {
        runOnUiThread {
            progressBar.visibility = View.GONE
        }
        startActivity(
            Intent(
                this@SplashActivity,
                MainActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        this.finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        writeExternalStorageCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        cameraCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (requestCode == Constants.REQUEST_WRITE_EXTERNAL_STORAGE_STATE) {
            if ((grantResults.size > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (cameraCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        Constants.REQUEST_CAMERA_STATE
                    )
                } else {
                    if (Utils.isAuthenticated()) {
                        doCache()
                    } else {
                        goToLogin()
                    }
                }
            } else {
                //TODO Alert no permission
            }
        }

        if (requestCode == Constants.REQUEST_CAMERA_STATE) {
            if ((grantResults.size > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (writeExternalStorageCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Constants.REQUEST_WRITE_EXTERNAL_STORAGE_STATE
                    )
                } else {
                    if (Utils.isAuthenticated()) {
                        doCache()
                    } else {
                        goToLogin()
                    }
                }
            } else {
                //TODO Alert no permission
            }
        }

    }
}
