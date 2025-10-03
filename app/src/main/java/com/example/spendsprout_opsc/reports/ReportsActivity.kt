package com.example.spendsprout_opsc.reports

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class ReportsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var reportsViewModel: ReportsViewModel
    private lateinit var circularProgressView: CircularProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        circularProgressView = findViewById(R.id.circularProgressView)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the drawer indicator in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Reports"

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        reportsViewModel = ReportsViewModel()

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupMonthFilter()
        setupSegmentedControl()
        setupCircularProgress()
        setupChart()
        setupFab()
    }

    private fun setupMonthFilter() {
        val btnMonthFilter = findViewById<android.widget.LinearLayout>(R.id.btn_MonthFilter)
        btnMonthFilter.setOnClickListener {
            // Show month selection dialog
            Toast.makeText(this, "Month filter clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSegmentedControl() {
        val btnLineChart = findViewById<android.widget.LinearLayout>(R.id.btn_LineChart)
        val btnPieChart = findViewById<android.widget.LinearLayout>(R.id.btn_PieChart)
        
        btnLineChart.setOnClickListener {
            // Switch to line chart view
            Toast.makeText(this, "Line chart selected", Toast.LENGTH_SHORT).show()
        }
        
        btnPieChart.setOnClickListener {
            // Switch to pie chart view
            Toast.makeText(this, "Pie chart selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCircularProgress() {
        circularProgressView.setProgress(0.8f) // 80% progress
    }

    private fun setupChart() {
        val chartView = findViewById<com.example.spendsprout_opsc.reports.ChartView>(R.id.chartView)
        chartView.setData(reportsViewModel.getChartData())
    }

    private fun setupFab() {
        val fabAddReport = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_AddReport)
        fabAddReport.setOnClickListener {
            // Navigate to create new report or export data
            android.widget.Toast.makeText(this, "Add Report clicked", android.widget.Toast.LENGTH_SHORT).show()
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
                startActivity(Intent(this, CategoriesActivity::class.java))
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

