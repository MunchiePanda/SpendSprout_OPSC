package com.example.spendsprout_opsc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupCurrencySpinner()
        setupLanguageSpinner()
        setupSwitches()
        setupButtons()
    }

    private fun setupCurrencySpinner() {
        val spinnerCurrency = findViewById<Spinner>(R.id.spinner_Currency)
        val currencies = arrayOf("ZAR", "USD", "EUR", "GBP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter
    }

    private fun setupLanguageSpinner() {
        val spinnerLanguage = findViewById<Spinner>(R.id.spinner_Language)
        val languages = arrayOf("English", "Afrikaans", "Zulu", "Xhosa")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter
    }

    private fun setupSwitches() {
        val switchTheme = findViewById<Switch>(R.id.switch_Theme)
        val switchFingerprint = findViewById<Switch>(R.id.switch_Fingerprint)
        val switchNotifications = findViewById<Switch>(R.id.switch_Notifications)

        // Load saved preferences
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        switchTheme.isChecked = sharedPref.getBoolean("DarkMode", false)
        switchFingerprint.isChecked = sharedPref.getBoolean("Fingerprint", false)
        switchNotifications.isChecked = sharedPref.getBoolean("Notifications", true)

        // Save preferences when switches are toggled
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("DarkMode", isChecked)
                apply()
            }
        }

        switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("Fingerprint", isChecked)
                apply()
            }
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean("Notifications", isChecked)
                apply()
            }
        }
    }

    private fun setupButtons() {
        val btnAbout = findViewById<Button>(R.id.btn_About)
        val btnHelp = findViewById<Button>(R.id.btn_Help)

        btnAbout.setOnClickListener {
            showDialog("About", "SpendSprout is a personal finance app designed to help you manage your expenses and income.")
        }

        btnHelp.setOnClickListener {
            showDialog("Help", "For help, please contact support@spendsprout.com or visit our website.")
        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
