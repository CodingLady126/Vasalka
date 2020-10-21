package co.gergely.vasalka.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ProgressBar
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.adapter.BookmarkExpandedListAdapter
import co.gergely.vasalka.api.ApiBookmark
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.inerfaces.OnHandleChat
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.inerfaces.OnHandleTender
import co.gergely.vasalka.model.Bookmark
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class BookmarkListFragment : Fragment() {

    private val debug = true
    private val TAG = "BookmarkFrgmnt"
    private var tenderListener: OnHandleTender? = null
    private var personListener: OnHandlePerson? = null
    private var chatListener: OnHandleChat? = null
    private lateinit var expandableListView: ExpandableListView
    private lateinit var progressBar: ProgressBar;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_bookmark_list, container, false)
        expandableListView = v.findViewById(R.id.bookmarkExpListView);
        progressBar = v.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        var bookmarkList = VasalkaApp.getSession().bookmarkList

        if (!bookmarkList.isEmpty()) {
            buildBookmarkList(bookmarkList)
        }

        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.getResult()!!.token;
                var bookmarkService = NetworkClient.getRetrofit(token).create(ApiBookmark::class.java)

                bookmarkService.getBookmarkList(VasalkaApp.getSession().myPerson.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        VasalkaApp.getSession().setBookmarkList(it)
                        buildBookmarkList(it)
                    }, {
                        progressBar.visibility = View.GONE
                        Utils.unknownError(activity!!)
                    })
            }
        }

        return v;
    }

    fun buildBookmarkList(it: List<Bookmark>) {

        var personList = ArrayList<Bookmark>()
        var tenderList = ArrayList<Bookmark>()
        var chatList = ArrayList<Bookmark>()

        for (bookmark in it) {
            if (bookmark.objectType.id == Constants.BOOKMARK_OBJECT_TYPE_PERSON_ID) {
                personList.add(bookmark)
            } else if (bookmark.objectType.id == Constants.BOOKMARK_OBJECT_TYPE_TENDER_ID) {
                tenderList.add(bookmark)
            } else if (bookmark.objectType.id == Constants.BOOKMARK_OBJECT_TYPE_CHAT_ID) {
                chatList.add(bookmark)
            }
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, "Bookmark: " + bookmark)
            }
        }

        var expandableListDetail = HashMap<String, List<Bookmark>>()
        expandableListDetail.put(
            activity!!.getString(R.string.bookmarkPersonObjectTypeLabel),
            personList
        )
        expandableListDetail.put(
            activity!!.getString(R.string.bookmarkTenderObjectTypeLabel),
            tenderList
        )
        expandableListDetail.put(activity!!.getString(R.string.bookmarkChatObjectTypeLabel), chatList)

        var expandableListTitle = ArrayList<String>(expandableListDetail.keys)

        var expandableListAdapter =
            BookmarkExpandedListAdapter(activity!!, expandableListTitle, expandableListDetail)

        expandableListView.setAdapter(expandableListAdapter)

        expandableListView.setOnChildClickListener(ExpandableListView.OnChildClickListener() { parent, v, groupPosition, childPosition, id ->

            var bookmark =
                expandableListDetail.get(expandableListTitle.get(groupPosition))!!.get(childPosition) as Bookmark

            if (expandableListTitle.get(groupPosition).equals(activity!!.getString(R.string.bookmarkPersonObjectTypeLabel))) {
                val person = bookmark.person;
                personListener!!.onPersonClicked(person)
            } else if (expandableListTitle.get(groupPosition).equals(activity!!.getString(R.string.bookmarkTenderObjectTypeLabel))) {
                val tender = bookmark.tender
                tenderListener!!.onTenderClicked(tender);
            } else if (expandableListTitle.get(groupPosition).equals(activity!!.getString(R.string.bookmarkChatObjectTypeLabel))) {
                val chatRoom = bookmark.chatRoom
                chatListener!!.onChatOpen(chatRoom)
            }

            return@OnChildClickListener false;
        })
        progressBar.visibility = View.GONE
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandleTender) {
            tenderListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandleTender")
        }

        if (context is OnHandlePerson) {
            personListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandlePerson")
        }

        if (context is OnHandleChat) {
            chatListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandleChat")
        }
    }

    override fun onDetach() {
        super.onDetach()
        personListener = null;
        tenderListener = null;
        chatListener = null;
    }

}
