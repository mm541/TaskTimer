package com.moa.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

private const val TAG = "MainActivityFragment"
class MainActivityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG,"onCreateView() called")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate() called")
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
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
        super.onDetach()
    }
}