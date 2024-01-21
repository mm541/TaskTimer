package com.moa.tasktimer

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moa.tasktimer.databinding.TaskDurationItemBinding
import java.util.Locale


class ViewHolder(val binding: TaskDurationItemBinding):RecyclerView.ViewHolder(binding.root)
class DurationRVAdapter(val context:Context, private var cursor: Cursor?):RecyclerView.Adapter<ViewHolder>() {
    private val dateFormat = DateFormat.getDateFormat(context)

    private fun formatDuration(duration:Long):String {
        val hour = duration/3600
        val rem = duration - hour*3600
        val sec = rem%60
        val min = rem/60

        return String.format(Locale.getDefault(),"%02d:%02d:%02d",hour,min,sec)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_duration_item,parent,false)
        val viewBinding = TaskDurationItemBinding.bind(view)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
      return cursor?.count ?: 0
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cursor = cursor
        if(cursor != null && cursor.count != 0) {
            if(!cursor.moveToPosition(position)) {
                throw IllegalStateException("Cursor couldn't move to position: $position")
            }
            val name = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.NAME))
            val description = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DESCRIPTION))
            val startTime = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.START_TIME))
            val duration = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATION))

            val userDate = dateFormat.format(startTime*1000)
            val totalTime = formatDuration(duration)
            holder.binding.tdName.text = name
            holder.binding.tdDescription?.text = description
            holder.binding.tdStart.text = userDate
            holder.binding.tdDuration.text = totalTime
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun swapCursor(newCursor:Cursor?):Cursor?{
        if(newCursor === cursor)
            return null

        val numItems = cursor?.count
        val oldCursor = cursor
        cursor = newCursor
        if(newCursor != null) {
            notifyDataSetChanged()
        }else {
            notifyItemRangeRemoved(0,numItems!!)
        }
        return oldCursor
    }
}