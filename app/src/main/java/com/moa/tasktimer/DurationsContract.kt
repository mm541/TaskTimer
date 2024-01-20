package com.moa.tasktimer

import android.net.Uri

object DurationsContract {
    const val TABLE_NAME = "vwTaskDurations"
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

    object Columns {
        const val NAME = TasksContract.Columns.TASK_NAME
        const val DESCRIPTION = TasksContract.Columns.TASK_DESCRIPTION
        const val START_TIME = TimingsContract.Columns.TIMING_START_TIME
        const val START_DATE = "StartDate"
        const val DURATION = TimingsContract.Columns.TIMING_DURATION
    }

}