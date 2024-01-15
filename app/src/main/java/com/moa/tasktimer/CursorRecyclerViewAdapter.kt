package com.moa.tasktimer

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
private const val TAG = "CursorRecyclerViewAdapt"
class TaskViewHolder(view: View):RecyclerView.ViewHolder(view) {
    val name:TextView = view.findViewById(R.id.til_name)
    val description:TextView = view.findViewById(R.id.tli_description)
    val edit:ImageButton = view.findViewById(R.id.tli_edit)
    val delete:ImageButton = view.findViewById(R.id.tli_delete)
}
class CursorRecyclerViewAdapter(private var cursor:Cursor?):RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
         Log.d(TAG,"onCreateViewHolder() called")
         val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_items,parent,false)
         return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        val cursor = cursor
        Log.d(TAG,"getItemCount() called")
        return if(cursor==null || cursor.count==0) 1 else cursor.count
    }


    @SuppressLint("Range")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val cursor = cursor
        Log.d(TAG,"onBindViewHolder() called")
        if(cursor==null || cursor.count==0) {
            Log.d(TAG,"cursor is either null count is 0")
            holder.name.text = "instruction"
            holder.description.setText(R.string.description_heading)
            holder.edit.visibility = View.GONE
            holder.delete.visibility = View.GONE
        }else {
            Log.d(TAG,"${cursor.count}")
            if(!cursor.moveToPosition(position)) {
                throw IllegalStateException("Cursor can't move at position: $position")
            }else {
                val task = cursor.let {
                    Task(it.getString(it.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                        it.getString(it.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                        it.getLong(it.getColumnIndex(TasksContract.Columns.SORT_ORDER)),
                                it.getLong(it.getColumnIndex(TasksContract.Columns.ID))
                        )
                }
                holder.name.text = task.name
                holder.description.text = task.description
                holder.edit.visibility = View.VISIBLE
                holder.delete.visibility = View.VISIBLE
            }
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