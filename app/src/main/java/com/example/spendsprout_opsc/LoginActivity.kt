package com.example.spendsprout_opsc

import android.content.Intent  // Like Unity's "using UnityEngine.SceneManagement;"
import android.os.Bundle       // Like Unity's "void Start()"
import android.widget.Button   // Like Unity's "public Button loginButton;"
import android.widget.Toast    // Like Unity's "Debug.Log()" or a popup
import androidx.appcompat.app.AppCompatActivity  // Like Unity's "MonoBehaviour"
import com.google.android.material.textfield.TextInputEditText  // Like Unity's "public InputField emailInput;"

class LoginActivity : AppCompatActivity() {  // This class is like a Unity script attached to a GameObject

    // Called when the screen loads (like Unity's "void Start()")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)  // Load the "prefab" (login_screen.xml)

        // Get references to UI elements (like Unity's "GetComponent<>()" or dragging fields into the inspector)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)  // Like "public InputField emailInput;"
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)  // Like "public InputField passwordInput;"
        val loginButton = findViewById<Button>(R.id.loginButton)  // Like "public Button loginButton;"

        // Add a click listener to the login button (like Unity's "loginButton.onClick.AddListener()")
        loginButton.setOnClickListener {
            // Get the text from the input fields (like "emailInput.text" in Unity)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if fields are empty (like an "if" statement in Unity)
            if (email.isEmpty() || password.isEmpty()) {
                // Show an error message (like "Debug.Log()" or a popup in Unity)
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // If fields are filled, go to CategoryOverviewActivity (like "SceneManager.LoadScene()" in Unity)
                val intent = Intent(this, OverviewActivity::class.java)
                startActivity(intent)  // Load the new "scene" (CategoryOverviewActivity)
                finish()  // Close this "scene" (LoginActivity), like "Destroy(gameObject)"
            }
        }
    }
}
