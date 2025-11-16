package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class EditBudgetActivity : AppCompatActivity() {
    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private lateinit var editBudgetViewModel: EditBudgetViewModel
    private var existingBudget: com.example.spendsprout_opsc.roomdb.Budget_Entity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)
        
        // Get budget data from intent if editing existing budget
        existingBudget = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("budget", com.example.spendsprout_opsc.roomdb.Budget_Entity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("budget") as? com.example.spendsprout_opsc.roomdb.Budget_Entity
        }

        //MENU DRAWER SETUP
        //MenuDrawer: Drawer Layout/ Menu Code and connections
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

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

        //MenuDrawer: Close menu button click listener to close drawer
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }

        //MenuDrawer: respond to menu item clicks - using lambda like OverviewActivity
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> {
                    Toast.makeText(this, "Navigating to Overview", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, com.example.spendsprout_opsc.overview.OverviewActivity::class.java))
                }
                R.id.nav_categories -> {
                    Toast.makeText(this, "Navigating to Categories", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CategoriesActivity::class.java))
                }
                R.id.nav_transactions -> {
                    Toast.makeText(this, "Navigating to Transactions", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, TransactionsActivity::class.java))
                }
                R.id.nav_accounts -> {
                    Toast.makeText(this, "Navigating to Accounts", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AccountsActivity::class.java))
                }
                R.id.nav_reports -> {
                    Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Navigating to Settings", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_exit -> {
                    Toast.makeText(this, "Exiting app", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Initialize ViewModel
        editBudgetViewModel = EditBudgetViewModel()

        setupUI()

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveBudget)
            .setOnClickListener { saveBudget() }
    }

    private fun setupUI() {
        populateFields()
        setupButtons()
    }
    
    private fun populateFields() {
        if (existingBudget != null) {
            // Populate fields with existing budget data
            findViewById<EditText>(R.id.edt_BudgetName).setText(existingBudget!!.budgetName)
            findViewById<EditText>(R.id.edt_OpeningBalance).setText(String.format("%.2f", existingBudget!!.openingBalance))
            findViewById<EditText>(R.id.edt_MinGoal).setText(String.format("%.2f", existingBudget!!.budgetMinGoal))
            findViewById<EditText>(R.id.edt_MaxGoal).setText(String.format("%.2f", existingBudget!!.budgetMaxGoal))
            existingBudget!!.budgetNotes?.let { notes ->
                findViewById<EditText>(R.id.edt_Notes).setText(notes)
            }
        }
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val edtBudgetName = findViewById<EditText>(R.id.edt_BudgetName)
        val edtOpeningBalance = findViewById<EditText>(R.id.edt_OpeningBalance)
        val edtMinGoal = findViewById<EditText>(R.id.edt_MinGoal)
        val edtMaxGoal = findViewById<EditText>(R.id.edt_MaxGoal)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val budgetName = edtBudgetName.text.toString()
        val openingBalance = edtOpeningBalance.text.toString()
        val minGoal = edtMinGoal.text.toString()
        val maxGoal = edtMaxGoal.text.toString()
        val notes = edtNotes.text.toString()

        if (budgetName.isEmpty() || openingBalance.isEmpty() || minGoal.isEmpty() || maxGoal.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val openingBalanceVal = openingBalance.toDoubleOrNull()
        val minGoalVal = minGoal.toDoubleOrNull()
        val maxGoalVal = maxGoal.toDoubleOrNull()
        
        if (openingBalanceVal == null || minGoalVal == null || maxGoalVal == null) {
            Toast.makeText(this, "All numeric fields must contain valid numbers", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate goal ranges
        if (minGoalVal >= maxGoalVal) {
            Toast.makeText(this, "Minimum goal must be less than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (minGoalVal > openingBalanceVal || maxGoalVal > openingBalanceVal) {
            Toast.makeText(this, "Goals cannot exceed opening balance", Toast.LENGTH_SHORT).show()
            return
        }

        // Save budget using ViewModel with coroutine to wait for completion
        lifecycleScope.launch {
            try {
                if (existingBudget != null) {
                    // Update existing budget
                    editBudgetViewModel.updateBudget(existingBudget!!.id, budgetName, openingBalanceVal, minGoalVal, maxGoalVal, notes)
                    Toast.makeText(this@EditBudgetActivity, "Budget updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new budget
                    editBudgetViewModel.saveBudget(budgetName, openingBalanceVal, minGoalVal, maxGoalVal, notes)
                    Toast.makeText(this@EditBudgetActivity, "Budget created successfully", Toast.LENGTH_SHORT).show()
                }

                // Return data
                val resultIntent = Intent().apply {
                    putExtra("budgetName", budgetName)
                    putExtra("openingBalance", openingBalance)
                    putExtra("minGoal", minGoal)
                    putExtra("maxGoal", maxGoal)
                    putExtra("notes", notes)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditBudgetActivity, "Error saving budget: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBudgetActivity", "Error saving budget", e)
            }
        }
    }

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
}