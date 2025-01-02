package com.school.erabi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.school.erabi.R
import com.school.erabi.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var homeBinding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        homeBinding.imgRegistration.setOnClickListener(this)
        homeBinding.imgGenerateExcel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0){
            homeBinding.imgRegistration ->
                startActivity(Intent(this@HomeActivity, UserSlipActivity::class.java))
            homeBinding.imgGenerateExcel ->
              //  Toast.makeText(this@HomeActivity, "Feature is under development", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@HomeActivity, GenerateExcelActivity::class.java))
        }
    }
}