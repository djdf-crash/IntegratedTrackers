package com.spybike.integratedtrackers.views.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.enums.Filter
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.viewvmodel.MainViewModel
import kotlinx.android.synthetic.main.filter_fragment.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class FilterFragment : Fragment(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    companion object {
        fun newInstance() = FilterFragment()
    }

    private var selectedMode = Filter.MOST_RECENT
    private var mSelectFilter: FilterModel? = null
    private val calendar = Calendar.getInstance()
    private val dateToday = calendar.time
    private val dateMonthStr = SimpleDateFormat("yyyy-MM").format(dateToday)
    private val dateTodayStr = SimpleDateFormat("yyyy-MM-dd").format(dateToday)
    private lateinit var viewModel: MainViewModel

    private val dateInPickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener{ _, year, monthOfYear, dayOfMonth ->
        inputBetweenIn.setText("$year-${if (monthOfYear + 1 < 9) "0".plus(monthOfYear + 1) else monthOfYear + 1}-${if (dayOfMonth < 9) "0".plus(dayOfMonth) else dayOfMonth}")
    }

    private val dateOutPickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener{ _, year, monthOfYear, dayOfMonth ->
        inputBetweenOut.setText("$year-${if (monthOfYear + 1 < 9) "0".plus(monthOfYear + 1) else monthOfYear + 1}-${if (dayOfMonth < 9) "0".plus(dayOfMonth) else dayOfMonth}")
    }

    private val datePickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener{ _, year, monthOfYear, dayOfMonth ->
        inputDate.setText("$year-${if (monthOfYear + 1 < 9) "0".plus(monthOfYear + 1) else monthOfYear + 1}-${if (dayOfMonth < 9) "0".plus(dayOfMonth) else dayOfMonth}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_fragment, container, false)
    }

    override fun onPause() {
        super.onPause()
        updateFilter(selectedMode)
    }

    private fun initViews() {
        chkMostRecent.setOnCheckedChangeListener(this)
        chkDate.setOnCheckedChangeListener(this)
        chkToday.setOnCheckedChangeListener(this)
        chkBetween.setOnCheckedChangeListener(this)

        inputToday.text = dateTodayStr
        inputDate.setOnClickListener(this)
        inputBetweenIn.setOnClickListener(this)
        inputBetweenOut.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        initViews()
        subscribeViewModel()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.inputDate -> {
                showDatePicker(
                    this.context,
                    datePickerListener,
                    "Select the date",
                    inputDate.text.toString()
                )
                chkDate.isChecked = true
            }
            R.id.inputBetweenIn -> {
                showDatePicker(
                    this.context,
                    dateInPickerListener,
                    "Select the start date",
                    inputBetweenIn.text.toString()
                )
                chkBetween.isChecked = true
            }
            R.id.inputBetweenOut -> {
                showDatePicker(
                    this.context,
                    dateOutPickerListener,
                    "Select the end date",
                    inputBetweenOut.text.toString()
                )
                chkBetween.isChecked = true
            }
        }
    }

    override fun onCheckedChanged(v: CompoundButton?, check: Boolean) {
        if (check){
            when(v?.id){
                R.id.chkMostRecent -> {
                    chkDate.isChecked = false
                    chkToday.isChecked = false
                    chkBetween.isChecked = false
                    selectedMode = Filter.MOST_RECENT
                }
                R.id.chkToday -> {
                    chkMostRecent.isChecked = false
                    chkDate.isChecked = false
                    chkBetween.isChecked = false
                    selectedMode = Filter.TODAY
                }
                R.id.chkDate -> {
                    chkMostRecent.isChecked = false
                    chkToday.isChecked = false
                    chkBetween.isChecked = false
                    selectedMode = Filter.DATE
                    if (inputDate.text.isEmpty()){
                        showDatePicker(this.context, datePickerListener,"Select the date")
                    }
                }
                R.id.chkBetween -> {
                    chkMostRecent.isChecked = false
                    chkToday.isChecked = false
                    chkDate.isChecked = false
                    selectedMode = Filter.BETWEEN

                    if (inputBetweenIn.text.isEmpty()) {
                        showDatePicker(this.context, dateInPickerListener,"Select the start date")
                    }

                    if (inputBetweenOut.text.isEmpty()) {
                        showDatePicker(this.context, dateOutPickerListener,"Select the end date")
                    }
                }
            }
        }
    }

    private fun showDatePicker(ctx: Context?, listener: DatePickerDialog.OnDateSetListener, title: String, value: String = "") {

        val day: Int
        val month: Int
        val year: Int

        if (value.isEmpty()) {
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
        }else{
            val dateValue = LocalDate.parse(value)
            day = dateValue.dayOfMonth
            month = dateValue.monthValue - 1
            year = dateValue.year
        }

        val datePicker = DatePickerDialog(ctx!!, listener, year, month, day)
        datePicker.setCancelable(false)
        datePicker.setTitle(title)
        datePicker.show()
    }

    private fun updateFilter(mode: Filter) {
        if (mSelectFilter == null) {
            mSelectFilter = FilterModel()
        }
        mSelectFilter?.selectMode = mode
        if (numberTracks.text.isNotEmpty()) {
            mSelectFilter?.numberRows = numberTracks.text.toString()
        }
        mSelectFilter?.dateFrom = inputBetweenIn.text.toString()
        mSelectFilter?.dateTo = inputBetweenOut.text.toString()
        mSelectFilter?.date = inputDate.text.toString()
        mSelectFilter?.lastChange = dateToday
        mSelectFilter?.nameFiltered = "${mode.name} $dateToday"
        viewModel.applyFilter(this.context, mSelectFilter!!)
    }

    private fun subscribeViewModel() {
        viewModel.getFilterLiveData(activity as Context)?.observe(this, androidx.lifecycle.Observer {
            if (it != null){
                if (it.id != mSelectFilter?.id) {
                    mSelectFilter = it
                    initFilter()
                }
            }else{
                mSelectFilter = FilterModel()
                mSelectFilter?.selectMode = Filter.MOST_RECENT
                mSelectFilter?.numberRows = "50"
                mSelectFilter?.dateFrom = dateTodayStr
                mSelectFilter?.dateTo = dateTodayStr
                mSelectFilter?.date = dateTodayStr
                mSelectFilter?.lastChange = dateToday
                mSelectFilter?.nameFiltered = "${Filter.MOST_RECENT.name} $dateToday"
                initFilter()
            }
        })
    }

    private fun initFilter() {

        inputBetweenIn.setText(mSelectFilter?.dateFrom)
        inputBetweenOut.setText(mSelectFilter?.dateTo)
        inputDate.setText(mSelectFilter?.date)
        numberTracks.setText(mSelectFilter?.numberRows)

        when (mSelectFilter?.selectMode) {
            Filter.MOST_RECENT ->
                chkMostRecent.isChecked = true
            Filter.DATE ->
                chkDate.isChecked = true
            Filter.TODAY ->
                chkToday.isChecked = true
            Filter.BETWEEN ->
                chkBetween.isChecked = true
        }
    }

}
