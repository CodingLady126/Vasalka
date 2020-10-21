package co.gergely.vasalka.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.gergely.vasalka.R
import co.gergely.vasalka.activity.MainActivity
import co.gergely.vasalka.adapter.PersonServiceAdapter
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.model.SearchPerson
import java.util.*

private const val ARG_PARAM1 = "list"

class SearchResultFragment : Fragment() {
    private var param1: List<SearchPerson>? = null
    private var listener: OnHandlePerson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as List<SearchPerson>;
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_search_result, container, false)

        var itemDecorator = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        AppCompatResources.getDrawable(activity!!, R.drawable.item_decorator)?.let { itemDecorator.setDrawable(it) }

        // TODO Tesztelni
        Collections.sort(param1);

        var recycler = v.findViewById<RecyclerView>(R.id.personRecycler);
        var adapter = PersonServiceAdapter(activity?.applicationContext, param1, activity as MainActivity);
        recycler.adapter = adapter
        recycler.addItemDecoration(itemDecorator)
        recycler.setLayoutManager(LinearLayoutManager(activity));

        return v;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandlePerson) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnApplyTender")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: List<SearchPerson>) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, ArrayList(param1))
                }
            }
    }
}
