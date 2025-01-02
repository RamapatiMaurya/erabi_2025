package com.school.erabi.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.school.erabi.R
import com.school.erabi.activities.BillDetailedActivity
import com.school.erabi.databinding.BillingItemsBinding
import com.school.erabi.db.Student
import com.school.erabi.models.BillingSummary
import java.util.ArrayList

class BillingAdapter(val leaveTypeModels: List<Student>, val context : Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
private lateinit var itemsBinding: BillingItemsBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_items, parent, false)
        itemsBinding = BillingItemsBinding.bind(view)
        return VHItem(itemsBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val student: Student = getItem(position)
        itemsBinding.txtBillNo.text = student.billNo.toString()
        itemsBinding.txtBillDate.text = student.billingDate
        itemsBinding.txtMonth.text = student.month
        itemsBinding.txtYear.text = student.year
        itemsBinding.txtStudentName.text = student.studentName
        itemsBinding.txtTotal.text = student.total.toString()
        itemsBinding.txtRollno.text = student.rollNo
        itemsBinding.txtClassName.text = student.className

    }

    override fun getItemCount(): Int {
       return leaveTypeModels.size
    }

    fun getItem(position: Int) : Student {
        return leaveTypeModels.get(position)
    }
}

class VHItem(itemsBinding: BillingItemsBinding) : RecyclerView.ViewHolder(itemsBinding.root)
