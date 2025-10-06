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
        
        setupUI()
    }
    
    private fun setupUI() {
        val edtUsername = findViewById<EditText>(R.id.edt_Username)
        val edtPassword = findViewById<EditText>(R.id.edt_Password)
        val btnLogin = findViewById<Button>(R.id.btn_Login)
        
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Check if user exists
            if (userExists(username)) {
                // Try to login
                if (validateLogin(username, password)) {
                    saveLoginStatus(username)
                    Toast.makeText(this, "Login successful! Welcome back, $username", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OverviewActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Register new user
                registerUser(username, password)
                saveLoginStatus(username)
                Toast.makeText(this, "Account created! Welcome, $username", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, OverviewActivity::class.java))
                finish()
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
    
    private fun userExists(username: String): Boolean {
        return sharedPreferences.getString("user_$username", null) != null
    }
    
    private fun validateLogin(username: String, password: String): Boolean {
        val storedPassword = sharedPreferences.getString("user_$username", null)
        return storedPassword == password
    }
    
    private fun registerUser(username: String, password: String) {
        sharedPreferences.edit()
            .putString("user_$username", password)
            .apply()
    }
    
    fun logout() {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", false)
            .remove("username")
            .apply()
    }
}
