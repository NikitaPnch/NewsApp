package com.example.newsapp.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.example.newsapp.R
import com.example.newsapp.api.API
import com.example.newsapp.extensions.liveDataNotNull
import com.example.newsapp.extensions.observeNotNull
import com.example.newsapp.extensions.toQueryDate
import com.example.newsapp.viewmodel.MainActions
import com.example.newsapp.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_filter.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.DateFormat
import java.util.*

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private val model by sharedViewModel<MainViewModel>()
    private val calendar = Calendar.getInstance(TimeZone.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrayString = resources.getStringArray(R.array.filterNames)
        val map = mapOf(
            arrayString[0] to API.PUBLISHED_AT,
            arrayString[1] to API.POPULARITY,
            arrayString[2] to API.RELEVANCY
        )

        spinner_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                model.setSortBy(map[arrayString[position]] ?: error("No Such Filter"))
            }
        }

        tv_clear.clicks().liveDataNotNull(this) {
            clearFilters()
            hideClearButton()
        }

        model.fromDate.observeNotNull(this) {
            if (it.isNotBlank()) {
                showClearButton()
                val splittedString = it.split("-")
                setDateToTextView(
                    tv_from_date,
                    splittedString[0].toInt(),
                    splittedString[1].toInt(),
                    splittedString[2].toInt()
                )
            }
        }

        model.toDate.observeNotNull(this) {
            if (it.isNotBlank()) {
                showClearButton()
                val splittedString = it.split("-")
                setDateToTextView(
                    tv_to_date,
                    splittedString[0].toInt(),
                    splittedString[1].toInt(),
                    splittedString[2].toInt()
                )
            }
        }

        tv_from_date.clicks().liveDataNotNull(this) {
            showDatePickerDialog(calendar, getDatePickerListener(true))
        }

        tv_to_date.clicks().liveDataNotNull(this) {
            showDatePickerDialog(calendar, getDatePickerListener(false))
        }

        tv_show_result.clicks().liveDataNotNull(this) {
            dismiss()
            model.send { MainActions.SearchNews() }
        }
    }

    private fun clearFilters() {
        tv_from_date.text = "От"
        tv_to_date.text = "До"
        spinner_filter.setSelection(0)
        model.sortBy.value = API.PUBLISHED_AT
        model.fromDate.value = null
        model.toDate.value = null
    }

    private fun getDatePickerListener(
        isFromDate: Boolean
    ): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            if (isFromDate) {
                model.setFromDate(toQueryDate(year, month, dayOfMonth))
            } else {
                model.setToDate(toQueryDate(year, month, dayOfMonth))
            }
        }
    }

    private fun setDateToTextView(textView: TextView, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateFormat = DateFormat.getInstance()
        calendar.set(year, month - 1, dayOfMonth)
        textView.text = dateFormat.format(calendar.time).split(" ").first()
    }

    private fun showDatePickerDialog(
        calendar: Calendar,
        listener: DatePickerDialog.OnDateSetListener
    ) {
        DatePickerDialog(
            this.requireContext(),
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = calendar.timeInMillis
        }.show()
    }

    private fun showClearButton() {
        tv_clear.visibility = View.VISIBLE
    }

    private fun hideClearButton() {
        tv_clear.visibility = View.GONE
    }
}