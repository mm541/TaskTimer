package com.moa.tasktimer

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DurationReportViewModel(application: Application):AndroidViewModel(application) {
    private var databaseCursor = MutableLiveData<Cursor?>()
    val cursor:LiveData<Cursor?>
        get() = databaseCursor

    private var sortOrder = SortOrder.NAME
        set(order) {
            if (order != field) {
                field = order
                loadData()
            }
            field = order
        }
    init {
        loadData()
    }

    private val selection = "${DurationsContract.Columns.DURATION} BETWEEN ? AND ?"
    private var selectionArgs = arrayOf("1598333333","23823834345")

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
                null,
                null,
                order
            )
            databaseCursor.postValue(cursor)
        }
    }
}