package com.moa.tasktimer

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TasksContract {
    const val TABLE_NAME = "Tasks"
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

    object Columns {
        const val ID = BaseColumns._ID
        const val TASK_NAME= "Name"
        const val TASK_DESCRIPTION = "Description"
        const val SORT_ORDER = "SortOrder"
    }
    fun getId(uri:Uri):Long = ContentUris.parseId(uri)

    fun buildUriFromId(id:Long) = ContentUris.withAppendedId(CONTENT_URI,id)
}