package co.gergely.vasalka.fragment

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.MainActivity
import co.gergely.vasalka.api.ApiChat
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.async.SendFCM
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.dialog.PickEmojiDialog
import co.gergely.vasalka.dialog.ShowImageDialog
import co.gergely.vasalka.model.ChatMessage
import co.gergely.vasalka.model.ChatRoom
import co.gergely.vasalka.model.Image
import co.gergely.vasalka.model.Person
import co.gergely.vasalka.tools.MySecurePreferences
import co.gergely.vasalka.view.ChatMessageViewHolder
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chat.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

private const val ARG_PARAM1 = "partner"
private const val ARG_PARAM2 = "chatRoom"


class ChatFragment : Fragment() {

    private val TAG = "ChatFragment"
    private val debug = true;
    private var partner: Person? = null
    private var room: ChatRoom? = null
    private lateinit var emojiIcon: ImageView
    private lateinit var photoIcon: ImageView
    private lateinit var imageIcon: ImageView
    private lateinit var notifIcon: ImageView
    private lateinit var messageImage: ImageView
    private lateinit var clearMessageImage: ImageView
    private lateinit var textInput: TextInputEditText
    private lateinit var send: ImageView
    private lateinit var messageImageThumLayout: ConstraintLayout
    private lateinit var progressBar: ProgressBar;
    private var image: String? = null
    private var shoudPlaySoundEffect = true;
    private var layoutManager: LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.partner = it.get(ARG_PARAM1) as Person
            this.room = it.get(ARG_PARAM2) as ChatRoom
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.fragment_chat, container, false)
        init(v)
        progressBar.visibility = View.VISIBLE
        var securePreferences = MySecurePreferences(activity!!.application)


        if ((room!!.personOne.id != VasalkaApp.getSession().myPerson.id && !room!!.isPersonOneAlive)
            || (room!!.personTwo.id != VasalkaApp.getSession().myPerson.id && !room!!.isPersonTwoAlive)
        ) {

            var dialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.alertTitleAttention)
                .setMessage(activity!!.getString(R.string.partnerDeletedConversation, partner!!.name))
                .setCancelable(true)
                .create()
            dialog.show();
        }

        val listOfMessages = v.findViewById<RecyclerView>(R.id.list_of_messages)

        var ref = FirebaseDatabase.getInstance().getReference().child(Constants.FB_DB_CHILD_CHAT)
        var query =
            ref!!.child(room!!.fbId).child(Constants.FB_DB_CHILD_MESSAGES)
                .orderByChild("messageTime")
                .limitToLast(50);
        var options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java).build()

        var adapter = object : FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatMessageViewHolder {
                var view = LayoutInflater.from(p0.getContext())
                    .inflate(R.layout.item_chat_message, p0, false);

                return ChatMessageViewHolder(view);
            }

            override fun onDataChanged() {
                super.onDataChanged()
                progressBar.visibility = View.GONE
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                progressBar.visibility = View.GONE

            }

            override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int, model: ChatMessage) {

                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "Comming message: " + model)
                }

                if (model.personId.equals(VasalkaApp.getSession().myPerson.id)) {
                    holder.myMessageLayout.visibility = View.VISIBLE
                    holder.partnerMessageLayout.visibility = View.GONE
                    holder.myMessageDate.text = DateFormat.format("yyyy-MM-dd HH:mm:ss", model.messageTime)
                    holder.myMessageText.text = model.messageText
                    Glide.with(v).load(VasalkaApp.getSession().myPerson.photo)
                        .into(holder.myMessageProfileImage)

                    if (model.messageImage != null
                        && !model.messageImage.isEmpty()
                        && model.messageImage.contains("http")
                    ) {
                        holder.myCardView.visibility = View.VISIBLE
                        holder.myMessageImage.visibility = View.VISIBLE
                        holder.myMessageImage.getLayoutParams().height = 300;
                        holder.myMessageImage.requestLayout()
                        Glide.with(v).load(model.messageImage).into(holder.myMessageImage)
                        holder.myMessageImage.setOnClickListener {
                            ShowImageDialog(activity!!, model.messageImage).show()
                        }
                    } else {
                        holder.myCardView.visibility = View.GONE
                        holder.myMessageImage.visibility = View.GONE
                    }
                } else {
                    holder.partnerMessageLayout.visibility = View.VISIBLE
                    holder.myMessageLayout.visibility = View.GONE
                    holder.partnerMessageDate.text = DateFormat.format("yyyy-MM-dd HH:mm:ss", model.messageTime)
                    holder.partnerMessageText.text = model.messageText
                    Glide.with(v).load(partner!!.photo)
                        .into(holder.partnerMessageProfileImage)

                    holder.partnerMessageProfileImage.setOnClickListener {
                        (activity!! as MainActivity).goToShowProfile(partner!!);
                    }

                    if (model.messageImage != null
                        && !model.messageImage.isEmpty()
                        && model.messageImage.contains("http")
                    ) {
                        holder.partnerCardView.visibility = View.VISIBLE
                        holder.partnerMessageImage.visibility = View.VISIBLE
                        holder.partnerMessageImage.getLayoutParams().height = 300;
                        holder.partnerMessageImage.requestLayout()
                        Glide.with(v).load(model.messageImage).into(holder.partnerMessageImage)
                        holder.partnerMessageImage.setOnClickListener {
                            ShowImageDialog(activity!!, model.messageImage).show()
                        }
                    } else {
                        holder.partnerCardView.visibility = View.GONE
                        holder.partnerMessageImage.visibility = View.GONE
                    }
                }
            }
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                listOfMessages.smoothScrollToPosition(adapter.itemCount - 1);
                if (layoutManager != null) {
                    layoutManager!!.scrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                var friendlyMessageCount = adapter.getItemCount();
                var lastVisiblePosition = layoutManager!!.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                            lastVisiblePosition == (positionStart - 1))
                ) {
                    listOfMessages.scrollToPosition(positionStart);
                }
            }
        })

        listOfMessages.setHasFixedSize(true);
        layoutManager = LinearLayoutManager(activity!!)
        layoutManager!!.stackFromEnd = true
        layoutManager!!.reverseLayout = false
        listOfMessages.scrollToPosition(adapter.itemCount - 1)
        layoutManager!!.scrollToPosition(adapter.itemCount - 1)
        listOfMessages.adapter = adapter
        listOfMessages.setLayoutManager(layoutManager);

        adapter.startListening()

        clearMessageImage.setOnClickListener {
            var myImage = image!!.substring(image!!.lastIndexOf('/') + 1)


            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var chatService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)

                    chatService.deleteImage(true, VasalkaApp.getSession().myPerson.id, partner!!.id, myImage)
                        .enqueue(object : Callback<Void> {
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "Response:" + t.localizedMessage)
                                }
                            }

                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (BuildConfig.DEBUG && debug) {
                                    Log.d(TAG, "Response:" + response.message())
                                }
                            }

                        })
                }
            }
            image = ""
            messageImageThumLayout.visibility = View.GONE

        }

        send.setOnClickListener {
            sendMessage()
        }

        photoIcon.setOnClickListener {
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var photoFile: File? = null
            try {
                val activity = this.activity as MainActivity
                photoFile = activity.createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(activity!!, "co.gergely.vasalka.provider", photoFile)
                takePicture.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    photoURI
                )
            }
            activity!!.startActivityForResult(takePicture, Constants.PHOTO_FOR_CHAT_CAMERA)
        }

        imageIcon.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            activity!!.startActivityForResult(pickPhoto, Constants.PHOTO_FOR_CHAT_GALERY)
        }

        emojiIcon.setOnClickListener {
            PickEmojiDialog(activity!!, this).show()
        }

        notifIcon.setOnClickListener {
            if (shoudPlaySoundEffect) {
                shoudPlaySoundEffect = false;
                notifIcon.setImageResource(R.drawable.ic_notifications_off_black_24dp)
            } else {
                shoudPlaySoundEffect = true;
                notifIcon.setImageResource(R.drawable.ic_notifications_black_24dp)
            }
            securePreferences.putBoolean(room!!.fbId + "_" + Constants.CHAT_ROOM_SOUND_EFFECT_KEY, shoudPlaySoundEffect)
        }

        if (securePreferences.getBoolean(room!!.fbId + "_" + Constants.CHAT_ROOM_SOUND_EFFECT_KEY, true)) {
            shoudPlaySoundEffect = true;
            notifIcon.setImageResource(R.drawable.ic_notifications_black_24dp)
        } else {
            shoudPlaySoundEffect = false;
            notifIcon.setImageResource(R.drawable.ic_notifications_off_black_24dp)
        }

        return v;
    }

    fun setImage(path: String) {
        if (path != null && !path.isEmpty()) {


            var file = File(path)
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "FILE PATH: " + path)
            }
            var fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            var part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody)

            progressBar.visibility = View.VISIBLE

            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var chatService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)

                    chatService.addImage(part, true, VasalkaApp.getSession().myPerson.id, partner!!.id)
                        .enqueue(object : Callback<Image> {
                            override fun onFailure(call: Call<Image>, t: Throwable) {
                                if (BuildConfig.DEBUG && debug) {
                                    t.printStackTrace()
                                }
                            }

                            override fun onResponse(call: Call<Image>, response: Response<Image>) {
                                if (BuildConfig.DEBUG && debug) {
                                    image = (response.body() as Image).url
                                    messageImage.setImageURI(Uri.parse(path))
                                    messageImageThumLayout.visibility = View.VISIBLE
                                    messageImage.setOnClickListener {
                                        ShowImageDialog(it.context, path).show()
                                    }
                                    progressBar.visibility = View.GONE
                                }
                            }


                        })
                }
            }
        }

    }

    fun sendMessage() {
        val msg = ChatMessage();
        msg.setPerson(VasalkaApp.getSession().myPerson.id)
        msg.messageText = inputText.text.toString()
        if (image != null) {
            msg.messageImage = image
        }
        msg.messageTime = Date().time

        FirebaseDatabase.getInstance()
            .getReference(Constants.FB_DB_CHILD_CHAT)
            .child(room!!.fbId)
            .child(Constants.FB_DB_CHILD_MESSAGES)
            .push()
            .setValue(msg)

        if (shoudPlaySoundEffect) {
            var mp = MediaPlayer.create(activity!!, R.raw.type);
            mp.start();
        }

        if (partner!!.isAlertOnChat) {
            var title = activity!!.getString(R.string.newMessageTitle)
            var body = activity!!.getString(R.string.newMessageBody, VasalkaApp.getSession().myPerson.name)
            var messageType = Constants.PUSH_MESSAGE_TYPE_CHAT
            var messageId = VasalkaApp.getSession().myPerson.id
            SendFCM().execute(partner!!.token, title, body, messageType.toString(), messageId.toString())
        }

        // Clear the input
        inputText.setText("");
        image = "";
        messageImageThumLayout.visibility = View.GONE
    }

    fun insertEmoji(emojiStr: String) {
        var text = inputText.text.toString()
        inputText.setText(text + " " + emojiStr)
        var len = inputText.getText().toString().length
        inputText.setSelection(len)
    }

    fun init(v: View) {
        emojiIcon = v.findViewById(R.id.emojiIcon)
        photoIcon = v.findViewById(R.id.photoIcon)
        imageIcon = v.findViewById(R.id.imageIcon)
        notifIcon = v.findViewById(R.id.notifIcon)
        messageImageThumLayout = v.findViewById(R.id.messageImageThumbLayout)
        messageImage = v.findViewById(R.id.messageImage)
        clearMessageImage = v.findViewById(R.id.messageImageDelete)
        textInput = v.findViewById(R.id.inputText)
        send = v.findViewById(R.id.messageSend)
        progressBar = v.findViewById(R.id.progressBar)
    }

    companion object {
        @JvmStatic
        fun newInstance(partner: Person, chatRoom: ChatRoom) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, partner)
                    putSerializable(ARG_PARAM2, chatRoom)
                }
            }
    }
}
