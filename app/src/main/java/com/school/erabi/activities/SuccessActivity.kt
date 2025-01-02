package com.school.erabi.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.school.erabi.databinding.ActivitySuccessBinding
import com.school.erabi.db.StudentRepository

class SuccessActivity : AppCompatActivity() {
    private lateinit var successBinding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        successBinding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(successBinding.root)

        val billNo = intent?.getStringExtra("billNo")
                billNo?.let {
                    val student = StudentRepository(applicationContext).getStudent(billNo)
                successBinding.txtBillNo.text = student.billNo
                successBinding.lblSchoolName.text = student.schoolName
                successBinding.txtStudentName.text = student.studentName
                successBinding.txtFatherName.text = student.fatherName
                successBinding.txtClassName.text = student.className
                successBinding.txtRollno.text = student.rollNo
                successBinding.txtBillingMonth.text = student.month
                successBinding.txtBillingDate.text = student.billingDate
                successBinding.txtTutionFee.text = student.tutionFees.toString()
                successBinding.txtVanFee.text = student.vanFees.toString()
                successBinding.txtExamFee.text = student.examFee.toString()
                successBinding.txtOtherFee.text = student.otherFees.toString()
                successBinding.txtTotal.text = student.total.toString()
                successBinding.lblCellNo.text = student.msg
            }

        successBinding.mBack.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}