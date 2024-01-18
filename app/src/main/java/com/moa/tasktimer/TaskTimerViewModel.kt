package com.moa.tasktimer

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "TaskTimerViewModel"
class TaskTimerViewModel(application:Application):AndroidViewModel(application) {
    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            loadTasks()
        }
    }
    private val cursorMutable = MutableLiveData<Cursor?>()
    val cursorLiveData:LiveData<Cursor?>
        get() = cursorMutable
    private var currentTiming:Timing? = null
    private val taskTiming = MutableLiveData<String>()
    val timing:LiveData<String>
        get() = taskTiming
    init {
        getApplication<Application>().contentResolver.registerContentObserver(TasksContract.CONTENT_URI,true,contentObserver)
        Log.d(TAG,"TaskTimerViewModel() initialised")
        currentTiming = getCurrentTiming()
        loadTasks()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun loadTasks() {
      GlobalScope.launch {
          val cursor = getApplication<Application>().contentResolver.query(
              TasksContract.CONTENT_URI,
              null,
              null,
              null,
              "${TasksContract.Columns.SORT_ORDER},${TasksContract.Columns.TASK_NAME}"
          )
          cursorMutable.postValue(cursor)
      }
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun deleteTask(id:Long) {
       GlobalScope.launch {
          getApplication<Application>().contentResolver.delete(
              TasksContract.buildUriFromId(id),
              null,
              null
          )
      }

    }
    @OptIn(DelicateCoroutinesApi::class)
    fun saveTask(task:Task) {
        if(task.name.isNotEmpty()) {
            val values = ContentValues().apply {
                put(TasksContract.Columns.TASK_NAME,task.name)
                put(TasksContract.Columns.TASK_DESCRIPTION,task.description)
                put(TasksContract.Columns.SORT_ORDER,task.sortOrder)
            }
            if(task.id == 0L) {
                GlobalScope.launch {
                    getApplication<Application>().contentResolver.insert(TasksContract.CONTENT_URI,values)
                }
            }else {
                GlobalScope.launch {
                    getApplication<Application>().contentResolver.update(TasksContract.buildUriFromId(task.id),values,null,null)
                }
            }
        }
    }
    fun assignCurrentTiming(task:Task) {
        val currTiming = currentTiming
        if(currTiming == null) {
            currentTiming = Timing(task.id)
            saveTiming(currentTiming!!)
        }else {
            currTiming.setDuration()
            saveTiming(currTiming)
            if(currTiming.taskId == task.id) {
                currentTiming =   null
            }else {
               currentTiming = Timing(task.id)
                saveTiming(currentTiming!!)
            }
        }
        taskTiming.value = if(currentTiming!=null) task.name else null
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun saveTiming(timing:Timing) {
        val inserting = (timing.duration == 0L)

        val values = ContentValues().apply {
            if(inserting) {
                put(TimingsContract.Columns.TIMING_TASK_ID,timing.taskId)
                put(TimingsContract.Columns.TIMING_START_TIME,timing.startTime)
            }
            put(TimingsContract.Columns.TIMING_DURATION,timing.duration)
        }
        GlobalScope.launch {
            if (inserting) {
                val uri = getApplication<Application>().contentResolver.insert(
                    TimingsContract.CONTENT_URI,
                    values
                )
                if (uri != null) {
                    timing.id = TimingsContract.getId(uri)
                }
            } else {
                getApplication<Application>().contentResolver.update(TimingsContract.buildUriFromId(timing.id),values,null,null)
            }
        }
    }
    @SuppressLint("Range")
    fun getCurrentTiming():Timing? {
        var timing:Timing? = null
        val cursor =  getApplication<Application>().contentResolver.query(CurrentTimingContract.CONTENT_URI,null,null,null,null)
        cursor.use {
            if (it!=null && it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(TasksContract.Columns.TASK_NAME))
                val taskId = it.getLong(it.getColumnIndex(TimingsContract.Columns.TIMING_TASK_ID))
                val timingId = it.getLong(it.getColumnIndex(TimingsContract.Columns.ID))
                val startTime = it.getLong(it.getColumnIndex(TimingsContract.Columns.TIMING_START_TIME))
                taskTiming.value = name
                timing = Timing(taskId,startTime,timingId)
            }
        }
        return timing
    }
    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}