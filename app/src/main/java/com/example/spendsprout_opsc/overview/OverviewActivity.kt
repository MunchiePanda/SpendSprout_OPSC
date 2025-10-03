package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

/**
 * OverviewActivity - Main Dashboard Scene
 * 
 * This is like Unity's Main Menu Scene - the first thing users see when they open the app.
 * Similar to Unity's SceneManager.LoadScene("MainMenu") being the default scene.
 * 
 * Responsibilities:
 * - Display total balance (like Unity's UI Text showing player score)
 * - Show recent transactions (like Unity's UI List showing recent events)
 * - Render income vs expenses chart (like Unity's custom UI component)
 * - Handle navigation to other screens (like Unity's SceneManager.LoadScene())
 */
class OverviewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var overviewViewModel: OverviewViewModel

    /**
     * onCreate() - Like Unity's Start() method
     * This is called when the scene/activity is first created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the scene layout - like Unity's SceneManager.LoadScene()
        setContentView(R.layout.activity_overview)

        // Get references to UI components - like Unity's GameObject.Find()
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar - like Unity's UI Canvas setup
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Create navigation drawer toggle - like Unity's UI Button setup
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the drawer indicator in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Overview"

        // Set up navigation listener - like Unity's Button.onClick.AddListener()
        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel - like Unity's GetComponent<Script>()
        overviewViewModel = OverviewViewModel()

        // Initialize UI components - like Unity's UI setup in Start()
        setupUI()
        // Start observing data changes - like Unity's coroutines or Update()
        observeData()
    }

    private fun setupUI() {
        setupBalanceText()
        setupTransactionRecyclerView()
        setupChart()
    }

    private fun setupBalanceText() {
        val balanceTextView = findViewById<TextView>(R.id.txt_Balance)
        balanceTextView.text = "Total Balance: R 12,780"
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val transactions = overviewViewModel.getRecentTransactions()
        recyclerView.adapter = TransactionAdapter(transactions)
    }

    private fun setupChart() {
        // Chart setup will be handled by the custom view
        val chartView = findViewById<com.example.spendsprout_opsc.overview.ChartView>(R.id.chartView)
        chartView.setData(overviewViewModel.getChartData())
    }

    private fun observeData() {
        // Observe ViewModel data changes
        // This will be implemented when we add LiveData/Flow
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                // Already in Overview, do nothing
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
                startActivity(Intent(this, ReportsActivity::class.java))
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

