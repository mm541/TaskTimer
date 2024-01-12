package com.moa.tasktimer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moa.tasktimer.databinding.FragmentAddEditBinding

private const val ARG_TASK = "task"
private const val TAG = "AddEditFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEditFragment : Fragment() {

    private var task: Task? = null
    private var listener:OnSaveClicked? = null
    private lateinit var binding:FragmentAddEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate() starts")
        arguments?.let {
            task =     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable(ARG_TASK,Task::class.java)
                }else {
                    it.getParcelable(ARG_TASK)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(TAG,"onCreateView() starts")
        binding = FragmentAddEditBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"onAttach() starts")
        if(context is OnSaveClicked) {
            listener = context
        }else {
            throw RuntimeException("$context: OnSaveClicked must be implemented")
        }
    }
    private fun saveTask() {
        val sortOrder = if(binding.addeditSortorder.text.isNotEmpty()) {
            binding.addeditSortorder.text.toString().toLong()
        }else {
            0
        }
        val values = ContentValues()
        val task = task
        if(task != null) {
            if(binding.addeditName.text .isNotEmpty() && binding.addeditName.text.toString() != task.name) {
                values.put(TasksContract.Columns.TASK_NAME,binding.addeditName.text.toString())
            }
            if(binding.addeditDescription.text.toString() != task.description) {
                values.put(TasksContract.Columns.TASK_DESCRIPTION,binding.addeditDescription.text.toString())
            }
            if(sortOrder != task.sortOrder) {
                values.put(TasksContract.Columns.SORT_ORDER,sortOrder)
            }
            if(values.size() != 0) {
                Log.d(TAG,"updating values")
                activity?.contentResolver?.update(TasksContract.buildUriFromId(task.id),values,null,null)
            }
        }else {
            if(binding.addeditName.text.isNotEmpty()) {
                values.put(TasksContract.Columns.TASK_NAME,binding.addeditName.text.toString())
                if(binding.addeditDescription.text.isNotEmpty()) {
                    values.put(TasksContract.Columns.TASK_DESCRIPTION,binding.addeditDescription.text.toString())
                }
                values.put(TasksContract.Columns.SORT_ORDER,sortOrder)
                activity?.contentResolver?.insert(TasksContract.CONTENT_URI,values)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding.addeditSave.setOnClickListener {
            saveTask()
            listener?.onSaveClicked()
        }
        if(savedInstanceState == null) {
        val task = task
            if(task != null) {
                Log.d(TAG,"onViewCreated() task is not null and id is ${task.id}")
                binding.addeditName.setText(task.name)
                binding.addeditName.setText(task.description)
                binding.addeditName.setText(task.sortOrder.toString())
            }
        }
    }
    override fun onDetach() {
        Log.d(TAG,"onDetach() starts")
        super.onDetach()
        listener = null
    }
    interface OnSaveClicked {
        fun onSaveClicked()
    }

    override fun onStart() {
        super.onStart()
        if(listener is AppCompatActivity) {
            val actionBar = (listener as AppCompatActivity?)?.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(task: Task?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }


}