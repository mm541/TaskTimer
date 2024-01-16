package com.moa.tasktimer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

private const val TAG = "AppDialog"
const val DIALOG_ID = "id"
const val DIALOG_MESSAGE = "message"
const val DIALOG_POSITIVE_RID = "positive_rid"
const val DIALOG_NEGATIVE_RID = "negative_rid"
const val DIALOG_ICON = "icon"
class AppDialog: AppCompatDialogFragment() {
    private var dialogEvents:DialogEvents? = null
    interface DialogEvents{
        fun onPositiveDialogResult(dialogId:Int,args: Bundle)
//        fun onNegativeDialogResult(dialogId:Int,args: Bundle)
//        fun onDialogCancelled(dialogId:Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogEvents = try {
            parentFragment as DialogEvents
        }catch (e:TypeCastException) {
            try {
                context as DialogEvents
            }catch (e:ClassCastException) {
                throw ClassCastException("Activity:$context must implement DialogEvents interface")
            }
        }catch (e:java.lang.ClassCastException) {
            throw ClassCastException("Fragment:$parentFragment must implement DialogEvents interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        val arguments = arguments
        val dialogId:Int
        val messageString:String?
        var positiveStringId:Int
        var negativeStringId:Int
        var iconId:Int
        if(arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID)
            messageString = arguments.getString(DIALOG_MESSAGE)

            if(dialogId == 0 || messageString == null) {
                throw IllegalArgumentException("DialogId and/or message String not present in the bundle")
            }
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID)
            if(positiveStringId == 0) {
                positiveStringId = R.string.ok
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID)
            if(negativeStringId == 0) {
                negativeStringId = R.string.cancel
            }
            iconId = arguments.getInt(DIALOG_ICON)
            if(iconId == 0) {
                iconId = R.drawable.default_icon
            }
        }else {
            throw IllegalArgumentException("$TAG arguments is null")
        }
        return builder.setMessage(messageString)
            .setPositiveButton(positiveStringId){ _, _ ->
                dialogEvents?.onPositiveDialogResult(dialogId,arguments)
            }.setNegativeButton(negativeStringId){ _, _ ->

            }.setIcon(iconId).create()
    }
    override fun onDetach() {
        super.onDetach()
        dialogEvents = null
    }
}