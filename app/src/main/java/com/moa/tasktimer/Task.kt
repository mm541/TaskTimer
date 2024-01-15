package com.moa.tasktimer
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(val name:String,val description:String,val sortOrder:Long,val id:Long=0):Parcelable {
    override fun toString(): String {
        return "Task(name='$name', description='$description', sortOrder=$sortOrder, id=$id)"
    }
}