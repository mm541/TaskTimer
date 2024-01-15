package com.moa.tasktimer

import android.app.Application
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private fun loadTasks() {
       val  cursor = getApplication<Application>().contentResolver.query(
            TasksContract.CONTENT_URI,
            null,
            null,
            null,
            "${TasksContract.Columns.SORT_ORDER},${TasksContract.Columns.TASK_NAME}"
        )
        cursorMutable.value = cursor
    }

    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}