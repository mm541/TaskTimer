package com.moa.tasktimer

import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moa.tasktimer.databinding.DurationLayoutBinding
private const val TAG = "DurationsReport"
enum class SortOrder {
    NAME,
    DESCRIPTION,
    START_DATE,
    DURATION
}
private const val DATE_PICKER_FILTER = 1
private const val DATE_PICKER_DELETE = 2

class DurationsReport : AppCompatActivity(),DatePickerFragment.OnDateSet {
    private val reportAdapter:DurationRVAdapter by lazy { DurationRVAdapter(this,null) }
    private val viewModel:DurationReportViewModel by lazy { ViewModelProvider(this)[DurationReportViewModel::class.java] }
    private lateinit var binding: DurationLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DurationLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tdList.layoutManager = LinearLayoutManager(this)
        binding.tdList.adapter = reportAdapter

       viewModel.cursor.observe(this, Observer {
           reportAdapter.swapCursor(it)?.close()
       })

        binding.tdDurationHeading.setOnClickListener{changeSortOrder(it as TextView)}
        binding.tdNameHeading.setOnClickListener{changeSortOrder(it as TextView)}
        binding.tdStartHeading.setOnClickListener{changeSortOrder(it as TextView)}
        binding.tdDescriptionHeading?.setOnClickListener{changeSortOrder(it as TextView)}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report,menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.rm_period)
        if(item != null) {
            if(viewModel.displayWeek) {
                item.setTitle(getString(R.string.rm_period_week))
                item.setIcon(R.drawable.baseline_filter_7_24)
            }else {
                item.setTitle(getString(R.string.rm_period_day))
                item.setIcon(R.drawable.baseline_filter_1_24)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.rm_period -> {
                viewModel.toggleWeek()
                invalidateOptionsMenu()
                return true
            }
            R.id.rm_date -> {
                showDatePicker(getString(R.string.select_date_for_report), DATE_PICKER_FILTER)
            }
            R.id.rm_delete ->{}
        }
        return super.onOptionsItemSelected(item)
    }
    private fun changeSortOrder(textView: TextView) {
       viewModel.sortOrder = when(textView.text) {
            getString(R.string.task_name) -> SortOrder.NAME
            getString(R.string.task_description) -> SortOrder.DESCRIPTION
            getString(R.string.duration) -> SortOrder.DURATION
             else -> SortOrder.START_DATE
       }
    }
    private fun showDatePicker(title:String,dialogId:Int) {
        val datePicker = DatePickerFragment()
        datePicker.arguments?.apply {
            putInt(DATE_PICKER_ID, dialogId)
            putString(DATE_PICKER_TITLE,title)
            putSerializable(DATE_PICKER_DATE,viewModel.getFilteredDate())
        }
        datePicker.show(supportFragmentManager,null)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        when(view.tag) {
            DATE_PICKER_FILTER -> {
                viewModel.setReportDate(year,month,day)
            }
            DATE_PICKER_DELETE -> {

            }
            else -> {
                throw IllegalArgumentException("Illegal dialog id")
            }
        }
    }


}