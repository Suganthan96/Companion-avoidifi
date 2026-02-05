package com.example.digitalwellbeingviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.digitalwellbeingviewer.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()
            
            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user name to SharedPreferences
            saveUserName(userName)
            
            // Show toast
            Toast.makeText(this, "Welcome, $userName!", Toast.LENGTH_SHORT).show()
            
            // Navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Allow Enter key to submit
        binding.etUserName.setOnKeyListener { _, keyCode, event ->
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER && 
                event.action == android.view.KeyEvent.ACTION_DOWN) {
                binding.btnContinue.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun saveUserName(userName: String) {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_name", userName)
            putBoolean("onboarding_complete", true)
            apply()
        }
    }

    companion object {
        fun isOnboardingComplete(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return sharedPref.getBoolean("onboarding_complete", false)
        }

        fun getUserName(context: Context): String {
            val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return sharedPref.getString("user_name", "User") ?: "User"
        }
    }
}
