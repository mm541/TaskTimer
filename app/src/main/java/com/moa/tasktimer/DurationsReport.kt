package com.moa.tasktimer

import android.os.Bundle
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
class DurationsReport : AppCompatActivity() {
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
    }




}