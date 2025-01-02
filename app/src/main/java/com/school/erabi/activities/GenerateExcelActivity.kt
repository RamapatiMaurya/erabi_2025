package com.school.erabi.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.school.erabi.R
import com.school.erabi.adapter.BillingAdapter
import com.school.erabi.databinding.ActivityGenerateExcelBinding
import com.school.erabi.db.Student
import com.school.erabi.db.StudentRepository
import com.school.erabi.listener.RecyclerItemClickListener
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.*
import java.util.*


class GenerateExcelActivity : AppCompatActivity() {
    private lateinit var file : File
    private lateinit var excelBinding: ActivityGenerateExcelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        excelBinding = ActivityGenerateExcelBinding.inflate(layoutInflater)
        setContentView(excelBinding.root)
        file = File(applicationContext.getExternalCacheDir(), "student_report.xls")

        if (file.exists()) {
            excelBinding.mShare.visibility = View.VISIBLE
            excelBinding.lbStatus.setText(resources.getString(R.string.excel_status))
        } else {
            excelBinding.mShare.visibility = View.GONE
            excelBinding.lbStatus.setText(resources.getString(R.string.excel_status_n))
        }

        createRecycler()

        excelBinding.mShare.setOnClickListener { sentEmail(file) }
        excelBinding.mGenerateExcel.setOnClickListener { createWorkSheet() }

    }

    private fun sentEmail(file : File) {
        val path: Uri = FileProvider.getUriForFile(
            this@GenerateExcelActivity,
            "com.school.erabi.provider",  //(use your app signature + ".provider" )
            file
        )
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("publicschool.era@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Billing Report - ${UserSlipActivity.dateToString(
            Date()
        )}")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun createWorkSheet() {
        val hssfWorkBook = HSSFWorkbook()
        val hssfSheet = hssfWorkBook.createSheet()

        val studentRepository = StudentRepository(applicationContext)
        val studentList = studentRepository.getAllStudents()

        insertCellHeader(hssfSheet, 0)
        studentList.forEachIndexed { index, student ->
            insertCellItem(hssfSheet, index + 1, student)
        }

        var foutputStream: FileOutputStream? = null
        try {
            if (!file.exists()) file.createNewFile()

            foutputStream = FileOutputStream(file)
            hssfWorkBook.write(foutputStream)

            Toast.makeText(
                this@GenerateExcelActivity,
                "Excel Report Generated Successfully!", Toast.LENGTH_LONG
            ).show()

            runOnUiThread({
                if (file.exists()) {
                    excelBinding.mShare.visibility = View.VISIBLE
                    excelBinding.lbStatus.setText(resources.getString(R.string.excel_status))
                } else {
                    excelBinding.mShare.visibility = View.GONE
                    excelBinding.lbStatus.setText(resources.getString(R.string.excel_status_n))
                }
            })



        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (foutputStream != null) {
                foutputStream.flush()
                foutputStream.close()
            }
        }
    }

    private fun createRecycler() {

        excelBinding.excelRecyclerView.layoutManager =
            LinearLayoutManager(this@GenerateExcelActivity, RecyclerView.VERTICAL, false)
        excelBinding.excelRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this@GenerateExcelActivity,
                0
            )
        )
        excelBinding.excelRecyclerView.setItemAnimator(DefaultItemAnimator())
        excelBinding.excelRecyclerView.setHasFixedSize(true)
        val billings = StudentRepository(applicationContext).getAllStudents()
        excelBinding.excelRecyclerView.setAdapter(
            BillingAdapter(billings, this@GenerateExcelActivity)
        )
        excelBinding.excelRecyclerView.scrollToPosition(0)
        excelBinding.excelRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(
            this@GenerateExcelActivity
        ) { v, position ->
            //  leaveId = leaveType.get(position).getLeaveId();
        })
    }

    private fun insertCellItem(hssfSheet: HSSFSheet?, index: Int, student: Student) {
        var i = -1
        val abc = hssfSheet?.createRow(index)
        abc?.let {
            it.createCell(++i)?.setCellValue(student.id.toString())
            it.createCell(++i).setCellValue(student.billNo.toString())
            it.createCell(++i).setCellValue(student.schoolName)
            it.createCell(++i).setCellValue(student.studentName)
            it.createCell(++i).setCellValue(student.fatherName)
            it.createCell(++i).setCellValue(student.className)
            it.createCell(++i).setCellValue(student.rollNo)
            it.createCell(++i).setCellValue(student.month)
            it.createCell(++i).setCellValue(student.year)
            it.createCell(++i).setCellValue(student.billingDate)
            it.createCell(++i).setCellValue(student.tutionFees.toString())
            it.createCell(++i).setCellValue(student.vanFees.toString())
            it.createCell(++i).setCellValue(student.examFee.toString())
            it.createCell(++i).setCellValue(student.otherFees.toString())
            it.createCell(++i).setCellValue(student.total.toString())
        }

    }

    private fun insertCellHeader(hssfSheet: HSSFSheet?, index: Int) {
        var i = -1
        val abc = hssfSheet?.createRow(index)
        abc?.let {
            it.createCell(++i)?.setCellValue("Id")
            it.createCell(++i).setCellValue("Bill Number")
            it.createCell(++i).setCellValue("School Name")
            it.createCell(++i).setCellValue("Student Name")
            it.createCell(++i).setCellValue("Father Name")
            it.createCell(++i).setCellValue("Class Name")
            it.createCell(++i).setCellValue("Roll Number")
            it.createCell(++i).setCellValue("Month")
            it.createCell(++i).setCellValue("Year")
            it.createCell(++i).setCellValue("Billing Date")
            it.createCell(++i).setCellValue("Tution Fee")
            it.createCell(++i).setCellValue("Van Fee")
            it.createCell(++i).setCellValue("Exam Fee")
            it.createCell(++i).setCellValue("Other Fee")
            it.createCell(++i).setCellValue("Total")
        }

    }

}