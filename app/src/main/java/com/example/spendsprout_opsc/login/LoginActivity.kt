package com.example.spendsprout_opsc.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.firebase.FirebaseRepositoryProvider
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        firebaseAuth = FirebaseAuth.getInstance()

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
            val email = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            saveLoginStatus(user.uid, email)
                            Toast.makeText(this, "Login successful! Welcome back, $email", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, OverviewActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login successful but no user returned", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // If sign in fails, try to register the user
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { registrationTask ->
                                if (registrationTask.isSuccessful) {
                                    val user = firebaseAuth.currentUser
                                    if (user != null) {
                                        saveLoginStatus(user.uid, email)
                                        Toast.makeText(this, "Account created! Welcome, $email", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, OverviewActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Account created but no user returned", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val errorMessage = registrationTask.exception?.message ?: "Unknown error"
                                    Toast.makeText(this, "Authentication failed: $errorMessage", Toast.LENGTH_LONG).show()
                                    android.util.Log.e("LoginActivity", "Authentication failed", registrationTask.exception)
                                }
                            }
                    }
                }
        }
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    private fun saveLoginStatus(userId: String, email: String) {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_id", userId)
            .putString("username", email)
            .apply()
    }

    fun logout() {
        firebaseAuth.signOut()
        sharedPreferences.edit()
            .putBoolean("is_logged_in", false)
            .remove("user_id")
            .remove("username")
            .apply()
    }
}
