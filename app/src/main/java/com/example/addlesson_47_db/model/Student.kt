package com.example.addlesson_47_db.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Student(
    val name: String,
    val age: Int = 22,
    val course: Int = 4,
    val id: Long=0
) : Parcelable