package com.moa.tasktimer

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

    init {
        getApplication<Application>().contentResolver.registerContentObserver(TasksContract.CONTENT_URI,true,contentObserver)
        Log.d(TAG,"TaskTimerViewModel() initialised")
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
    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}