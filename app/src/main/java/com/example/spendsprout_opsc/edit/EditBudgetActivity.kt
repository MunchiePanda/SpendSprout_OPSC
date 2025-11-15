package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Budget
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditBudgetActivity : AppCompatActivity() {
    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private val editBudgetViewModel: EditBudgetViewModel by viewModels()
    private var existingBudget: Budget? = null
    private var budgetId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        budgetId = intent.getStringExtra("budgetId")

        //MENU DRAWER SETUP
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val headerView = navigationView.getHeaderView(0)
        btnCloseMenu = headerView.findViewById(R.id.btn_CloseMenu)
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> {
                    startActivity(Intent(this, com.example.spendsprout_opsc.overview.OverviewActivity::class.java))
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
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_exit -> {
                    finishAffinity()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        setupUI()

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveBudget)
            .setOnClickListener { saveBudget() }
    }

    private fun setupUI() {
        loadBudgetIfNeeded()
        setupButtons()
    }

    private fun loadBudgetIfNeeded() {
        if (budgetId != null) {
            editBudgetViewModel.loadBudget(budgetId!!) { budget ->
                existingBudget = budget
                populateFields()
            }
        } else {
            populateFields()
        }
    }

    private fun populateFields() {
        existingBudget?.let {
            findViewById<EditText>(R.id.edt_BudgetName).setText(it.budgetName)
            findViewById<EditText>(R.id.edt_OpeningBalance).setText(String.format("%.2f", it.openingBalance))
            findViewById<EditText>(R.id.edt_MinGoal).setText(String.format("%.2f", it.budgetMinGoal))
            findViewById<EditText>(R.id.edt_MaxGoal).setText(String.format("%.2f", it.budgetMaxGoal))
            it.budgetNotes?.let { notes ->
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
        val budgetName = findViewById<EditText>(R.id.edt_BudgetName).text.toString()
        val openingBalance = findViewById<EditText>(R.id.edt_OpeningBalance).text.toString()
        val minGoal = findViewById<EditText>(R.id.edt_MinGoal).text.toString()
        val maxGoal = findViewById<EditText>(R.id.edt_MaxGoal).text.toString()
        val notes = findViewById<EditText>(R.id.edt_Notes).text.toString()

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

        if (minGoalVal >= maxGoalVal) {
            Toast.makeText(this, "Minimum goal must be less than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

        if (minGoalVal > openingBalanceVal || maxGoalVal > openingBalanceVal) {
            Toast.makeText(this, "Goals cannot exceed opening balance", Toast.LENGTH_SHORT).show()
            return
        }

        if (existingBudget != null) {
            editBudgetViewModel.updateBudget(budgetId!!, budgetName, openingBalanceVal, minGoalVal, maxGoalVal, notes)
            Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            editBudgetViewModel.saveBudget(budgetName, openingBalanceVal, minGoalVal, maxGoalVal, notes)
            Toast.makeText(this, "Budget created successfully", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
