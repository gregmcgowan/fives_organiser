package com.gregmcgowan.fivesorganiser.core.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment

class DatePickerFragment : DialogFragment() {

    companion object {
        private const val YEAR = "year"
        private const val MONTH = "month"
        private const val DATE = "date"

        fun newInstance(year: Int, month: Int, day: Int): DatePickerFragment {
            val datePickerFragment = DatePickerFragment()
            val args = Bundle()

            args.putInt(YEAR, year)
            args.putInt(MONTH, month)
            args.putInt(DATE, day)
            datePickerFragment.arguments = args

            return datePickerFragment
        }
    }

    var dateListener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            return DatePickerDialog(activity as Context,
                    dateListener,
                    it.getInt(YEAR),
                    it.getInt(MONTH),
                    it.getInt(DATE))
        }
        throw IllegalStateException()
    }
}

class TimePickerFragment : DialogFragment() {

    companion object {
        private const val HOUR = "hour"
        private const val MINUTE = "minute"
        private const val IS_24_HOUR = "is24Hour"

        fun newInstance(hour: Int, minute: Int, is24Hour: Boolean): TimePickerFragment {
            val datePickerFragment = TimePickerFragment()
            val args = Bundle()
            args.putInt(HOUR, hour)
            args.putInt(MINUTE, minute)
            args.putBoolean(IS_24_HOUR, is24Hour)
            datePickerFragment.arguments = args

            return datePickerFragment
        }
    }

    var timePickerListener: TimePickerDialog.OnTimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            return TimePickerDialog(activity,
                    timePickerListener,
                    it.getInt(HOUR),
                    it.getInt(MINUTE),
                    it.getBoolean(IS_24_HOUR))

        }
        throw IllegalStateException()

    }
}