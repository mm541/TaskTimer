package com.moa.tasktimer

import android.app.Application
import android.database.Cursor
import android.icu.util.GregorianCalendar
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

private const val TAG = "DurationViewModel"

class DurationReportViewModel(application: Application):AndroidViewModel(application) {
    private var databaseCursor = MutableLiveData<Cursor?>()
    val cursor:LiveData<Cursor?>
        get() = databaseCursor
    private var _displayWeek = true
    val displayWeek:Boolean
        get() = _displayWeek
     var sortOrder = SortOrder.NAME
        set(order) {
            if (order != field) {
                field = order
                applyFilter()
            }
            field = order
        }
    private val calendar:GregorianCalendar = GregorianCalendar()

    private val selection = "${DurationsContract.Columns.START_TIME} BETWEEN ? AND ?"
    private var selectionArgs = emptyArray<String>()

    init {
        Log.d(TAG,calendar.toString())
        applyFilter()
    }



    @OptIn(DelicateCoroutinesApi::class)
    private fun loadData() {
        val order = when(sortOrder) {
            SortOrder.NAME -> DurationsContract.Columns.NAME
            SortOrder.DESCRIPTION-> DurationsContract.Columns.DESCRIPTION
            SortOrder.START_DATE -> DurationsContract.Columns.START_TIME
            SortOrder.DURATION -> DurationsContract.Columns.DURATION
        }
        GlobalScope.launch {

            val cursor = getApplication<Application>().contentResolver.query(
                DurationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                order
            )
            databaseCursor.postValue(cursor)
        }
    }

    fun toggleWeek() {
        _displayWeek = !_displayWeek
        applyFilter()
    }

    fun getFilteredDate(): Serializable? {
        return calendar.time
    }

    fun setReportDate(year: Int, month: Int, day: Int) {
        if(calendar.get(GregorianCalendar.YEAR)!=year ||
            calendar.get(GregorianCalendar.MONTH)!=month||
            calendar.get(GregorianCalendar.DAY_OF_MONTH)!=day) {
            calendar.set(year,month,day)
            applyFilter()
        }

    }
//    1705775400 - 1706380199
    private fun applyFilter() {

        val cal = GregorianCalendar()
        cal.timeInMillis = calendar.timeInMillis
        if(displayWeek) {

            val firstDayOfWeek = calendar.firstDayOfWeek
            val startTime = cal.let {
                it.set(GregorianCalendar.DAY_OF_WEEK,firstDayOfWeek)
                it.set(GregorianCalendar.HOUR_OF_DAY, 0)
                it.set(GregorianCalendar.MINUTE, 0)
                it.set(GregorianCalendar.SECOND, 0)
                it.timeInMillis/1000
            }
            cal.apply {
                add(GregorianCalendar.DATE,6)
                set(GregorianCalendar.HOUR_OF_DAY,23)
                set(GregorianCalendar.MINUTE,59)
                set(GregorianCalendar.SECOND,59)
            }
            val endTime = cal.timeInMillis/1000
            Log.d(TAG,"$startTime-$endTime")
            selectionArgs = arrayOf(startTime.toString(),endTime.toString())
        }else {

            val startTime = cal.let {
                it.set(GregorianCalendar.HOUR_OF_DAY, 0)
                it.set(GregorianCalendar.MINUTE, 0)
                it.set(GregorianCalendar.SECOND, 0)
                it.timeInMillis/1000
            }
            val endTime  = cal.let{
                it.add(GregorianCalendar.DATE,1)
                it.timeInMillis/1000
            }
            Log.d(TAG,"$startTime-$endTime")
            selectionArgs = arrayOf(startTime.toString(),endTime.toString())
        }
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"Closing cursor")
        databaseCursor.value?.close()
    }
}