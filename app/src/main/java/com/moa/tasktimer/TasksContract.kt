package com.moa.tasktimer

import android.provider.BaseColumns

object TasksContract {
    const val TABLE_NAME = "Tasks"

    object Columns {
        const val ID = BaseColumns._ID
        const val TASK_NAME= "Name"
        const val TASK_DESCRIPTION = "Description"
        const val SORT_ORDER = "SortOrder"
    }
}