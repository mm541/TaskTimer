package com.moa.tasktimer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 4
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
            ${TasksContract.Columns.SORT_ORDER} INTEGER);
        """.trimIndent()
        db.execSQL(sSQL)

        val sSql1 = """
            CREATE TABLE ${TimingsContract.TABLE_NAME} (
            ${TimingsContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TimingsContract.Columns.TIMING_TASK_ID} INTEGER NOT NULL,
            ${TimingsContract.Columns.TIMING_START_TIME} INTEGER,
            ${TimingsContract.Columns.TIMING_DURATION} INTEGER);
        """.trimIndent()
        db.execSQL(sSql1)
        val sSql2 = """
            CREATE TRIGGER Remove_Task
            AFTER DELETE ON ${TasksContract.TABLE_NAME}
            FOR EACH ROW
            BEGIN
            DELETE FROM ${TimingsContract.TABLE_NAME}
            WHERE ${TimingsContract.Columns.TIMING_TASK_ID} = OLD.${TasksContract.Columns.ID};
            END;
        """.trimIndent()
        db.execSQL(sSql2)

        addCurrentTimingView(db)
        addDurationView(db)
    }
    private fun addDurationView(db: SQLiteDatabase) {
        val sSql = """
            CREATE VIEW ${DurationsContract.TABLE_NAME} AS
            SELECT ${DurationsContract.Columns.NAME},${DurationsContract.Columns.DESCRIPTION},
            ${DurationsContract.Columns.START_TIME},DATE(${DurationsContract.Columns.START_TIME},'unixepoch','localtime') AS ${DurationsContract.Columns.START_DATE},
            SUM(${TimingsContract.Columns.TIMING_DURATION}) AS ${DurationsContract.Columns.DURATION}
            FROM ${TasksContract.TABLE_NAME} INNER JOIN ${TimingsContract.TABLE_NAME} ON
            ${TasksContract.TABLE_NAME}.${TasksContract.Columns.ID}  = ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_TASK_ID}
            GROUP BY ${TasksContract.TABLE_NAME}.${TasksContract.Columns.ID},${DurationsContract.Columns.START_DATE}
        """.trimIndent()
        db.execSQL(sSql)
    }
    private fun addCurrentTimingView(db: SQLiteDatabase) {
        /*
        CREATE VIEW vwCurrentTiming
             AS SELECT Timings._id,
                 Timings.TaskId,
                 Timings.StartTime,
                 Tasks.Name
             FROM Timings
             JOIN Tasks
             ON Timings.TaskId = Tasks._id
             WHERE Timings.Duration = 0
             ORDER BY Timings.StartTime DESC;
         */
        val sSQLTimingView = """CREATE VIEW ${CurrentTimingContract.TABLE_NAME}
        AS SELECT ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.ID},
            ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_TASK_ID},
            ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_START_TIME},
            ${TasksContract.TABLE_NAME}.${TasksContract.Columns.TASK_NAME}
        FROM ${TimingsContract.TABLE_NAME}
        JOIN ${TasksContract.TABLE_NAME}
        ON ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_TASK_ID} = ${TasksContract.TABLE_NAME}.${TasksContract.Columns.ID}
        WHERE ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_DURATION} = 0
        ORDER BY ${TimingsContract.TABLE_NAME}.${TimingsContract.Columns.TIMING_START_TIME} DESC;
    """.replaceIndent(" ")
        Log.d(TAG, sSQLTimingView)
        db.execSQL(sSQLTimingView)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG,"onUpgrade starts")

        when(oldVersion) {
            1 -> {
                val sSql1 = """
            CREATE TABLE ${TimingsContract.TABLE_NAME} (
            ${TimingsContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
            ${TimingsContract.Columns.TIMING_TASK_ID} INTEGER NOT NULL,
            ${TimingsContract.Columns.TIMING_START_TIME} INTEGER,
            ${TimingsContract.Columns.TIMING_DURATION} INTEGER);
        """.trimIndent()
                db.execSQL(sSql1)
                val sSql2 = """
            CREATE TRIGGER Remove_Task
            AFTER DELETE ON ${TasksContract.TABLE_NAME}
            FOR EACH ROW
            BEGIN
            DELETE FROM ${TimingsContract.TABLE_NAME}
            WHERE ${TimingsContract.Columns.TIMING_TASK_ID} = OLD.${TasksContract.Columns.ID};
            END;
        """.trimIndent()
                db.execSQL(sSql2)
                addCurrentTimingView(db)
                addDurationView(db)
            }
            2 -> {
                addCurrentTimingView(db)
                addDurationView(db)
            }
            3 -> {
                addDurationView(db)
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