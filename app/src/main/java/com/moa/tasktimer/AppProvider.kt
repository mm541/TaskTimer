package com.moa.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

private const val TAG = "AppProvider"

private const val CONTENT_AUTHORITY = "com.moa.tasktimer.provider"

private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201

private const val TASK_DURATION = 400
private const val TASK_DURATION_ID = 401

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://${CONTENT_AUTHORITY}")


class AppProvider:ContentProvider() {
    private val uriMatcher:UriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher():UriMatcher {
        Log.d(TAG,"buildUriMatcher starts")

        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(CONTENT_AUTHORITY,TasksContract.TABLE_NAME, TASKS)
        matcher.addURI(CONTENT_AUTHORITY,"${TasksContract.TABLE_NAME}/#", TASKS_ID)

//        matcher.addURI(CONTENT_AUTHORITY,"${TimingsContract.TABLE_NAME}",TIMINGS)
//        matcher.addURI(CONTENT_AUTHORITY,"${TimingsContract.TABLE_NAME}/#",TIMINGS_ID)
//
//        matcher.addURI(CONTENT_AUTHORITY,"${DurationsContract.TABLE_NAME}",TASK_DURATION)
//        matcher.addURI(CONTENT_AUTHORITY,"${DurationsContract.TABLE_NAME}/#",TASK_DURATION_ID)

        return matcher
    }
    override fun onCreate(): Boolean {
        Log.d(TAG,"onCreate called")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        val match = uriMatcher.match(uri)
        Log.d(TAG, "query called and match is $match")

        val queryBuilder = SQLiteQueryBuilder()
        when (match) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME
            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                queryBuilder.appendWhereEscapeString("${TasksContract.Columns.ID} = $taskId")
            }
//            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME
//            TIMINGS_ID -> {
//                queryBuilder.tables = TimingsContract.TABLE_NAME
//                val taskId = TimingsContract.getId(uri)
//                queryBuilder.appendWhereEscapeString("$TimingsContract.Columns.ID} = $taskId")
//            }
//            TASK_DURATION -> queryBuilder.tables = TasksContract.TABLE_NAME
//            TASK_DURATION_ID -> {
//                queryBuilder.tables = DurationsContract.TABLE_NAME
//                val taskId = DurationsContract.getId(uri)
//                queryBuilder.appendWhereEscapeString("${DurationsContract.Columns.ID} = $taskId")
//            }
            else -> throw IllegalStateException("Unknown Uri: $uri")
        }
        val db = AppDatabase.getInstance(context!!).readableDatabase
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        TODO("Not yet implemented")
    }
}