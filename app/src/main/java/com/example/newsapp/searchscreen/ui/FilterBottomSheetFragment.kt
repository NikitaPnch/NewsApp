package com.example.newsapp.searchscreen.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.example.newsapp.Constants
import com.example.newsapp.R
import com.example.newsapp.extensions.liveDataNotNull
import com.example.newsapp.extensions.toQueryDate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.fragment_filter.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.DateFormat
import java.util.*

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private val model: SearchScreenViewModel by sharedViewModel()
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

        setupSpinner()
        setupClicks()
        setupObservers()
    }

    // настраивает spinner
    private fun setupSpinner() {

        // переменная хранит список названий фильтров
        val arrayFilterNames = resources.getStringArray(R.array.filterNames)

        // создаем карту пар названий фильтров и их значения из апи
        val map = mapOf(
            arrayFilterNames[0] to Constants.PUBLISHED_AT,
            arrayFilterNames[1] to Constants.POPULARITY,
            arrayFilterNames[2] to Constants.RELEVANCY
        )

        // установка слушателя выбранного элемента фильтра
        spinner_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                model.processUiEvent(
                    UiEvent.OnSortedByChanged(
                        sortedBy = map[arrayFilterNames[position]] ?: Constants.PUBLISHED_AT
                    )
                )
            }
        }
    }

    // настраивает все клики в текущем фрагменте
    private fun setupClicks() {

        // кнопка "очистить фильтры"
        tv_clear.clicks().liveDataNotNull(this) {
            clearFilters()
            hideClearButton()
        }

        // кнопка "настройки даты от определенного числа"
        tv_from_date.clicks().liveDataNotNull(this) {
            showDatePickerDialog(calendar, getDatePickerListener(true))
        }

        // кнопка "настройки даты до определенного числа"
        tv_to_date.clicks().liveDataNotNull(this) {
            showDatePickerDialog(calendar, getDatePickerListener(false))
        }

        // кнопка "показать результаты с текущими фильтрами"
        tv_show_result.clicks().liveDataNotNull(this) {
            model.processUiEvent(UiEvent.OnShowResultClick)
            dismiss()
        }
    }

    // установка наблюдателей
    private fun setupObservers() {

        // слушает изменение даты от определенного числа
        model.viewState.observe(this) {
            it.fromDate?.let { fromDate ->
                if (fromDate.isNotBlank()) {
                    showClearButton()
                    val splittedString = it.fromDate.split("-")
                    setDateToTextView(
                        tv_from_date,
                        splittedString[0].toInt(),
                        splittedString[1].toInt(),
                        splittedString[2].toInt()
                    )
                }
            }
            it.toDate?.let { toDate ->
                if (toDate.isNotBlank()) {
                    showClearButton()
                    val splittedString = it.toDate.split("-")
                    setDateToTextView(
                        tv_to_date,
                        splittedString[0].toInt(),
                        splittedString[1].toInt(),
                        splittedString[2].toInt()
                    )
                }
            }
        }
    }

    // очищает фильтры
    private fun clearFilters() {
        tv_from_date.text = resources.getString(R.string.from)
        tv_to_date.text = resources.getString(R.string.to)
        spinner_filter.setSelection(0)
        model.processUiEvent(UiEvent.OnClickFiltersClear)
    }

    // получает настроенный слушатель выбора даты
    private fun getDatePickerListener(
        isFromDate: Boolean
    ): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            if (isFromDate) {
                model.processUiEvent(
                    UiEvent.OnFromDateChanged(
                        fromDate = toQueryDate(
                            year,
                            month,
                            dayOfMonth
                        )
                    )
                )
            } else {
                model.processUiEvent(
                    UiEvent.OnToDateChanged(
                        toDate = toQueryDate(
                            year,
                            month,
                            dayOfMonth
                        )
                    )
                )
            }
        }
    }

    // установка даты в textView на выбор
    private fun setDateToTextView(textView: TextView, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateFormat = DateFormat.getInstance()
        calendar.set(year, month - 1, dayOfMonth)
        textView.text = dateFormat.format(calendar.time).split(" ").first()
    }

    // показывает пользователю диалог с выбором даты
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

    // показывает пользователю кнопку очистить
    private fun showClearButton() {
        tv_clear.visibility = View.VISIBLE
    }

    // скрывает от пользователя кнопку очистить
    private fun hideClearButton() {
        tv_clear.visibility = View.GONE
    }
}