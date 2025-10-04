package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

    // OLD VARIABLES - COMMENTED OUT AS LAYOUT STRUCTURE CHANGED
    //private lateinit var drawerLayout: DrawerLayout  // OLD: was drawer_layout
    //private lateinit var navView: NavigationView    // OLD: was nav_view
    //private lateinit var overviewViewModel: OverviewViewModel  // NOT YET IMPLEMENTED

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    /**
     * onCreate() - Like Unity's Start() method
     * This is called when the scene/activity is first created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the scene layout - like Unity's SceneManager.LoadScene()
        setContentView(R.layout.activity_overview)

        //MENU DRAWER SETUP
            //MenuDrawer: Drawer Layout/ Menu Code and connections
            drawerLayout = findViewById(R.id.drawerLayout)
            navigationView = findViewById(R.id.navigationView)
            
            // Set up the toolbar from the included layout
            val headerBar = findViewById<View>(R.id.header_bar)
            val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            //MenuDrawer: Drawer Layout/ Menu Code and connections
            toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()  //tell toggle it is ready to be used
            //MenuDrawer
            supportActionBar?.setDisplayHomeAsUpEnabled(true)   //able to open toggle, when it is opened the toggle button moves to back arrow

            //MenuDrawer: Access the close button from the navigation view header
            val headerView = navigationView.getHeaderView(0)
            btnCloseMenu = headerView.findViewById(R.id.btn_CloseMenu)

            //MenuDrawer: Close menu button click listener to close drawer
            btnCloseMenu.setOnClickListener {
                drawerLayout.closeDrawer(navigationView)
            }

            //MenuDrawer: respond to menu item clicks
            navigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.nav_overview
                        -> Toast.makeText(applicationContext, "Overview", Toast.LENGTH_SHORT).show()
                    R.id.nav_reports
                        -> Toast.makeText(applicationContext, "Overview", Toast.LENGTH_SHORT).show()
                }
                true
            }

        /*
        // OLD CODE - COMMENTED OUT AS LAYOUT STRUCTURE CHANGED
        // Get references to UI components - like Unity's GameObject.Find()
        // drawerLayout = findViewById(R.id.drawer_layout)  // OLD: was drawer_layout
        // navView = findViewById(R.id.nav_view)            // OLD: was nav_view

        // Set up the toolbar - like Unity's UI Canvas setup
        // val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)  // OLD: direct findViewById
        // setSupportActionBar(toolbar)

        // Create navigation drawer toggle - like Unity's UI Button setup
        // val toggle = ActionBarDrawerToggle(
        //     this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        // )
        // drawerLayout.addDrawerListener(toggle)
        // toggle.syncState()

        // Enable the drawer indicator in the action bar
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // supportActionBar?.setHomeButtonEnabled(true)
        // supportActionBar?.title = "Overview"

        // Set up navigation listener - like Unity's Button.onClick.AddListener()
        // navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel - like Unity's GetComponent<Script>()
        // overviewViewModel = OverviewViewModel()
        */

        // Initialize UI components - like Unity's UI setup in Start()
        setupUI()
        // Start observing data changes - like Unity's coroutines or Update()
        observeData()
    }

    private fun setupUI() {
        setupBudgetInfo()
        setupTransactionRecyclerView()
        setupCategoriesRecyclerView()
        setupChart()
    }

    private fun setupBudgetInfo() {
        // Update the budget balance display
        val budgetBalanceTextView = findViewById<TextView>(R.id.txt_BudgetBalance)
        budgetBalanceTextView.text = "R 12,780"
        
        // Update other budget fields
        val budgetAllocationTextView = findViewById<TextView>(R.id.txt_BudgetAllocation)
        budgetAllocationTextView.text = "R 100,000"
        
        val minTextView = findViewById<TextView>(R.id.txt_Min)
        minTextView.text = "R 200"
        
        val maxTextView = findViewById<TextView>(R.id.txt_Max)
        maxTextView.text = "R 1,000"
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //val transactions = overviewViewModel.getRecentTransactions()
        //recyclerView.adapter = TransactionAdapter(transactions)
    }
    
    private fun setupCategoriesRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //val categories = overviewViewModel.getCategories()
        //recyclerView.adapter = CategoryAdapter(categories)
    }

    private fun setupChart() {
        // Chart setup will be handled by the custom view
        val chartView = findViewById<com.example.spendsprout_opsc.overview.ChartView>(R.id.chartView)
        //chartView.setData(overviewViewModel.getChartData())
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

    /*
    // OLD onBackPressed - COMMENTED OUT AS NOT CURRENTLY USED
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    */

    //MenuDrawer: Drawer Layout/ Menu Code
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

