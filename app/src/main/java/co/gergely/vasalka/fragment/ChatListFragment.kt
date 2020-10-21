package co.gergely.vasalka.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.activity.SearchPersonActivity
import co.gergely.vasalka.adapter.ChatListAdapter
import co.gergely.vasalka.api.ApiChat
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.inerfaces.OnHandleChat
import co.gergely.vasalka.model.ChatRoom
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ChatListFragment : Fragment() {

    private val debug = false;
    private val TAG = "ChatListFragment"
    private var listener: OnHandleChat? = null
    private lateinit var chatListRecyclerView: RecyclerView;
    private lateinit var addChatBtn: FloatingActionButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_chat_list, container, false)

        chatListRecyclerView = v.findViewById(R.id.chatListRecycler)
        addChatBtn = v.findViewById(R.id.addChatBtn)
        progressBar = v.findViewById(R.id.progressBar)

        buildChatList(VasalkaApp.getSession().chatRoomList)

        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var chatRoomService = NetworkClient.getRetrofit(token).create(ApiChat::class.java)

                chatRoomService.getList(VasalkaApp.getSession().myPerson.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        progressBar.visibility = View.VISIBLE
                        buildChatList(it);
                    }, {
                        progressBar.visibility = View.GONE
                        Utils.unknownError(activity!!)
                    })
            }
        }

        addChatBtn.setOnClickListener {
            activity!!.startActivityForResult(
                Intent(activity!!, SearchPersonActivity::class.java),
                Constants.REQUEST_SEARCH_PERSON
            )
        }

        return v
    }


    private fun buildChatList(list: List<ChatRoom>) {

        val itemDecorator = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        AppCompatResources.getDrawable(activity!!, R.drawable.item_decorator)
            ?.let {
                itemDecorator.setDrawable(it)
            }

        val lista = ArrayList<ChatRoom>();
        for (room in list) {
            if (room.personOne.id == VasalkaApp.getSession().myPerson.id && room.isPersonOneAlive) {
                lista.add(room)
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "PERSON 1: " + room.personOne.name + " ->" + room.isPersonOneAlive)
                }
            } else if (room.personTwo.id == VasalkaApp.getSession().myPerson.id && room.isPersonTwoAlive) {
                lista.add(room)
                if (BuildConfig.DEBUG && debug) {
                    Log.d(TAG, "PERSON 2: " + room.personTwo.name + " ->" + room.isPersonTwoAlive)
                }
            }
        }

        val adapter = ChatListAdapter(activity!!, listener, lista)
        chatListRecyclerView.adapter = adapter
        chatListRecyclerView.addItemDecoration(itemDecorator)
        chatListRecyclerView.setLayoutManager(LinearLayoutManager(activity));
        adapter.notifyDataSetChanged()
        progressBar.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandleChat) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandleChat")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}
