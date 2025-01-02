package com.school.erabi.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var billNo: String?,
    var schoolName: String,
    var studentName: String,
    var fatherName: String,
    var className: String,
    var rollNo: String,
    var month: String,
    var year: String,
    var billingDate: String,
    var tutionFees: Double,
    var vanFees: Double,
    var examFee: Double,
    var otherFees: Double,
    var total: Double,
    var msg: String
    )
