package com.moa.tasktimer

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.moa.tasktimer.databinding.ActivityMainBinding
import com.moa.tasktimer.debug.TestData

private const val TAG = "MainActivity"
private const val EDIT_CONFIRMATION_ID = 1
class MainActivity : AppCompatActivity(),AddEditFragment.OnSaveClicked,MainActivityFragment.OnEditTask,AppDialog.DialogEvents {

    private var aboutDialog:AlertDialog? = null
    private lateinit var binding: ActivityMainBinding
    private var mTwoPane = false
    private lateinit var taskDetailsContainer:FrameLayout
    private val viewModel:TaskTimerViewModel by lazy { ViewModelProvider(this)[TaskTimerViewModel::class.java] }
    private val currTask:TextView by lazy { binding.mainFragment.findViewById(R.id.current_task) }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate() called")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        taskDetailsContainer = findViewById(R.id.task_details_container)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG,"onCreate: mTwoPane is $mTwoPane")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if(fragment != null) {
            taskDetailsContainer.visibility = View.VISIBLE
            binding.mainFragment.visibility = if (mTwoPane) View.VISIBLE else View.GONE
        }else {
            taskDetailsContainer.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
            binding.mainFragment.visibility = View.VISIBLE
        }

        Log.d(TAG,"onCreate() finished")

        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val frag = supportFragmentManager.findFragmentById(R.id.task_details_container)
                if(frag==null || mTwoPane) {
                    isEnabled = false
                    finish()
                }else {
                    if((fragment is AddEditFragment) && fragment.isDirty()) {
                        showConfirmation(
                            EDIT_CONFIRMATION_ID,
                            getString(R.string.edit_confirmation_message),
                            R.string.exit_without_saving,
                            R.string.continue_editing)
                    }else {
                        removeEditPane(fragment)
                    }
                }
            }
        })
        viewModel.timing.observe(this, Observer {
           currTask.text = if(it == null) {
               getString(R.string.no_task_selected)
           }else {
               getString(R.string.task_timing,it)
           }
        })
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

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.mainmenu_generate).isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
            R.id.mainmenu_addtask -> {requestEditTask(null)
            }
             R.id.mainmenu_about -> {showDialog()}
             R.id.mainmenu_generate -> {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     TestData(application).generateTestData()
                 }
             }
             android.R.id.home -> {
                 Log.d(TAG,"onOptionsItemSelected: home button pressed")
                 val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
                 if((fragment is AddEditFragment) && fragment.isDirty()) {
                     showConfirmation(
                         EDIT_CONFIRMATION_ID,
                         getString(R.string.edit_confirmation_message),
                         R.string.exit_without_saving,
                         R.string.continue_editing)
                 }else {
                     removeEditPane(fragment)
                 }
             }
             R.id.mainmenu_settings -> {
                 SettingsDialog().show(supportFragmentManager,null)
             }
        }
        return  super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun showDialog() {
        val messageView = layoutInflater.inflate(R.layout.about,null,false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setPositiveButton(R.string.ok) {_,_ ->
            if(aboutDialog != null && aboutDialog!!.isShowing) {
                aboutDialog?.dismiss()
            }
        }
        aboutDialog = builder.setView(messageView).create()
        aboutDialog?.show()
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
        Log.d(TAG,"requestEditTask() called")
        taskDetailsContainer.visibility = View.VISIBLE
        binding.mainFragment.visibility = if (mTwoPane) View.VISIBLE else View.GONE

    }

    override fun onEditTask(task: Task) {
        Log.d(TAG,"onEditTask() called")
        requestEditTask(task)
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        if(dialogId == EDIT_CONFIRMATION_ID) {
            val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
            removeEditPane(fragment)
        }
    }

    override fun onStop() {
        super.onStop()
        if(aboutDialog?.isShowing == true) {
            aboutDialog?.dismiss()
        }
    }

}