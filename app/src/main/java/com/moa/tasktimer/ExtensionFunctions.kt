package com.moa.tasktimer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.showConfirmation(dialogId:Int, message:String, positiveRid:Int=R.string.ok, negativeRid:Int=R.string.cancel) {
    val args = Bundle().apply {
        putInt(DIALOG_ID,dialogId)
        putString(DIALOG_MESSAGE,message)
        putInt(DIALOG_POSITIVE_RID,positiveRid)
        putInt(DIALOG_NEGATIVE_RID,negativeRid)
    }
    val appDialog = AppDialog()
    appDialog.arguments = args
    appDialog.show(supportFragmentManager,null)
}