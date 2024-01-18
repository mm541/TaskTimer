package com.moa.tasktimer

import android.net.Uri

object CurrentTimingContract {
    const val TABLE_NAME = "vwCurrentTiming"
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

//    object Columns {
//        const val TIMING_ID = TimingsContract.Columns.ID
//        const val TASK_ID = TimingsContract.Columns.TIMING_TASK_ID
//        const val START_TIME = TimingsContract.Columns.TIMING_START_TIME
//        const val TASK_NAME = TasksContract.Columns.TASK_NAME
//    }

}