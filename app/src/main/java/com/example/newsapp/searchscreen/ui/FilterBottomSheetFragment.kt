package com.example.newsapp.searchscreen.ui

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.Constants
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentFilterBinding
import com.example.newsapp.extensions.toQueryDate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.DateFormat
import java.util.*

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val INTERVAL_MONTH = AlarmManager.INTERVAL_DAY * 28
    }

    private val binding by viewBinding(FragmentFilterBinding::bind)

    private val model: SearchScreenViewModel by sharedViewModel()
    private val calendar = Calendar.getInstance(TimeZone.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        with(binding) {
            // кнопка "очистить фильтры"
            tvClear.setOnClickListener {
                clearFilters()
                tvClear.isVisible = false
            }

            // кнопка "настройки даты от определенного числа"
            tvFromDate.setOnClickListener {
                showDatePickerDialog(calendar, getDatePickerListener(true))
            }

            // кнопка "настройки даты до определенного числа"
            tvToDate.setOnClickListener {
                showDatePickerDialog(calendar, getDatePickerListener(false))
            }

            // кнопка "показать результаты с текущими фильтрами"
            tvShowResult.setOnClickListener {
                model.processUiEvent(UiEvent.OnShowResultClick)
                dismiss()
            }
        }
    }

    // установка наблюдателей
    private fun setupObservers() {
        with(binding) {
            // слушает изменение даты от определенного числа
            model.viewState.observe(this@FilterBottomSheetFragment) {
                it.fromDate?.let { fromDate ->
                    if (fromDate.isNotBlank()) {
                        tvClear.isVisible = true
                        val splittedString = it.fromDate.split("-")
                        setDateToTextView(
                            tvFromDate,
                            splittedString[0].toInt(),
                            splittedString[1].toInt(),
                            splittedString[2].toInt()
                        )
                    }
                }
                it.toDate?.let { toDate ->
                    if (toDate.isNotBlank()) {
                        tvClear.isVisible = true
                        val splittedString = it.toDate.split("-")
                        setDateToTextView(
                            tvToDate,
                            splittedString[0].toInt(),
                            splittedString[1].toInt(),
                            splittedString[2].toInt()
                        )
                    }
                }
            }
        }
    }

    // очищает фильтры
    private fun clearFilters() {
        with(binding) {
            tvFromDate.text = resources.getString(R.string.from)
            tvToDate.text = resources.getString(R.string.to)
            spinnerFilter.setSelection(0)
            model.processUiEvent(UiEvent.OnClickFiltersClear)
        }
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
            datePicker.minDate = calendar.timeInMillis - INTERVAL_MONTH
            datePicker.maxDate = calendar.timeInMillis
        }.show()
    }
}