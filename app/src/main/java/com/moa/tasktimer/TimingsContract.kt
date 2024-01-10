package com.moa.tasktimer

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TimingsContract {
    const val TABLE_NAME = "Timings"
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)
    object Columns {
        const val ID = BaseColumns._ID
        const val TIMING_TASK_ID = "TaskId"
        const val TIMING_START_TIME = "StartTime"
        const val TIMING_DURATION = "Duration"
    }
    fun getId(uri: Uri) = ContentUris.parseId(uri)
    fun buildUriFromId(id: Long) = ContentUris.withAppendedId(CONTENT_URI,id)
}