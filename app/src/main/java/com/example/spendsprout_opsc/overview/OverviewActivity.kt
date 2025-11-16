package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.edit.EditBudgetActivity
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
class OverviewActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton
    
    //ViewModel
    private lateinit var overviewViewModel: OverviewViewModel
    
    //Login management
    private lateinit var sharedPreferences: SharedPreferences

    private val budgetEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            android.util.Log.d("OverviewActivity", "Budget edit successful, refreshing data...")
            // Budget was updated successfully, refresh data immediately
            overviewViewModel.refreshCurrentBudget()
            Toast.makeText(this, "Budget updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * onCreate() - Like Unity's Start() method
     * This is called when the scene/activity is first created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the scene layout - like Unity's SceneManager.LoadScene()
        setContentView(R.layout.activity_overview)
        
        // Initialize SharedPreferences for login management
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        //MENU DRAWER SETUP
            //MenuDrawer: Drawer Layout/ Menu Code and connections
            drawerLayout = findViewById(R.id.drawerLayout)
            navigationView = findViewById(R.id.navigationView)
            
            // Set up the toolbar from the included layout
            val headerBar = findViewById<View>(R.id.header_bar)
            val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            // Enable back button functionality
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            
            // Set up menu button click listener
            val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
            btnMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            //MenuDrawer: Access the close button from the navigation view header
            val headerView = navigationView.getHeaderView(0)
            btnCloseMenu = headerView.findViewById(R.id.btn_CloseMenu)
            
            // Set the username in the navigation header
            val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
            val currentUsername = sharedPreferences.getString("username", "User")
            txtUsername.text = currentUsername

            //MenuDrawer: Close menu button click listener to close drawer
            btnCloseMenu.setOnClickListener {
                drawerLayout.closeDrawer(navigationView)
            }

            //MenuDrawer: respond to menu item clicks using lambda
            navigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.nav_overview -> {
                        // Already here
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
                        startActivity(Intent(this, ReportsActivity::class.java))
                    }
                    R.id.nav_settings -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                    R.id.nav_logout -> {
                        logout()
                    }
                    R.id.nav_exit -> {
                        finishAffinity()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
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

        // Initialize ViewModel
        overviewViewModel = OverviewViewModel()
        
        // Initialize UI components - like Unity's UI setup in Start()
        setupUI()
        // Start observing data changes - like Unity's coroutines or Update()
        observeData()
    }

    private fun setupUI() {
        setupBudgetInfo()
        setupBudgetOverviewClickListener()
        setupTransactionRecyclerView()
        setupCategoriesRecyclerView()
        setupChart()
    }

    private fun setupBudgetInfo() {
        // Update budget info with real data from ViewModel
        lifecycleScope.launch {
            overviewViewModel.currentBudget.collect { currentBudget ->
                if (currentBudget != null) {
                    updateBudgetDisplay(currentBudget)
                } else {
                    // Show default values if no budget exists
                    updateBudgetDisplayWithDefaults()
                }
            }
        }
    }
    
    private fun updateBudgetDisplay(budget: com.example.spendsprout_opsc.roomdb.Budget_Entity) {
        android.util.Log.d("OverviewActivity", "Updating budget display with: ${budget.budgetName}, balance: ${budget.budgetBalance}, opening: ${budget.openingBalance}")
        
        val budgetBalanceTextView = findViewById<TextView>(R.id.txt_BudgetBalance)
        budgetBalanceTextView.text = "R ${String.format("%.2f", budget.budgetBalance)}"
        
        val budgetAllocationTextView = findViewById<TextView>(R.id.txt_BudgetAllocation)
        budgetAllocationTextView.text = "R ${String.format("%.2f", budget.openingBalance)}"
        
        val minTextView = findViewById<TextView>(R.id.txt_Min)
        minTextView.text = "R ${String.format("%.2f", budget.budgetMinGoal)}"
        
        val maxTextView = findViewById<TextView>(R.id.txt_Max)
        maxTextView.text = "R ${String.format("%.2f", budget.budgetMaxGoal)}"
        
        android.util.Log.d("OverviewActivity", "Budget display updated successfully")
    }
    
    private fun updateBudgetDisplayWithDefaults() {
        val budgetBalanceTextView = findViewById<TextView>(R.id.txt_BudgetBalance)
        budgetBalanceTextView.text = "R 0.00"
        
        val budgetAllocationTextView = findViewById<TextView>(R.id.txt_BudgetAllocation)
        budgetAllocationTextView.text = "R 0.00"
        
        val minTextView = findViewById<TextView>(R.id.txt_Min)
        minTextView.text = "R 0.00"
        
        val maxTextView = findViewById<TextView>(R.id.txt_Max)
        maxTextView.text = "R 0.00"
    }

    private fun setupBudgetOverviewClickListener() {
        val budgetOverviewLayout = findViewById<LinearLayout>(R.id.layout_budgetOverview)
        budgetOverviewLayout.setOnClickListener {
            // Navigate to EditBudgetActivity
            val intent = Intent(this, EditBudgetActivity::class.java)
            
            // Pass current budget data if it exists
            val currentBudget = overviewViewModel.getCurrentBudget()
            if (currentBudget != null) {
                intent.putExtra("budget", currentBudget)
            }
            
            budgetEditLauncher.launch(intent)
        }
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //val transactions = overviewViewModel.getRecentTransactions()
        //recyclerView.adapter = TransactionAdapter(transactions) // TODO: Implement TransactionAdapter
    }
    
    private fun setupCategoriesRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //val categories = overviewViewModel.getCategories()
        //recyclerView.adapter = CategoryAdapter(categories) // TODO: Implement CategoryAdapter
    }

    private fun setupChart() {
        // Chart setup will be handled by the custom view
    }

    private fun observeData() {
        // Observe ViewModel data changes
        lifecycleScope.launch {
            overviewViewModel.budgets.collect { budgets ->
                // Update UI when budgets change
                if (budgets.isNotEmpty()) {
                    val currentBudget = overviewViewModel.getCurrentBudget()
                    if (currentBudget != null) {
                        updateBudgetDisplay(currentBudget)
                    }
                } else {
                    updateBudgetDisplayWithDefaults()
                }
            }
        }
        
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        overviewViewModel.refreshData()
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
        when (item.itemId) {
            android.R.id.home -> {
                // Handle back button - finish this activity
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun logout() {
        // Clear login status
        sharedPreferences.edit()
            .putBoolean("is_logged_in", false)
            .remove("username")
            .apply()
        
        // Navigate back to login screen
        val intent = Intent(this, com.example.spendsprout_opsc.login.LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
