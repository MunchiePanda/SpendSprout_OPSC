package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.model.Budget
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditBudgetActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    companion object {
        const val EXTRA_BUDGET_ID = "EXTRA_BUDGET_ID"
    }

    private val editBudgetViewModel: EditBudgetViewModel by viewModels()
    private var existingBudget: Budget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        val budgetId = intent.getStringExtra(EXTRA_BUDGET_ID)

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
                    startActivity(Intent(this, com.example.spendsprout_opsc.CategoryOverviewActivity::class.java))
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

        setupUI()
        loadBudgetIfNeeded(budgetId)

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveBudget)
            .setOnClickListener { saveBudget() }
    }

    private fun setupUI() {
        setupButtons()
    }

    private fun loadBudgetIfNeeded(budgetId: String?) {
        if (budgetId != null) {
            editBudgetViewModel.getBudget(budgetId) { budget ->
                if (budget != null) {
                    existingBudget = budget
                    populateFields()
                } else {
                    Toast.makeText(this, "Error loading budget data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateFields() {
        existingBudget?.let {
            findViewById<EditText>(R.id.edt_BudgetName).setText(it.budgetName)
            findViewById<EditText>(R.id.edt_OpeningBalance).setText(String.format("%.2f", it.budgetAmount))
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
        val edtBudgetAmount = findViewById<EditText>(R.id.edt_OpeningBalance)

        val budgetName = edtBudgetName.text.toString()
        val budgetAmount = edtBudgetAmount.text.toString()

        if (budgetName.isEmpty() || budgetAmount.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val budgetAmountVal = budgetAmount.toDoubleOrNull()
        if (budgetAmountVal == null) {
            Toast.makeText(this, "Budget amount must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        if (existingBudget != null) {
            val updatedBudget = existingBudget!!.copy(
                budgetName = budgetName,
                budgetAmount = budgetAmountVal
            )
            editBudgetViewModel.updateBudget(updatedBudget)
            Toast.makeText(this@EditBudgetActivity, "Budget updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            editBudgetViewModel.saveBudget(budgetName, budgetAmountVal)
            Toast.makeText(this@EditBudgetActivity, "Budget created successfully", Toast.LENGTH_SHORT).show()
        }

        val resultIntent = Intent().apply {
            putExtra("budgetName", budgetName)
            putExtra("budgetAmount", budgetAmount)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
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
