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
import java.util.*


class FilterFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        fun newInstance() = FilterFragment()
    }

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
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

    private fun initViews() {
        chkMostRecent.setOnCheckedChangeListener(this)
        chkDate.setOnCheckedChangeListener(this)
        chkToday.setOnCheckedChangeListener(this)
        chkBetween.setOnCheckedChangeListener(this)

        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)

        inputToday.text = dateTodayStr
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        initViews()
        subscribeViewModel()
    }

    override fun onCheckedChanged(v: CompoundButton?, check: Boolean) {
        if (check){
            var mode = Filter.MOST_RECENT
            when(v?.id){
                R.id.chkMostRecent -> {
                    chkDate.isChecked = false
                    chkToday.isChecked = false
                    chkBetween.isChecked = false
                    mode = Filter.MOST_RECENT
                }
                R.id.chkToday -> {
                    chkMostRecent.isChecked = false
                    chkDate.isChecked = false
                    chkBetween.isChecked = false
                    mode = Filter.TODAY
                }
                R.id.chkDate -> {
                    chkMostRecent.isChecked = false
                    chkToday.isChecked = false
                    chkBetween.isChecked = false
                    mode = Filter.DATE
                    val datePicker = DatePickerDialog(this.context, datePickerListener, year, month, day)
                    datePicker.setCancelable(false)
                    datePicker.setTitle("Select the date")
                    datePicker.show()
                }
                R.id.chkBetween -> {
                    chkMostRecent.isChecked = false
                    chkToday.isChecked = false
                    chkDate.isChecked = false
                    mode = Filter.BETWEEN
                    var datePicker = DatePickerDialog(this.context, dateInPickerListener, year, month, day)
                    datePicker.setCancelable(false)
                    datePicker.setTitle("Select the start date")
                    datePicker.show()
                    datePicker = DatePickerDialog(this.context, dateOutPickerListener, year, month, day)
                    datePicker.setCancelable(false)
                    datePicker.setTitle("Select the end date")
                    datePicker.show()
                }
            }
            updateFilter(mode)
        }
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
                mSelectFilter = it
                when (it.selectMode){
                    Filter.MOST_RECENT ->
                        chkMostRecent.isChecked = true
                    Filter.DATE ->
                        chkDate.isChecked = true
                    Filter.TODAY ->
                        chkToday.isChecked = true
                    Filter.BETWEEN ->
                        chkBetween.isChecked = true
                }
                inputBetweenIn.setText(mSelectFilter?.dateFrom)
                inputBetweenOut.setText(mSelectFilter?.dateTo)
                inputDate.setText(mSelectFilter?.date)
                numberTracks.setText(mSelectFilter?.numberRows)
            }else{
                numberTracks.setText("50")
            }
        })
    }

}
