package com.moa.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext


private const val TAG = "AppProvider"

const val CONTENT_AUTHORITY = "com.moa.tasktimer.provider"

private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201
private const val CURRENT_TIMING = 300
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

        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME,TIMINGS)
        matcher.addURI(CONTENT_AUTHORITY,"${TimingsContract.TABLE_NAME}/#",TIMINGS_ID)
        matcher.addURI(CONTENT_AUTHORITY,CurrentTimingContract.TABLE_NAME, CURRENT_TIMING)
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
        val context = requireContext(this)
        val match = uriMatcher.match(uri)
        Log.d(TAG, "query called and match is $match")

        val queryBuilder = SQLiteQueryBuilder()
        when (match) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME
            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                queryBuilder.appendWhere("${TasksContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$taskId")
            }
            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME
            TIMINGS_ID -> {
                queryBuilder.tables = TimingsContract.TABLE_NAME
                val timingId = TimingsContract.getId(uri)
                queryBuilder.appendWhere("$TimingsContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$timingId")
            }
            CURRENT_TIMING -> {
                queryBuilder.tables = CurrentTimingContract.TABLE_NAME
            }
//            TASK_DURATION -> queryBuilder.tables = TasksContract.TABLE_NAME
//            TASK_DURATION_ID -> {
//                queryBuilder.tables = DurationsContract.TABLE_NAME
//                val taskId = DurationsContract.getId(uri)
//                queryBuilder.appendWhere("${DurationsContract.Columns.ID} = ")
//                queryBuilder.appendWhereEscapeString("$taskId")
//            }
            else -> throw IllegalStateException("Unknown Uri: $uri")
        }

        val db = AppDatabase.getInstance(context).readableDatabase
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val match = uriMatcher.match(uri)
        Log.d(TAG, "insert() called and match is $match")
        val context = requireContext(this)
        val recordId:Long
        val returnUri:Uri
        when(match) {
            TASKS -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                recordId = db.insert(TasksContract.TABLE_NAME,null,values)
                if(recordId != -1L) {
                    returnUri = TasksContract.buildUriFromId(recordId)
                }else {
                    throw SQLException("Something error occurred while inserting in Tasks table")
                }
            }
            TIMINGS -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                recordId = db.insert(TimingsContract.TABLE_NAME,null,values)
                if(recordId != -1L) {
                    returnUri = TimingsContract.buildUriFromId(recordId)
                }else {
                    throw SQLException("Something error occurred while inserting in Timings table")
                }
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }
        if(recordId > 0) {
            context.contentResolver.notifyChange(uri,null)
        }
        Log.d(TAG,"exiting insert returning: $returnUri")
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = uriMatcher.match(uri)
        Log.d(TAG, "delete() called and match is $match")
        val context = requireContext(this)
        val count:Int
        var selectionCriteria:String

        when(match) {
            TASKS -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                count = db.delete(TasksContract.TABLE_NAME,selection,selectionArgs)
            }
            TASKS_ID -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"
                if(!selection.isNullOrEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TasksContract.TABLE_NAME,selectionCriteria,selectionArgs)
            }
            TIMINGS -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                count = db.delete(TimingsContract.TABLE_NAME,selection,selectionArgs)
            }
            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"
                if(!selection.isNullOrEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TimingsContract.TABLE_NAME,selectionCriteria,selectionArgs)
            }
            else -> throw IllegalArgumentException("invalid uri: $uri")
        }
        if(count > 0) {
            context.contentResolver.notifyChange(uri,null)
        }
        Log.d(TAG,"return count : $count")
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        val match = uriMatcher.match(uri)
        Log.d(TAG, "update() called and match is $match")
        val context = requireContext(this)
        val count:Int
        var selectionCriteria:String

        when(match) {
            TASKS -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                count = db.update(TasksContract.TABLE_NAME,values,selection,selectionArgs)
            }
            TASKS_ID -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"
                if(!selection.isNullOrEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TasksContract.TABLE_NAME,values,selectionCriteria,selectionArgs)
            }
            TIMINGS -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                count = db.update(TimingsContract.TABLE_NAME,values,selection,selectionArgs)
            }
            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"
                if(!selection.isNullOrEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TimingsContract.TABLE_NAME,values,selectionCriteria,selectionArgs)
            }
            else -> throw IllegalArgumentException("invalid uri: $uri")
        }
        if(count > 0) {
            context.contentResolver.notifyChange(uri,null)
        }
        Log.d(TAG,"return count : $count")
        return count
    }
}