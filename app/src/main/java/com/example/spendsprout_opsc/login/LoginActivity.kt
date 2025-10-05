package com.example.spendsprout_opsc.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.OverviewActivity

class LoginActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        
        // Check if user is already logged in
        if (isLoggedIn()) {
            startActivity(Intent(this, OverviewActivity::class.java))
            finish()
            return
        }
        
        // TEMPORARY: Auto-login for testing (remove this later)
        saveLoginStatus("testuser")
        startActivity(Intent(this, OverviewActivity::class.java))
        finish()
        return
        
        setupUI()
    }
    
    private fun setupUI() {
        val edtUsername = findViewById<EditText>(R.id.edt_Username)
        val edtPassword = findViewById<EditText>(R.id.edt_Password)
        val btnLogin = findViewById<Button>(R.id.btn_Login)
        
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Simple validation - accept any non-empty credentials for testing
            if (username.isNotEmpty() && password.isNotEmpty()) {
                saveLoginStatus(username)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, OverviewActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
    
    private fun saveLoginStatus(username: String) {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", true)
            .putString("username", username)
            .apply()
    }
}
