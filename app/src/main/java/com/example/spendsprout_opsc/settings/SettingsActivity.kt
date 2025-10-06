package com.example.spendsprout_opsc.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class SettingsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Settings"
        
        // Set up menu button click listener
        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        settingsViewModel = SettingsViewModel(this)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupCurrencySpinner()
        setupLanguageSpinner()
        setupSwitches()
        setupGoals()
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
        switchTheme.isChecked = settingsViewModel.isDarkMode()
        switchFingerprint.isChecked = settingsViewModel.isFingerprintEnabled()
        switchNotifications.isChecked = settingsViewModel.isNotificationsEnabled()

        // Save preferences when switches are toggled
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setDarkMode(isChecked)
        }

        switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setFingerprintEnabled(isChecked)
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setNotificationsEnabled(isChecked)
        }
    }

    private fun setupGoals() {
        val edtMinGoal = findViewById<EditText>(R.id.edt_MinGoal)
        val edtMaxGoal = findViewById<EditText>(R.id.edt_MaxGoal)

        // Load saved goals
        edtMinGoal.setText(settingsViewModel.getMinMonthlyGoal().toString())
        edtMaxGoal.setText(settingsViewModel.getMaxMonthlyGoal().toString())

        // Save goals when text changes
        edtMinGoal.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val goal = edtMinGoal.text.toString().toFloatOrNull() ?: 0f
                settingsViewModel.setMinMonthlyGoal(goal)
                Toast.makeText(this, "Min goal saved", Toast.LENGTH_SHORT).show()
            }
        }

        edtMaxGoal.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val goal = edtMaxGoal.text.toString().toFloatOrNull() ?: 0f
                settingsViewModel.setMaxMonthlyGoal(goal)
                Toast.makeText(this, "Max goal saved", Toast.LENGTH_SHORT).show()
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

    private fun observeData() {
        // Observe ViewModel data changes
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                startActivity(Intent(this, com.example.spendsprout_opsc.CategoryOverviewActivity::class.java))
            }
            R.id.nav_transactions -> {
                startActivity(Intent(this, TransactionsActivity::class.java))
            }
            R.id.nav_accounts -> {
                startActivity(Intent(this, AccountsActivity::class.java))
            }
            R.id.nav_reports -> {
                android.widget.Toast.makeText(this, "Reports coming soon!", android.widget.Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                // Already in Settings, do nothing
            }
            R.id.nav_exit -> {
                finishAffinity()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle back button - finish this activity
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

