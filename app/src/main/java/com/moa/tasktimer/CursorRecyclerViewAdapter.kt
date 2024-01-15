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
class TaskViewHolder(private val view: View):RecyclerView.ViewHolder(view) {
    val name:TextView = view.findViewById(R.id.til_name)
    val description:TextView = view.findViewById(R.id.tli_description)
    val edit:ImageButton = view.findViewById(R.id.tli_edit)
    val delete:ImageButton = view.findViewById(R.id.tli_delete)

    fun bind(task: Task,listener: CursorRecyclerViewAdapter.OnClickTask) {
        name.text = task.name
        description.text = task.description
        edit.visibility = View.VISIBLE
        delete.visibility = View.VISIBLE

        edit.setOnClickListener {
            Log.d(TAG,"onEditClicked(): ${task.name}")
            listener.onClickEdit(task)
        }
        delete.setOnClickListener {
            Log.d(TAG,"onDeleteClicked(): ${task.name}")
            listener.onClickDelete(task)
        }
       view.setOnLongClickListener {
           Log.d(TAG,"onLongClicked(): ${task.name}")
           listener.onLongClick(task)
           true
       }
    }
}
class CursorRecyclerViewAdapter(private var cursor:Cursor?,private val listener: OnClickTask):RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_items,parent,false)
         return TaskViewHolder(view)
    }
    interface OnClickTask{
        fun onClickEdit(task:Task)
        fun onClickDelete(task:Task)
        fun onLongClick(task:Task)
    }

    override fun getItemCount(): Int {
        val cursor = cursor
        return if(cursor==null || cursor.count==0) 1 else cursor.count
    }


    @SuppressLint("Range")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val cursor = cursor
        if(cursor==null || cursor.count==0) {
            holder.name.setText(R.string.instruction_heading)
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
               holder.bind(task, listener)
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