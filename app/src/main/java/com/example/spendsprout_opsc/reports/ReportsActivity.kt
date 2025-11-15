package com.example.spendsprout_opsc.reports

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import java.util.*

class ReportsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var reportsViewModel: ReportsViewModel
    private lateinit var progressBarSpending: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        progressBarSpending = findViewById(R.id.progressBar_Spending)

        // Set up the toolbar from the included header bar
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Reports"

        // Set up menu button click listener
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        reportsViewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupMonthSpinner()
        setupExportButton()
        setupChart()
        updateProgressBar()
    }

    override fun onResume() {
        super.onResume()
        // Refresh chart and progress in case data changed
        setupChart()
        updateProgressBar()
    }

    private fun setupMonthSpinner() {
        val spinnerMonth = findViewById<Spinner>(R.id.spinner_Month)
        val months = arrayOf("January 2025", "February 2025", "March 2025", "April 2025", "May 2025", "June 2025")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = adapter
    }

    private fun setupExportButton() {
        val btnExport = findViewById<Button>(R.id.btn_Export)
        btnExport.setOnClickListener {
            Toast.makeText(this, "Report exported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChart() {
        val chartView = findViewById<com.example.spendsprout_opsc.overview.ChartView>(R.id.chartView)
        // Load daily spend for current month and use max goal as target
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val monthlyTarget = prefs.getFloat("MaxMonthlyGoal", 0f).toDouble()
        reportsViewModel.loadDailySpendSeries(
            getStartOfMonth(),
            getEndOfMonth(),
            monthlyTarget
        )
        reportsViewModel.dailySpendSeries.observe(this) { points ->
            chartView.setData(points)
        }
    }

    private fun updateProgressBar() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val maxGoal = prefs.getFloat("MaxMonthlyGoal", 0f).toDouble()
        reportsViewModel.loadMonthlySpent(
            getStartOfMonth(),
            getEndOfMonth()
        )
        reportsViewModel.monthlySpent.observe(this) { totalSpent ->
            val percent = if (maxGoal > 0) ((totalSpent / maxGoal) * 100).toInt().coerceIn(0, 100) else 0
            progressBarSpending.progress = percent
        }
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
                // Already in Reports, do nothing
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
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

    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
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
