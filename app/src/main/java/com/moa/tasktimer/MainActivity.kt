package com.moa.tasktimer

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.moa.tasktimer.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

//        testInsert()
//        testUpdate()
//        testBulkUpdate()
//        testDelete()
        val projection = arrayOf(TasksContract.Columns.TASK_NAME, TasksContract.Columns.SORT_ORDER)
        val sortColumn = TasksContract.Columns.SORT_ORDER
        val cursor = contentResolver.query(
            TasksContract.CONTENT_URI,
            null,
            null,
            null,
            sortColumn
        )
        cursor.use {
            if (it != null) {
                while (it.moveToNext()) {
                    with(it) {
                        val id = getLong(0)
                        val name = getString(1)
                        val description = getString(2)
                        val sortOrder = getInt(3)
                        Log.d(
                            TAG, """                      
                                id = $id
                                name = $name                               
                                description = $description
                                sortOrder = $sortOrder
                            """.trimIndent()
                        )
                    }
                }
            }
        }


    }

    private fun testInsert() {
        val values = ContentValues().apply {
            put(TasksContract.Columns.TASK_NAME, "New Task 1")
            put(TasksContract.Columns.TASK_DESCRIPTION, "Task 1")
            put(TasksContract.Columns.SORT_ORDER, 2)
        }
        val uri = contentResolver.insert(TasksContract.CONTENT_URI, values)
        Log.d(TAG, "new row id : ${uri?.let { TasksContract.getId(it) }}")

    }

    private fun testUpdate() {
        val values = ContentValues().apply {
            put(TasksContract.Columns.TASK_NAME, "content providers")
            put(TasksContract.Columns.TASK_DESCRIPTION, "Learn content providers")
            put(TasksContract.Columns.SORT_ORDER, 2)
        }
        val rowsAffected =
            contentResolver.update(TasksContract.buildUriFromId(3), values, null, null)
//        Log.d(TAG,"new row id : ${uri?.let { TasksContract.getId(it) }}")
        Log.d(TAG, "num of rows Affected: $rowsAffected")
    }

    private fun testBulkUpdate() {
        val values = ContentValues().apply {
            put(TasksContract.Columns.TASK_DESCRIPTION, "Completed")
            put(TasksContract.Columns.SORT_ORDER, 999)
        }
        val selection = TasksContract.Columns.SORT_ORDER + "= ?"
        val selectionArgs = arrayOf("99")
        val rowsAffected = contentResolver.update(
            TasksContract.buildUriFromId(3),
            values,
            selection,
            selectionArgs
        )
//        Log.d(TAG,"new row id : ${uri?.let { TasksContract.getId(it) }}")
        Log.d(TAG, "num of rows Affected: $rowsAffected")
    }

    private fun testDelete() {
        val rowsAffected = contentResolver.delete(TasksContract.buildUriFromId(3), null, null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.mainmenu_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}