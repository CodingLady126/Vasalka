package co.gergely.vasalka.fragment

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import co.gergely.vasalka.BuildConfig
import co.gergely.vasalka.R
import co.gergely.vasalka.VasalkaApp
import co.gergely.vasalka.api.ApiSearch
import co.gergely.vasalka.api.ApiTender
import co.gergely.vasalka.api.NetworkClient
import co.gergely.vasalka.common.Constants
import co.gergely.vasalka.dialog.FileUploadProgressDialog
import co.gergely.vasalka.dialog.PickImageDialog
import co.gergely.vasalka.dialog.ShowImageDialog
import co.gergely.vasalka.inerfaces.OnHandlePerson
import co.gergely.vasalka.model.*
import co.gergely.vasalka.tools.Utils
import com.google.firebase.auth.FirebaseAuth
import com.xw.repo.BubbleSeekBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SearchFragment : Fragment() {

    private val TAG = "SearchFragment";
    private val debug = true
    private lateinit var searchButton: Button
    private lateinit var tenderBtn1: Button
    private lateinit var tenderBtn2: Button
    private lateinit var tenderLayout: LinearLayout
    private lateinit var ironingToggle: ToggleButton
    private lateinit var washingToggle: ToggleButton
    private lateinit var cleaningToggle: ToggleButton
    private lateinit var gardeningToggle: ToggleButton
    private lateinit var ironingLabel: TextView
    private lateinit var washingLabel: TextView
    private lateinit var cleaningLabel: TextView
    private lateinit var gardeningLabel: TextView
    private lateinit var cityAdapter: ArrayAdapter<City>
    private lateinit var citySpinner: Spinner
    private lateinit var areaSeekBar: BubbleSeekBar
    private lateinit var progressBar: ProgressBar
    private lateinit var myPerson: Person
    private var isTender = false;
    private lateinit var horizontalList: LinearLayout
    private lateinit var addImageView: ImageView
    private lateinit var description: EditText
    private lateinit var image1Layout: ConstraintLayout
    private lateinit var image1Close: ImageView
    private lateinit var image1: ImageView
    private var image1Path = ""
    private lateinit var image2Layout: ConstraintLayout
    private lateinit var image2Close: ImageView
    private lateinit var image2: ImageView
    private var image2Path = ""
    private lateinit var image3Layout: ConstraintLayout
    private lateinit var image3Close: ImageView
    private lateinit var image3: ImageView
    private var image3Path = ""
    private lateinit var titleLabel: TextView
    private var listener: OnHandlePerson? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_search, container, false)
        init(v)

        myPerson = VasalkaApp.getSession().myPerson

        activity?.title = "Search"

        tenderBtn1.setOnClickListener {
            titleLabel.setText(R.string.searchTitleTender)
            showTender()
        }

        ironingToggle.setOnClickListener {
            ironingClicker()
        }

        ironingLabel.setOnClickListener {
            if (ironingToggle.isChecked) {
                ironingToggle.isChecked = false
            } else {
                ironingToggle.isChecked = true
            }
            ironingClicker()
        }

        washingToggle.setOnClickListener {
            washingClicker()
        }
        washingLabel.setOnClickListener {
            if (washingToggle.isChecked) {
                washingToggle.isChecked = false
            } else {
                washingToggle.isChecked = true
            }
            washingClicker()
        }

        cleaningToggle.setOnClickListener {
            cleaningClicker()
        }
        cleaningLabel.setOnClickListener {
            if (cleaningToggle.isChecked) {
                cleaningToggle.isChecked = false
            } else {
                cleaningToggle.isChecked = true
            }
            cleaningClicker()
        }

        gardeningToggle.setOnClickListener {
            gardeningClicker()
        }
        gardeningLabel.setOnClickListener {
            if (gardeningToggle.isChecked) {
                gardeningToggle.isChecked = false
            } else {
                gardeningToggle.isChecked = true
            }
            gardeningClicker()
        }

        cityAdapter = ArrayAdapter(
            activity!!.applicationContext,
            android.R.layout.simple_spinner_item,
            VasalkaApp.getSession().cityList
        )
        citySpinner.adapter = cityAdapter

        if (myPerson.city != null && cityAdapter != null && !cityAdapter.isEmpty) {
            var i = 0;
            citySpinner.post(Runnable {
                for (city in VasalkaApp.getSession().cityList) {
                    if (city.id == myPerson.city.id) {
                        citySpinner.setSelection(i)
                    }
                    i++
                }
            })
        }

        searchButton.setOnClickListener {
            titleLabel.setText(R.string.searchTitleSearch)
            progressBar.visibility = View.VISIBLE
            val search = Search();
            search.city = citySpinner.selectedItem as City
            search.serviceArea = areaSeekBar.progress;
            search.personId = myPerson.id
            var arrayList = ArrayList<Service>()
            if (ironingToggle.isChecked) {
                arrayList.add(Utils.getService(Constants.SERVICE_IRONING_ID)!!)
            }
            if (washingToggle.isChecked) {
                arrayList.add(Utils.getService(Constants.SERVICE_WASHING_ID)!!)
            }
            if (cleaningToggle.isChecked) {
                arrayList.add(Utils.getService(Constants.SERVICE_CLEANING_ID)!!)
            }
            if (gardeningToggle.isChecked) {
                arrayList.add(Utils.getService(Constants.SERVICE_GARDENING_ID)!!)
            }
            search.serviceList = arrayList

            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;
                    var searchService = NetworkClient.getRetrofit(token).create(ApiSearch::class.java)

                    searchService.doSearch(search)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d(TAG, "result size: " + it.size)
                            progressBar.visibility = View.GONE
                            if (it.size == 0) {
                                var builder = AlertDialog.Builder(activity)
                                builder.setMessage(activity!!.getString(R.string.noResultFound))
                                builder.setPositiveButton(activity!!.getString(R.string.OK)) { dialog, which ->
                                    dialog.dismiss()
                                }
                                var dialog = builder.create()
                                dialog.show()
                            } else {
                                for (person in it) {
                                    Log.d(TAG, "Person: " + person.name + ", city=" + person.city.name)
                                }
                                listener!!.onShowSearchResult(it)
                            }
                        }, {
                            progressBar.visibility = View.GONE
                            if (BuildConfig.DEBUG && debug) {
                                Log.e(TAG, "Search Result Error: " + it.localizedMessage)
                            }
                            it.printStackTrace()
                            Utils.unknownError(activity!!)
                        })
                }
            }
        }

        image1Close.setOnClickListener {
            image1Path = ""
            image1Layout.visibility = View.GONE
        }

        image2Close.setOnClickListener {
            image2Path = ""
            image2Layout.visibility = View.GONE
        }

        image3Close.setOnClickListener {
            image3Path = ""
            image3Layout.visibility = View.GONE
        }

        addImageView.setOnClickListener {
            if (image1Path.isEmpty() || image2Path.isEmpty() || image3Path.isEmpty()) {
                PickImageDialog(activity, Constants.PHOTO_FOR_TENDER).show()
                if (image1Path.isEmpty() && image2Path.isEmpty() && image3Path.isEmpty()) {
                    Toast.makeText(activity!!, R.string.only3TenderImageMsg, Toast.LENGTH_SHORT).show()
                }
            } else {
                Utils.showMessage(v, activity!!.getString(R.string.only3TenderImageMsg))
            }
        }

        tenderBtn2.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            var tender = Tender();
            tender.alive = true;
            tender.person = myPerson
            tender.serviceArea = areaSeekBar.progress
            tender.city = citySpinner.selectedItem as City
            tender.description = description.text.toString()

            if (ironingToggle.isChecked) {
                tender.addService(Utils.getService(Constants.SERVICE_IRONING_ID)!!)
            }
            if (washingToggle.isChecked) {
                tender.addService(Utils.getService(Constants.SERVICE_WASHING_ID)!!)
            }
            if (cleaningToggle.isChecked) {
                tender.addService(Utils.getService(Constants.SERVICE_CLEANING_ID)!!)
            }
            if (gardeningToggle.isChecked) {
                tender.addService(Utils.getService(Constants.SERVICE_GARDENING_ID)!!)
            }

            FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.getResult()!!.token;

                    var tenderService = NetworkClient.getRetrofit(token).create(ApiTender::class.java)

                    tenderService.createTender(tender)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (BuildConfig.DEBUG && debug) {
                                Log.d(TAG, "MEGJÖTT A TENDER")
                            }
                            progressBar.visibility = View.GONE
                            FileUploadProgressDialog(
                                activity,
                                it.id,
                                myPerson.id,
                                image1Path,
                                image2Path,
                                image3Path
                            ).show()
                        }, {
                            it.printStackTrace()
                            Log.d(TAG, "ERROR: " + it.localizedMessage)
                        })
                }
            }
        }

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setFocusableInTouchMode(true);
        view.requestFocus()

        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() === KeyEvent.ACTION_UP) {
                    if (isTender) {
                        hideTender()
                    } else {
                        activity!!.onBackPressed()
                    }
                    return true
                }
                return false
            }
        })
    }


    override fun onResume() {
        super.onResume()

        if (ironingToggle.isChecked) {
            ironingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            ironingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }

        if (washingToggle.isChecked) {
            washingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            washingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }

        if (cleaningToggle.isChecked) {
            cleaningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            cleaningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }

        if (gardeningToggle.isChecked) {
            gardeningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            gardeningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }

    }


    fun addTenderImage(path: String) {
        if (path != null && !path.isEmpty()) {
            when {
                image1Path.isEmpty() -> {
                    image1.setImageURI(Uri.parse(path))
                    image1Layout.visibility = View.VISIBLE
                    image1Path = path
                    image1.setOnClickListener {
                        ShowImageDialog(it.context, path).show()
                    }

                }
                image2Path.isEmpty() -> {
                    image2.setImageURI(Uri.parse(path))
                    image2Layout.visibility = View.VISIBLE
                    image2Path = path
                    image2.setOnClickListener {
                        var dialog = ShowImageDialog(it.context, path)
                        dialog.show()
                    }
                }

                image3Path.isEmpty() -> {
                    image3.setImageURI(Uri.parse(path))
                    image3Layout.visibility = View.VISIBLE
                    image3Path = path
                    image3.setOnClickListener {
                        var dialog = ShowImageDialog(it.context, path)
                        dialog.show()
                    }
                }
            }
        }
    }

    private fun init(v: View) {
        searchButton = v.findViewById<Button>(R.id.searchBtn)
        tenderBtn1 = v.findViewById<Button>(R.id.tenderBtn1)
        tenderBtn2 = v.findViewById(R.id.tenderBtn2)
        tenderLayout = v.findViewById<LinearLayout>(R.id.tenderLayout)
        ironingToggle = v.findViewById(R.id.toggleIroning)
        washingToggle = v.findViewById(R.id.toggleWashing)
        cleaningToggle = v.findViewById(R.id.toggleCleaning)
        gardeningToggle = v.findViewById(R.id.toggleGardening)
        ironingLabel = v.findViewById(R.id.ironingLabel)
        washingLabel = v.findViewById(R.id.washingLabel)
        cleaningLabel = v.findViewById(R.id.cleaningLabel)
        gardeningLabel = v.findViewById(R.id.gardeningLabel)
        citySpinner = v.findViewById(R.id.citySpinner)
        areaSeekBar = v.findViewById(R.id.areaSeekBar)
        progressBar = v.findViewById(R.id.progressBar)
        horizontalList = v.findViewById(R.id.horizontalList)
        addImageView = v.findViewById(R.id.addImageView)
        description = v.findViewById(R.id.desc)
        image1Layout = v.findViewById(R.id.image1_layout)
        image1Close = v.findViewById(R.id.image1_close)
        image1 = v.findViewById(R.id.image1)
        image2Layout = v.findViewById(R.id.image2_layout)
        image2Close = v.findViewById(R.id.image2_close)
        image2 = v.findViewById(R.id.image2)
        image3Layout = v.findViewById(R.id.image3_layout)
        image3Close = v.findViewById(R.id.image3_close)
        image3 = v.findViewById(R.id.image3)
        titleLabel = v.findViewById(R.id.titleLabel)
    }

    fun showTender() {
        isTender = true;
        searchButton.visibility = View.GONE;
        tenderBtn1.visibility = View.GONE;
        tenderLayout.visibility = View.VISIBLE;
    }

    fun hideTender() {
        isTender = false;
        tenderLayout.visibility = View.GONE;
        searchButton.visibility = View.VISIBLE;
        tenderBtn1.visibility = View.VISIBLE;
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHandlePerson) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnHandlePerson")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun ironingClicker() {
        if (ironingToggle.isChecked) {
            ironingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            ironingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }
    }

    private fun washingClicker() {
        if (washingToggle.isChecked) {
            washingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            washingToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }
    }

    private fun cleaningClicker() {
        if (cleaningToggle.isChecked) {
            cleaningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            cleaningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }
    }

    private fun gardeningClicker() {
        if (gardeningToggle.isChecked) {
            gardeningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_gold
                )
            )
        } else {
            gardeningToggle.setBackground(
                AppCompatResources.getDrawable(
                    activity!!,
                    R.drawable.circle_background_dark
                )
            )
        }
    }
}
