package com.example.spendsprout_opsc.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        setupUI()
    }

    private fun setupUI() {
        val edtUsername = findViewById<EditText>(R.id.edt_RegisterUsername)
        val edtPassword = findViewById<EditText>(R.id.edt_RegisterPassword)
        val edtConfirmPassword = findViewById<EditText>(R.id.edt_RegisterConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btn_Register)

        btnRegister.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userExists(username)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(username, password)
            Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun userExists(username: String): Boolean {
        return sharedPreferences.getString("user_$username", null) != null
    }

    private fun registerUser(username: String, password: String) {
        sharedPreferences.edit()
            .putString("user_$username", password)
            .apply()
    }
}
