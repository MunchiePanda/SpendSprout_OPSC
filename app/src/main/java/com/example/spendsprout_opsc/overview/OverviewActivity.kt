package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

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
    private val overviewViewModel: OverviewViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var categoryAdapter: CategorySummaryAdapter
    private lateinit var accountAdapter: AccountSummaryAdapter

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

        // Initialize UI components - like Unity's UI setup in Start()
        setupUI()
        // Start observing data changes - like Unity's coroutines or Update()
        observeData()
    }

    private fun setupUI() {
        setupBalanceText()
        setupTransactionRecyclerView()
        setupCategoryRecyclerView()
        setupAccountRecyclerView()
        setupChart()
        setupFab()
    }

    private fun setupBalanceText() {
        val balanceTextView = findViewById<TextView>(R.id.txt_Balance)
        // Balance will be updated in observeData()
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        transactionAdapter = TransactionAdapter(emptyList())
        recyclerView.adapter = transactionAdapter
    }

    private fun setupCategoryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        categoryAdapter = CategorySummaryAdapter(emptyList()) { category ->
            // Handle category click - like Unity's UI interaction
            android.widget.Toast.makeText(this, "Clicked ${category.name}", android.widget.Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = categoryAdapter
    }

    private fun setupAccountRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Accounts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        accountAdapter = AccountSummaryAdapter(emptyList()) { account ->
            // Handle account click - like Unity's UI interaction
            android.widget.Toast.makeText(this, "Clicked ${account.name}", android.widget.Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = accountAdapter
    }

    private fun setupChart() {
        // Chart setup will be handled by the custom view
        val chartView = findViewById<com.example.spendsprout_opsc.overview.ChartView>(R.id.chartView)
        // Chart data will be set in observeData()
    }

    private fun setupFab() {
        val fabAddTransaction = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_AddTransaction)
        fabAddTransaction.setOnClickListener {
            // Navigate to add new transaction
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        val balanceTextView = findViewById<TextView>(R.id.txt_Balance)
        val chartView = findViewById<com.example.spendsprout_opsc.overview.ChartView>(R.id.chartView)
        
        lifecycleScope.launch {
            // Observe total balance
            overviewViewModel.totalBalance.collect { balance ->
                balanceTextView.text = "R ${String.format("%.0f", balance)}"
            }
        }
        
        lifecycleScope.launch {
            // Observe recent transactions
            overviewViewModel.recentTransactions.collect { transactions ->
                transactionAdapter.updateData(transactions)
            }
        }
        
        lifecycleScope.launch {
            // Observe category summary
            overviewViewModel.categorySummary.collect { categories ->
                categoryAdapter.updateData(categories)
            }
        }
        
        lifecycleScope.launch {
            // Observe account summary
            overviewViewModel.accountSummary.collect { accounts ->
                accountAdapter.updateData(accounts)
            }
        }
        
        lifecycleScope.launch {
            // Observe chart data
            overviewViewModel.chartData.collect { chartData ->
                chartView.setData(chartData)
            }
        }
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

    @Deprecated("Use onBackPressedDispatcher instead")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

