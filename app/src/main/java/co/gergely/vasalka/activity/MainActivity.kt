package co.gergely.vasalka.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings.Secure
import android.support.design.widget.NavigationView
import android.support.v4.content.CursorLoader
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.login.LoginActivity
import co.gergely.vasalka.api.ApiChat
import co.gergely.vasalka.api.ApiPerson
import co.gergely.vasalka.api.ApiTender
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.fragment.*
import co.gergely.vasalka.inerfaces.OnClickNews
import co.gergely.vasalka.inerfaces.OnHandleChat
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.inerfaces.OnHandleTender
import co.gergely.vasalka.model.*
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.tools.Utils
import com.facebook.login.LoginManager
import com.google.android.gms.ads.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BasicActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProfileShowFragment.OnTenderListAction,
    OnHandlePerson,
    OnHandleTender,
    OnHandleChat,
    OnClickNews {


    private val debug = true;
    private val TAG = "MainActivity";
    private lateinit var mAdView: AdView
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var securePreferences: MySecurePreferences
    private lateinit var myPerson: Person
    private lateinit var myProfileFragment: MyProfileFragment;
    private lateinit var parent: FrameLayout;
    private lateinit var chatFragment: ChatFragment
    private var searchFragment: SearchFragment = SearchFragment()
    private lateinit var androidId: String
    var mCurrentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG || debug) {
            Log.d(TAG, TAG + " STARTED");
        }

        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
        mInterstitialAd = InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        init()

        val adRequest = AdRequest.Builder()
            .addTestDevice("CD53A6BF59E75FE534ACEEE4FB3BBF7A") // Apa
            .addTestDevice("BA8E587F51048328BF4ED2C40B7959C8") // Anya
            .addKeyword("Cleaning")
            .addKeyword("Laundry")
            .addKeyword("Gardening")
            .addKeyword("Washing")
            .addKeyword("Ironing")
            .build()
        mAdView.loadAd(adRequest)
        mInterstitialAd.loadAd(adRequest)


        var toolbar = findViewById<Toolbar>(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        val actionbar: ActionBar? = supportActionBar
        actionbar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar?.setCustomView(R.layout.app_bar_layout)
        actionbar?.setDisplayShowCustomEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_navigate_next_black_24dp)

        var titleId = getResources().getIdentifier(
            "title", "id",
            "android"
        );
        findViewById<TextView>(titleId)?.typeface = Typeface.createFromAsset(assets, "fonts/Luna.ttf")

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        myPerson = VasalkaApp.getSession().myPerson
        androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_header_name).text = myPerson.name

        var myImage = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_photo);
        Utils.loadMyImage(this, myImage)


        var zoomOut = true;
        myImage.setOnClickListener {
            if (zoomOut) {
                myImage
                    .animate()
                    .scaleX(2f)
                    .scaleY(2f)
                    .setInterpolator(AccelerateInterpolator())
                    .setDuration(500).start()
                zoomOut = false
            } else {
                myImage
                    .animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(AccelerateInterpolator())
                    .start()
                zoomOut = true
            }
        }

        nav_view.getHeaderView(0).setOnClickListener {
            myProfileFragment = MyProfileFragment()
            supportFragmentManager.beginTransaction().replace(R.id.content, myProfileFragment)
                .addToBackStack(null)
                .commit()
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        var shoudUpdate = false;
        if (myPerson.deviceId == null || myPerson.deviceId.isEmpty() || !myPerson.deviceId.equals(androidId)) {
            shoudUpdate = true;
            VasalkaApp.getSession().myPerson.deviceId = androidId
        }
        if (myPerson.deviceName == null || myPerson.deviceName.isEmpty() || !myPerson.deviceName.equals(Utils.getDeviceName())) {
            shoudUpdate = true;
            VasalkaApp.getSession().myPerson.deviceName = Utils.getDeviceName()
        }

        if (myPerson.deviceType == null || !myPerson.deviceType.isLetter() || myPerson.deviceType.equals('A')) {
            shoudUpdate = true;
            VasalkaApp.getSession().myPerson.deviceType = 'A';
        }

        if (shoudUpdate) {
            if (BuildConfig.DEBUG || debug) {
                Log.d(TAG, "myPerson should update");
            }
            VasalkaApp.getSession().myPerson.save()
            Utils.updateMyPerson()
        }
        goToMain()
    }

    fun checkExtras() {
        var messageType = intent.getIntExtra(Constants.PUSH_MESSAGE_TYPE_KEY, 0);
        var messageId = intent.getIntExtra(Constants.PUSH_MESSAGE_ID_KEY, 0);

        if (messageType != 0 && messageId != 0) {
            if (BuildConfig.DEBUG || debug) {
                Log.d(TAG, "HAS EXTRAS: TYPE:" + messageType + ", ID:" + messageId);
            }
            when {
                messageType == Constants.PUSH_MESSAGE_TYPE_CHAT -> {
                    var room = intent.getSerializableExtra(Constants.CHAT_ROOM_EXTRAS_KEY) as ChatRoom
                    if (room != null) {
                        onChatOpen(room);
                    }
                }
                messageType == Constants.PUSH_MESSAGE_TYPE_NEWS -> {
                    onClickNews(intent.getLongExtra(Constants.NEWS_ID_KEY, 0))
                }
                messageType == Constants.PUSH_MESSAGE_TYPE_TENDER -> {
                    onOpenTender(intent.getLongExtra(Constants.TENDER_ID_KEY, 0))
                }
                messageType == Constants.PUSH_MESSAGE_TYPE_TENDER_APPLY -> {
                    showPerson(intent.getLongExtra(Constants.PERSON_ID_KEY, 0))
                }
            }
        }
        if (BuildConfig.DEBUG || debug) {
            Log.d(TAG, "checkEXTRAS EDDIG--")
        }
    }

    fun init() {
        securePreferences = MySecurePreferences(application)
        parent = findViewById(R.id.content)
        mAdView = findViewById(R.id.adView)
    }

    override fun onResume() {
        super.onResume()
        if (!Utils.isAuthenticated()) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            this@MainActivity.finish()
        } else {
            /*
            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    Utils.refreshMyPersonFromServer(token!!);
                }
            }
            */
            checkExtras()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                if (BuildConfig.DEBUG || debug) {
                    Log.d(TAG, "Home menu CLICKED");
                }
                supportFragmentManager.beginTransaction().replace(R.id.content, MainFragment())
                    .addToBackStack(null)
                    .commit()
            }

            R.id.menu_chat -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, ChatListFragment())
                    .addToBackStack(null)
                    .commit()
            }

            R.id.menu_bookmarks -> {
                goToBookmarkList()
            }

            R.id.menu_services -> {
                goToServicerList()
            }

            R.id.menu_search -> {
                if (BuildConfig.DEBUG || debug) {
                    Log.d(TAG, "Search, Tender menu CLICKED");
                }
                if (searchFragment == null) {
                    searchFragment = SearchFragment()
                }
                supportFragmentManager.beginTransaction().replace(R.id.content, searchFragment)
                    .addToBackStack(null)
                    .commit()
            }

            R.id.menu_tender -> {
                goToTenderList()
            }

            R.id.menu_rate -> {
                goToSatisfactionList()
            }

            R.id.menu_data_protection -> {
                var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dataprotectionUrl)));
                startActivity(browserIntent);
            }

            R.id.menu_about -> {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            }

            R.id.menu_contact -> {
                Utils.sendSupportEmail(this@MainActivity)
            }

            R.id.menu_logout -> {
                if (BuildConfig.DEBUG || debug) {
                    Log.d(TAG, "Logout menu CLICKED");
                }

                var dialog: AlertDialog

                dialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.areYouSure))
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        when (securePreferences.getInt(Constants.SIGN_TYPE_KEY, 0)) {
                            Constants.GOOGLE_SIGN_IN -> {
                                googleSignOut()
                            }
                            Constants.SIGN_TYPE_FACEBOOK -> {
                                facebookLogout()
                            }
                            else -> {
                                FirebaseAuth.getInstance().signOut();
                            }
                        }
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        this@MainActivity.finish()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()

                dialog.show()

            }

            R.id.menu_delete -> {
                var dialog: AlertDialog

                dialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.sureDeletetitle))
                    .setMessage(getString(R.string.sureDeleteMessage))
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        when (securePreferences.getInt(Constants.SIGN_TYPE_KEY, 0)) {
                            Constants.GOOGLE_SIGN_IN -> {
                                googleSignOut()
                            }
                            Constants.SIGN_TYPE_FACEBOOK -> {
                                facebookLogout()
                            }
                            else -> {
                                FirebaseAuth.getInstance().signOut();
                            }
                        }
                        deletePerson()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        this@MainActivity.finish()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()

                dialog.show()
            }

        }



        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun googleSignOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestServerAuthCode(getString(R.string.web_client_id))
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope("https://www.googleapis.com/auth/plus.login"))
            .requestScopes(Scope("https://www.googleapis.com/auth/plus.me"))
            .requestScopes(Scope(Scopes.EMAIL))
            .requestScopes(Scope(Scopes.PROFILE))
            .requestScopes(Scope(Scopes.PLUS_ME))
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.signOut()
        mGoogleSignInClient.revokeAccess()
    }

    fun facebookLogout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {

            requestCode == Constants.REQUEST_SEARCH_PERSON -> {
                if (data != null) {
                    var person = data.getSerializableExtra(Constants.SEARCH_RESULT_PERSON_KEY) as Person
                    if (person != null) {
                        createChatRoom(person)
                    }
                }
            }
            requestCode == Constants.PHOTO_FOR_PERSON_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    nav_view.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_photo)
                        .setImageURI(Uri.parse(mCurrentPhotoPath))
                    uploadPersonGaleryPhoto(Uri.parse(mCurrentPhotoPath), requestCode)
                } else {
                    if (BuildConfig.DEBUG || debug) {
                        Log.d(TAG, "TAKE PROFILE PHOTO RESULT CODE: " + resultCode + ", data:" + data)
                    }
                }
            }
            requestCode == Constants.PHOTO_FOR_PERSON_GALERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data!!.data != null) {
                        nav_view.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_photo)
                            .setImageURI(data!!.data)
                        uploadPersonGaleryPhoto(data!!.data, requestCode)
                        var path = fromUriToPath(data!!.data)
                        myProfileFragment.photoUrl = fromUriToPath(data!!.data)
                        myProfileFragment.updateUI()
                    }
                } else {
                    if (BuildConfig.DEBUG || debug) {
                        Log.d(TAG, "GALERY PROFILE PHOTO RESULT CODE: " + resultCode + ", data:" + data)
                    }
                }
            }
            requestCode == Constants.PHOTO_FOR_TENDER_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    searchFragment?.addTenderImage(mCurrentPhotoPath!!)
                } else {
                    if (BuildConfig.DEBUG || debug) {
                        Log.d(TAG, "TAKE TENDER PHOTO RESULT CODE: " + resultCode + ", data:" + data)
                    }
                }
            }
            requestCode == Constants.PHOTO_FOR_TENDER_GALERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data!!.data != null) {
                        searchFragment?.addTenderImage(fromUriToPath(data!!.data))
                    } else {
                        Utils.showMessage(parent, "Unable to choose image")
                    }
                } else {
                    if (BuildConfig.DEBUG || debug) {
                        Log.d(TAG, "GALERY TENDER PHOTO RESULT CODE: " + resultCode + ", data:" + data)
                    }
                }
            }
            requestCode == Constants.PHOTO_FOR_CHAT_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    chatFragment.setImage(mCurrentPhotoPath!!)
                } else {
                    if (BuildConfig.DEBUG || debug) {
                        Log.d(TAG, "TAKE CHAT PHOTO RESULT CODE: " + resultCode + ", data:" + data)
                    }
                }
            }
            requestCode == Constants.PHOTO_FOR_CHAT_GALERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data!!.data != null && chatFragment != null) {
                        chatFragment.setImage(fromUriToPath(data!!.data))
                    } else {
                        Utils.showMessage(parent, "Unable to choose image")
                    }
                } else {
                    if (BuildConfig.DEBUG || debug) {
                        Log.d(TAG, "GALERY CHAT PHOTO RESULT CODE: " + resultCode + ", data:" + data)
                    }
                }
            }
        }
    }

    fun fromUriToPath(filePath: Uri): String {

        var projection = arrayOf(MediaStore.Images.Media.DATA)
        var loader = CursorLoader(this, filePath, projection, null, null, null)
        var cursor = loader.loadInBackground()
        var idx = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor!!.moveToFirst()
        var path = cursor!!.getString(idx!!)
        return path
    }

    fun uploadPersonGaleryPhoto(filePath: Uri, action: Int) {
        showLoading(myProfileFragment.progressBar)
        var path: String? = null;
        if (action == Constants.PHOTO_FOR_PERSON_GALERY) {
            path = fromUriToPath(filePath);
        } else if (action == Constants.PHOTO_FOR_PERSON_CAMERA) {
            path = filePath.toString()
        }
        var file = File(path)
        if (BuildConfig.DEBUG || debug) {
            Log.d(TAG, "FILE PATH: " + path)
        }
        var fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        var part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody)

        myPerson = VasalkaApp.getSession().myPerson
        myProfileFragment.hidePickImageDialog()

        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var uploadPersonImage = NetworkClient.getRetrofit(token)
                    .create(ApiPerson::class.java)

                uploadPersonImage.uploadImage(part, myPerson.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            var personService =
                                NetworkClient.getRetrofit(securePreferences.getString(Constants.JWT_TOKEN_KEY, ""))
                                    .create(ApiPerson::class.java)

                            personService.getPerson(VasalkaApp.getSession().myPerson.id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    VasalkaApp.getSession().myPerson = it
                                    VasalkaApp.getSession().myPerson.save()
                                    hideLoading(myProfileFragment.progressBar)
                                    myProfileFragment.updateUI()
                                }, {
                                    Utils.unknownError(this@MainActivity)
                                })
                        }

                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        hideLoading(myProfileFragment.progressBar)
                        if (BuildConfig.DEBUG || debug) {
                            Log.d(TAG, "ERROR:" + t.localizedMessage)
                        }
                    }
                })
            }
        }
    }

    fun goToMain() {
        supportFragmentManager.beginTransaction().replace(R.id.content, MainFragment())
            .addToBackStack(null)
            .commit()
        if (BuildConfig.DEBUG || debug) {
            Log.d(TAG, "MainFragment COMMITED");
        }
    }

    fun goToSatisfactionList() {
        supportFragmentManager.beginTransaction().replace(R.id.content, SatisfactionListFragment())
            .addToBackStack(null)
            .commit()
    }

    fun goToShowProfile(person: Person) {
        supportFragmentManager.beginTransaction().replace(R.id.content, ProfileShowFragment.newInstance(person))
            .addToBackStack(null)
            .commit()
    }

    fun goToServicerList() {
        supportFragmentManager.beginTransaction().replace(R.id.content, ServicerListFragment())
            .addToBackStack(null)
            .commit()
    }

    fun goToChatList() {
        var chatListFragment = ChatListFragment();
        supportFragmentManager.beginTransaction().replace(R.id.content, chatListFragment)
            .addToBackStack(null)
            .commit()
    }

    fun goToBookmarkList() {
        supportFragmentManager.beginTransaction().replace(R.id.content, BookmarkListFragment())
            .addToBackStack(null)
            .commit()
    }

    fun goToTenderList() {
        supportFragmentManager.beginTransaction().replace(R.id.content, TenderListFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onShowSearchResult(list: List<SearchPerson>) {
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                supportFragmentManager.beginTransaction().replace(R.id.content, SearchResultFragment.newInstance(list))
                    .addToBackStack(null)
                    .commit()
            }
        }
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "INTERSTITIAL NOT LOADED")
            }
            supportFragmentManager.beginTransaction().replace(R.id.content, SearchResultFragment.newInstance(list))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onPersonClicked(person: SearchPerson) {
        goToShowProfile(person as Person)
    }

    override fun onPersonClicked(person: Person?) {
        goToShowProfile(person!!)
    }

    override fun onTenderClicked(tender: Tender) {
        supportFragmentManager.beginTransaction().replace(R.id.content, TenderShowFragment.newInstance(tender))
            .addToBackStack(null)
            .commit()
    }

    override fun onTenderListAction() {
        supportFragmentManager.beginTransaction().replace(R.id.content, TenderListFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onOpenTender(id: Long?) {
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var tenderServices = NetworkClient.getRetrofit(token).create(ApiTender::class.java)
                tenderServices.getTender(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        onTenderClicked(it)
                    }, {
                        if (BuildConfig.DEBUG || debug) {
                            it.printStackTrace()
                            Log.e(TAG, "onOpenTender: " + it.localizedMessage)
                        }
                        Utils.unknownError(this@MainActivity)
                    })
            }
        }
    }

    fun showPerson(id: Long) {
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                val personService = NetworkClient.getRetrofit(token).create(ApiPerson::class.java)
                personService.getPerson(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        onPersonClicked(it!!)
                    }, {
                        if (BuildConfig.DEBUG || debug) {
                            Log.e(TAG, "showPerson error: " + it.localizedMessage)
                            it.printStackTrace()
                        }
                        Utils.unknownError(this@MainActivity)
                    })
            }
        }

    }

    override fun onApplyTender(tender: Tender) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun deletePerson() {
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                val personService = NetworkClient.getRetrofit(token).create(ApiPerson::class.java)
                personService.delete(VasalkaApp.getSession().myPerson.id).enqueue(object : Callback<Void> {

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {

                        var dialog: AlertDialog

                        dialog = AlertDialog.Builder(this@MainActivity)
                            .setTitle(getString(R.string.sureDeletetitle))
                            .setMessage(getString(R.string.goodbye))
                            .setNeutralButton(R.string.ok, { dialog, which ->
                                dialog.dismiss()
                                var intent = Intent(this@MainActivity, SplashActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            })
                            .create()
                        dialog.show()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Utils.unknownError(this@MainActivity)
                    }
                })
            }
        }
    }

    override fun onClickNews(id: Long?) {
        var intent = Intent(this@MainActivity, NewsActivity::class.java)
        intent.putExtra(Constants.NEWS_ID_KEY, id)
        startActivity(intent);
    }

    override fun onChatOpen(room: ChatRoom) {
        val partner: Person
        if (room.personOne.id == myPerson.id) {
            partner = room.personTwo
        } else {
            partner = room.personOne
        }
        chatFragment = ChatFragment.newInstance(partner, room)
        supportFragmentManager.beginTransaction().replace(R.id.content, chatFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onChatDelete(room: ChatRoom) {
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var chatService = NetworkClient.getRetrofit(token).create(ApiChat::class.java);
                chatService.updateChat(VasalkaApp.getSession().myPerson.id, room)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        supportFragmentManager.beginTransaction().replace(R.id.content, ChatListFragment())
                            .addToBackStack(null)
                            .commit()
                    }, {
                        Utils.unknownError(this@MainActivity)
                    })
            }
        }
    }

    fun createChatRoom(person: Person) {
        var dbRef = FirebaseDatabase.getInstance().getReference().child("chat")
        var fbId = dbRef.push().getKey();
        var room = ChatRoom()
        room.personOne = VasalkaApp.getSession().myPerson
        room.personTwo = person
        room.fbId = fbId
        var fbRoom = FbChatRoom(room)

        dbRef.child(fbId!!).setValue(fbRoom)
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var chatService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)
                chatService.createChat(VasalkaApp.getSession().myPerson.id, room)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (!it.fbId.equals(fbId)) { // ha már létezett
                            dbRef.child(fbId).removeValue()
                        }
                        goToChatList()
                    }, {
                        Utils.unknownError(this@MainActivity)
                    })
            }
        }

    }
}

