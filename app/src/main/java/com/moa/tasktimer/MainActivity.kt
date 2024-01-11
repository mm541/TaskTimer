package com.moa.tasktimer

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moa.tasktimer.databinding.ActivityMainBinding
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(),AddEditFragment.OnSaveClicked {


    private lateinit var binding: ActivityMainBinding
    private var mTwoPane = false
    private lateinit var taskDetailsContainer:FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        taskDetailsContainer = findViewById(R.id.task_details_container)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if(fragment != null) {
            taskDetailsContainer.visibility = View.VISIBLE
            binding.mainFragment.visibility = if (mTwoPane) View.VISIBLE else View.GONE
        }else {
            taskDetailsContainer.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
            binding.mainFragment.visibility = View.VISIBLE
        }
    }
    private fun removeEditPane(fragment: Fragment? = null) {
        Log.d(TAG,"removeEditPane() called")
        if(fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        taskDetailsContainer.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
        binding.mainFragment.visibility = View.VISIBLE
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
            R.id.mainmenu_addtask -> {requestEditTask(null)
            return true
            }
        }
        return  super.onOptionsItemSelected(item)
    }

    override fun onSaveClicked() {
        Log.d(TAG,"onSaveClicked() called")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        removeEditPane(fragment)
    }
    private fun requestEditTask(task:Task?) {
        val newFragment = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction()
                                .replace(R.id.task_details_container,newFragment)
                                .commit()
        Log.d(TAG,"")
        taskDetailsContainer.visibility = View.VISIBLE
        binding.mainFragment.visibility = if (mTwoPane) View.VISIBLE else View.GONE
    }


}