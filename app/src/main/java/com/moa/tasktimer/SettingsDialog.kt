package com.moa.tasktimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.moa.tasktimer.databinding.SettingsDialogBinding
import java.util.GregorianCalendar
import java.util.Locale

private const val TAG = "SettingsDialog"
const val SETTING_FIRST_DAY = "firstDay"
const val SETTINGS_IGNORE_SECONDS = "ignoreSeconds"
private const val SETTING_DEFAULT_IGNORE_SECONDS = 0
private val deltas = intArrayOf(0,5,10,15,20,25,30,35,40,45,50,55,60,120,180,240,300,360,420,480,540,600,900,1800,2700)

class SettingsDialog:DialogFragment() {

    private var firstDay = GregorianCalendar(Locale.getDefault()).firstDayOfWeek
    private var ignoreSeconds = SETTING_DEFAULT_IGNORE_SECONDS
    private lateinit var binding:SettingsDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(TAG,"onCreateView() called")
        binding = SettingsDialogBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.okButton.setOnClickListener {
            saveValues()
            dismiss()
        }
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.ignoreSeconds.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                changeIgnoreSecondsTitle(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                changeIgnoreSecondsTitle(binding.ignoreSeconds.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

//                    changeIgnoreSecondsTitle(binding.ignoreSeconds.progress)
            }

        })
    }
    private fun changeIgnoreSecondsTitle(igSeconds:Int) {
        Log.d(TAG,"position of seekbar: $igSeconds and ${deltas[igSeconds]}")
        if(igSeconds < 12) {
            binding.ignoreTimingTextView.text = getString(R.string.settingsIgnoreSecondsTitle,
                deltas[igSeconds],resources.getQuantityString(R.plurals.settingsLittleUnits, deltas[igSeconds]+1))
        }else {
            binding.ignoreTimingTextView.text = getString(R.string.settingsIgnoreSecondsTitle,
                deltas[igSeconds]/60,resources.getQuantityString(R.plurals.settingsBigUnits, deltas[igSeconds]/60))
        }
    }
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState == null) {
            readValues()
            binding.firstDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY)
            binding.ignoreSeconds.progress = ignoreSeconds
            changeIgnoreSecondsTitle(ignoreSeconds)
        }
    }
    private fun readValues() {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            firstDay = getInt(SETTING_FIRST_DAY,firstDay)
            ignoreSeconds = getInt(SETTINGS_IGNORE_SECONDS, SETTING_DEFAULT_IGNORE_SECONDS)
        }
    }
    private fun saveValues() {
        val newFirstDay = binding.firstDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        val newIgnoreSeconds = binding.ignoreSeconds.progress

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPref.edit().apply {
            if(newFirstDay != firstDay) {
                putInt(SETTING_FIRST_DAY,newFirstDay)
            }
            if(newIgnoreSeconds != ignoreSeconds) {
                putInt(SETTINGS_IGNORE_SECONDS,newIgnoreSeconds)
            }
        }.apply()
    }


}