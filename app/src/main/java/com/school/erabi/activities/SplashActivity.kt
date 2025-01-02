package com.school.erabi.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.school.erabi.databinding.ActivitySplashBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val hideHandler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideHandler.postDelayed({
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        }, 2000)


    }


    override fun onPause() {
        super.onPause()
        finish()
    }

}