package com.moa.tasktimer
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Task(val name:String,val description:String,val sortOrder:Long,val id:Long=0):Parcelable