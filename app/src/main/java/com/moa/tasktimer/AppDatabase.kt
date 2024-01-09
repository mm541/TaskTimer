package com.moa.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 1
private const val TAG = "AppDatabase"
class AppDatabase private constructor(context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {
    init{
        Log.d(TAG,"AppDatabase instance initialised")
    }
    override fun onCreate(db: SQLiteDatabase) {
       //CREATE TABLE Tasks(_id INTEGER PRIMARY KEY NOT NULL,Name TEXT,Description TEXT,SortOrder INTEGER)
        Log.d(TAG,"onCreate starts")
        val sSQL = """
            CREATE TABLE ${TasksContract.TABLE_NAME}(
            ${TasksContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TasksContract.Columns.TASK_NAME} TEXT NOT NULL,
            ${TasksContract.Columns.TASK_DESCRIPTION} TEXT,
            ${TasksContract.Columns.SORT_ORDER} INTEGER)
        """.trimIndent()
        db.execSQL(sSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG,"onUpgrade starts")

        when(oldVersion) {
            1 -> {

            }
            else -> throw IllegalStateException("version not recognised")
        }
    }
    companion object {
        @Volatile
        private var instance:AppDatabase? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance?:AppDatabase(context).also { instance = it }
        }
    }
}