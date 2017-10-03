package com.gregmcgowan.fivesorganiser.core.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

class DatePickerFragment : DialogFragment() {

    companion object {
        fun newInstance(year: Int, month: Int, day: Int): DatePickerFragment {
            val datePickerFragment = DatePickerFragment()
            val args = Bundle()

            args.putInt("year", year)
            args.putInt("month", month)
            args.putInt("date", day)
            datePickerFragment.arguments = args

            return datePickerFragment
        }
    }

    var dateListener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(activity,
                dateListener,
                arguments.getInt("year"),
                arguments.getInt("month"),
                arguments.getInt("date"))
    }
}

class TimePickerFragment : DialogFragment() {

    companion object {
        fun newInstance(hour: Int, minute: Int, is24Hour: Boolean): TimePickerFragment {
            val datePickerFragment = TimePickerFragment()
            val args = Bundle()
            args.putInt("hour", hour)
            args.putInt("minute", minute)
            args.putBoolean("is24Hour", is24Hour)
            datePickerFragment.arguments = args

            return datePickerFragment
        }
    }

    var timePickerListener: TimePickerDialog.OnTimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity,
                timePickerListener,
                arguments.getInt("hour"),
                arguments.getInt("minute"),
                arguments.getBoolean("is24Hour")
        )
    }
}