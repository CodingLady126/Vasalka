package co.gergely.vasalka.service

import android.util.Log
import android.widget.Toast
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.tools.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MessageService"
    private val debug = true;

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "New Firebase token received: '" + p0 + "'");
        }

        if (VasalkaApp.getSession().myPerson.id != null) {
            VasalkaApp.getSession().myPerson.token = p0
            VasalkaApp.getSession().myPerson.save()
            Utils.updateMyPerson()
        }
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        if (BuildConfig.DEBUG && debug) {
            Log.d(TAG, "FCM MESSAGE RECEIVED:" + p0!!.data)
        }

        var messageType = Integer.parseInt(p0?.data?.get(Constants.PUSH_MESSAGE_TYPE_KEY))
        var messageId = Integer.parseInt(p0?.data?.get(Constants.PUSH_MESSAGE_ID_KEY))

        when {
            messageType == Constants.PUSH_MESSAGE_TYPE_CHAT -> {
                Toast.makeText(this, R.string.newMessageTitle, Toast.LENGTH_LONG).show()
            }
            messageType == Constants.PUSH_MESSAGE_TYPE_TENDER -> {
                Toast.makeText(this, R.string.newTenderTitle, Toast.LENGTH_LONG).show()
            }
            messageType == Constants.PUSH_MESSAGE_TYPE_NEWS -> {
                Toast.makeText(this, R.string.newNewsPushTitle, Toast.LENGTH_LONG).show()
            }
            messageType == Constants.PUSH_MESSAGE_TYPE_TENDER_APPLY -> {
                Toast.makeText(this, R.string.appliedYourTender, Toast.LENGTH_LONG).show()
            }
        }


    }
}