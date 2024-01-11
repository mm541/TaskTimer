package com.moa.tasktimer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Inflate the layout for this fragment
        binding = FragmentAddEditBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_add_edit, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding.addeditSave.setOnClickListener {
            listener?.onSaveClicked()
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task Parameter 1.
         * @return A new instance of fragment AddEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(task: Task?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }
}