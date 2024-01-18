package com.moa.tasktimer.debug

import android.app.Application
import android.content.ContentValues
import android.os.Build
import androidx.annotation.RequiresApi
import com.moa.tasktimer.TasksContract
import com.moa.tasktimer.TimingsContract
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.GregorianCalendar
class TestTiming(val taskId:Long,val startTime:Long,val duration: Long)
class TestData(private val application:Application) {
    private val random = java.util.Random()
    private val lowerBoundYear = 2023
    private val upperBoundYear = 2024

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTestData() {
        val cursor = application.contentResolver.query(TasksContract.CONTENT_URI,
            arrayOf(TasksContract.Columns.ID),null,null,null)

        cursor.use {

            if (it != null) {
                while (it.moveToNext()) {
                    val numIterations = random.nextInt(501)+100
                    for(i in 1..numIterations) {
                        val date = randomDateTime()
                        val taskId = it.getLong(0)
                        val duration = random.nextInt(86400/6).toLong()
                        addTimings(TestTiming(taskId, date,duration))
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun randomDateTime():Long {

        val year = random.nextInt(upperBoundYear-lowerBoundYear+1)+lowerBoundYear
        val sec = random.nextInt(60)
        val min = random.nextInt(60)
        val hour = random.nextInt(24)
        val month = random.nextInt(12)
        val yearMonth = LocalDate.of(year,month+1,1)
        val day = random.nextInt(yearMonth.withDayOfMonth(1).lengthOfMonth())+1
        return GregorianCalendar(year,month,day,hour,min,sec).timeInMillis/1000
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun addTimings(timing: TestTiming) {
        val values = ContentValues().apply {
            put(TimingsContract.Columns.TIMING_START_TIME,timing.startTime)
            put(TimingsContract.Columns.TIMING_TASK_ID,timing.taskId)
            put(TimingsContract.Columns.TIMING_DURATION,timing.duration)
        }
        GlobalScope.launch {
            application.contentResolver.insert(
                TimingsContract.CONTENT_URI,
                values
            )
        }
    }
}