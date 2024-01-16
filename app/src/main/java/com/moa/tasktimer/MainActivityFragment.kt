package com.moa.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moa.tasktimer.databinding.FragmentMainBinding

private const val TAG = "MainActivityFragment"
private const val DIALOG_DEL_ID=1
private const val TASK_ID="task"
class MainActivityFragment : Fragment(),CursorRecyclerViewAdapter.OnClickTask,AppDialog.DialogEvents {
    private lateinit var binding: FragmentMainBinding
    private var listener:OnEditTask? = null
    private val adapter = CursorRecyclerViewAdapter(null,this)
    private val viewModel by lazy { ViewModelProvider(requireActivity())[TaskTimerViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(TAG,"onCreateView() called")
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding.taskList.layoutManager = LinearLayoutManager(activity)
        binding.taskList.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnEditTask
        Log.d(TAG,"onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate() called")
        super.onCreate(savedInstanceState)
        viewModel.cursorLiveData.observe(this, Observer { adapter.swapCursor(it)?.close() })
    }
    interface OnEditTask {
        fun onEditTask(task: Task)
    }

    override fun onClickEdit(task: Task) {
       listener?.onEditTask(task)
    }


    override fun onClickDelete(task: Task) {
            val args = Bundle().apply {
                putInt(DIALOG_ID, DIALOG_DEL_ID)
                putString(DIALOG_MESSAGE, getString(R.string.deldialog_message, task.id, task.name))
                putInt(DIALOG_POSITIVE_RID, R.string.delDialog_positive_caption)
                putLong(TASK_ID,task.id)
            }
        val dialog = AppDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager,null)

    }
    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        if(dialogId == DIALOG_DEL_ID) {
            val taskId = args.getLong(TASK_ID)
            viewModel.deleteTask(taskId)
        }
    }


    override fun onLongClick(task: Task) {

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewStateRestored() called")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG,"onStart() called")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG,"onStop() called")
        super.onStop()
    }

    override fun onPause() {
        Log.d(TAG,"onPause() called")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG,"onSaveInstanceState() called")
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        Log.d(TAG,"onDestroyView() called")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy() called")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG,"onDetach() Called")
        listener =  null
        super.onDetach()
    }



}