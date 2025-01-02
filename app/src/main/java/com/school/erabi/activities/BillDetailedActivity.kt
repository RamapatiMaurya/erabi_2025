package com.school.erabi.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.school.erabi.databinding.ActivityBillDetailedBinding

class BillDetailedActivity : AppCompatActivity() {
    private lateinit var billDetailedBinding: ActivityBillDetailedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billDetailedBinding = ActivityBillDetailedBinding.inflate(layoutInflater)
        setContentView(billDetailedBinding.root)
    }
}