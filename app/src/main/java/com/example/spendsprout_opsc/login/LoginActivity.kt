package com.example.spendsprout_opsc.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private var txtSignUp: TextView? = null
    private var progressBar: ProgressBar? = null
    
    private var isSignUpMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        
        // Check if user is already logged in (Firebase or SharedPreferences)
        if (isLoggedIn()) {
            startActivity(Intent(this, OverviewActivity::class.java))
            finish()
            return
        }
        
        setupUI()
    }
    
    private fun setupUI() {
        edtUsername = findViewById(R.id.edt_Username)
        edtPassword = findViewById(R.id.edt_Password)
        btnLogin = findViewById(R.id.btn_Login)
        
        // Get sign up text view and progress bar
        txtSignUp = findViewById<TextView>(R.id.txt_SignUp)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        
        // Setup sign up toggle if text view exists
        txtSignUp?.let {
            setupSignUpToggle()
        }
        
        btnLogin.setOnClickListener {
            handleLoginOrSignUp()
        }
    }
    
    private fun setupSignUpToggle() {
        txtSignUp?.setOnClickListener {
            toggleSignUpMode()
        }
    }
    
    private fun toggleSignUpMode() {
        isSignUpMode = !isSignUpMode
        if (isSignUpMode) {
            btnLogin.text = "Sign Up"
            txtSignUp?.text = "Already have an account? Sign In"
            edtUsername.hint = "Email"
        } else {
            btnLogin.text = "Sign In"
            txtSignUp?.text = "Don't have an account? Sign Up"
            edtUsername.hint = "Username or Email"
        }
    }
    
    private fun handleLoginOrSignUp() {
        val usernameOrEmail = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter ${if (isSignUpMode) "email" else "username/email"} and password", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate email format for sign up
        if (isSignUpMode && !android.util.Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show progress
        showProgress(true)
        btnLogin.isEnabled = false
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isSignUpMode) {
                    // Sign up new user with Firebase
                    signUpWithFirebase(usernameOrEmail, password)
                } else {
                    // Try to sign in
                    signInWithFirebase(usernameOrEmail, password)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showProgress(false)
                    btnLogin.isEnabled = true
                    handleAuthError(e)
                }
            }
        }
    }
    
    private suspend fun signUpWithFirebase(email: String, password: String) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Save login status
                withContext(Dispatchers.Main) {
                    saveLoginStatus(email)
                    Log.d("LoginActivity", "User signed up: ${user.uid}")
                    Toast.makeText(this@LoginActivity, "Account created! Welcome, $email", Toast.LENGTH_SHORT).show()
                    navigateToOverview()
                }
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error signing up", e)
            throw e
        }
    }
    
    private suspend fun signInWithFirebase(usernameOrEmail: String, password: String) {
        try {
            // First try with email (if it looks like email)
            val email = if (android.util.Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                usernameOrEmail
            } else {
                // For username, try to convert to email format or use as-is
                // In a real app, you might have a mapping of username to email
                // For now, we'll try the username as email (might fail, then fall back to SharedPreferences)
                usernameOrEmail
            }
            
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                withContext(Dispatchers.Main) {
                    saveLoginStatus(usernameOrEmail)
                    Log.d("LoginActivity", "User signed in: ${user.uid}")
                    Toast.makeText(this@LoginActivity, "Login successful! Welcome back, $usernameOrEmail", Toast.LENGTH_SHORT).show()
                    navigateToOverview()
                }
            }
        } catch (e: Exception) {
            // If Firebase sign in fails and username doesn't look like email, try legacy login
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                Log.d("LoginActivity", "Firebase sign in failed, trying legacy login for username: $usernameOrEmail")
                withContext(Dispatchers.Main) {
                    tryLegacyLogin(usernameOrEmail, password)
                }
            } else {
                throw e
            }
        }
    }
    
    private fun tryLegacyLogin(username: String, password: String) {
        // Fallback to old SharedPreferences-based login for backward compatibility
        if (userExists(username)) {
            if (validateLogin(username, password)) {
                saveLoginStatus(username)
                Toast.makeText(this, "Login successful! Welcome back, $username", Toast.LENGTH_SHORT).show()
                navigateToOverview()
            } else {
                showProgress(false)
                btnLogin.isEnabled = true
                Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } else {
            showProgress(false)
            btnLogin.isEnabled = true
            Toast.makeText(this, "User not found. Please sign up first.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleAuthError(e: Exception) {
        val errorMessage = when (e) {
            is FirebaseAuthWeakPasswordException -> {
                "Password is too weak. Please use at least 6 characters."
            }
            is FirebaseAuthUserCollisionException -> {
                "An account with this email already exists. Please sign in instead."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                "Invalid email or password. Please try again."
            }
            else -> {
                val message = e.message ?: "Authentication failed. Please try again."
                if (message.contains("EMAIL_NOT_FOUND") || message.contains("INVALID_PASSWORD")) {
                    "Invalid email or password. Please try again."
                } else if (message.contains("WEAK_PASSWORD")) {
                    "Password is too weak. Please use at least 6 characters."
                } else if (message.contains("EMAIL_EXISTS")) {
                    "An account with this email already exists. Please sign in instead."
                } else {
                    message
                }
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        Log.e("LoginActivity", "Authentication error", e)
    }
    
    private fun navigateToOverview() {
        showProgress(false)
        btnLogin.isEnabled = true
        startActivity(Intent(this, OverviewActivity::class.java))
        finish()
    }
    
    private fun showProgress(show: Boolean) {
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun isLoggedIn(): Boolean {
        // Check both Firebase Auth and SharedPreferences for backward compatibility
        return auth.currentUser != null || sharedPreferences.getBoolean("is_logged_in", false)
    }
    
    private fun saveLoginStatus(usernameOrEmail: String) {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", true)
            .putString("username", usernameOrEmail)
            .apply()
    }
    
    // Legacy methods for backward compatibility with old SharedPreferences users
    private fun userExists(username: String): Boolean {
        return sharedPreferences.getString("user_$username", null) != null
    }
    
    private fun validateLogin(username: String, password: String): Boolean {
        val storedPassword = sharedPreferences.getString("user_$username", null)
        return storedPassword == password
    }
    
    companion object {
        fun logout(sharedPreferences: SharedPreferences, auth: FirebaseAuth) {
            // Sign out from Firebase
            auth.signOut()
            
            // Clear SharedPreferences
            sharedPreferences.edit()
                .putBoolean("is_logged_in", false)
                .remove("username")
                .apply()
        }
    }
}
