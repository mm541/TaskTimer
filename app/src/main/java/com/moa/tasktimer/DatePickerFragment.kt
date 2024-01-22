package com.moa.tasktimer

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Date

const val DATE_PICKER_ID = "id"
const val DATE_PICKER_TITLE = "title"
const val DATE_PICKER_DATE ="date"
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val cal = GregorianCalendar()
    private var dialogId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var title:String?=null
        val arguments = arguments

        if(arguments != null) {
               title = arguments.getString(DATE_PICKER_TITLE,"")
                dialogId = arguments.getInt(DATE_PICKER_ID,0)
                val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arguments.getSerializable(DATE_PICKER_DATE,Date::class.java)
                } else {
                    arguments.getSerializable(DATE_PICKER_DATE)
                }
            if(date != null) {
                cal.time = date as Date?
            }

        }

        val dpd = DatePickerDialog(requireContext(),this,cal.get(GregorianCalendar.YEAR),cal.get(GregorianCalendar.MONTH),cal.get(GregorianCalendar.DAY_OF_MONTH))
        if(title != null)
            dpd.setTitle(title)
        return dpd
    }
    interface OnDateSet {
        fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context !is OnDateSet) {
            throw IllegalStateException("callee activity must implement OnDateSet interface")
        }

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        view.tag = dialogId
        (context as OnDateSet).onDateSet(view,year, month, day)
    }

}