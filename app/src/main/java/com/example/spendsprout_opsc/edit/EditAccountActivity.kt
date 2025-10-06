package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class EditAccountActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private lateinit var editAccountViewModel: EditAccountViewModel
    private var existingAccount: com.example.spendsprout_opsc.roomdb.Account_Entity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
        
        // Get account data from intent if editing existing account
        existingAccount = intent.getSerializableExtra("account") as? com.example.spendsprout_opsc.roomdb.Account_Entity
        
        // If no account object but we have an accountId, we'll load it later
        val accountId = intent.getIntExtra("accountId", -1)
        val isEdit = intent.getBooleanExtra("isEdit", false)

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

        // Initialize ViewModel
        editAccountViewModel = EditAccountViewModel()

        setupUI()

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveAccount)
            .setOnClickListener { saveAccount() }
    }

    private fun setupUI() {
        setupAccountTypeSpinner()
        loadAccountIfNeeded()
        setupButtons()
    }
    
    private fun loadAccountIfNeeded() {
        val accountId = intent.getIntExtra("accountId", -1)
        val isEdit = intent.getBooleanExtra("isEdit", false)
        
        if (existingAccount == null && isEdit && accountId != -1) {
            // Load account from database
            lifecycleScope.launch {
                try {
                    existingAccount = com.example.spendsprout_opsc.BudgetApp.db.accountDao().getById(accountId)
                    populateFields()
                } catch (e: Exception) {
                    Log.e("EditAccountActivity", "Error loading account: ${e.message}", e)
                    Toast.makeText(this@EditAccountActivity, "Error loading account data", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Either we have the account object or we're creating new
            populateFields()
        }
    }

    private fun setupAccountTypeSpinner() {
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val accountTypes = arrayOf("Cash", "Bank", "Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountType.adapter = adapter
    }
    
    private fun populateFields() {
        if (existingAccount != null) {
            // Populate fields with existing account data
            findViewById<EditText>(R.id.edt_AccountName).setText(existingAccount!!.accountName)
            findViewById<EditText>(R.id.edt_Balance).setText(String.format("%.2f", existingAccount!!.accountBalance))
            existingAccount!!.accountNotes?.let { notes ->
                findViewById<EditText>(R.id.edt_Notes).setText(notes)
            }
            
            // Set spinner selection based on account type
            val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
            val accountTypeString = mapAccountTypeToString(existingAccount!!.accountType)
            val adapter = spinnerAccountType.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(accountTypeString)
            if (position >= 0) {
                spinnerAccountType.setSelection(position)
            }
        }
    }
    
    private fun mapAccountTypeToString(accountType: com.example.spendsprout_opsc.AccountType): String {
        return when (accountType) {
            com.example.spendsprout_opsc.AccountType.Cash -> "Cash"
            com.example.spendsprout_opsc.AccountType.Debit -> "Bank"
            com.example.spendsprout_opsc.AccountType.Credit -> "Card"
        }
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveAccount()
        }
    }

    private fun saveAccount() {
        val edtAccountName = findViewById<EditText>(R.id.edt_AccountName)
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val edtBalance = findViewById<EditText>(R.id.edt_Balance)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val accountName = edtAccountName.text.toString()
        val accountType = spinnerAccountType.selectedItem.toString()
        val balance = edtBalance.text.toString()
        val notes = edtNotes.text.toString()

        if (accountName.isEmpty() || balance.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val balanceVal = balance.toDoubleOrNull()
        if (balanceVal == null) {
            Toast.makeText(this, "Balance must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        // Save account using ViewModel with coroutine to wait for completion
        lifecycleScope.launch {
            try {
                if (existingAccount != null) {
                    // Update existing account
                    editAccountViewModel.updateAccount(existingAccount!!.id, accountName, accountType, balanceVal, notes)
                    Toast.makeText(this@EditAccountActivity, "Account updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new account
                    editAccountViewModel.saveAccount(accountName, accountType, balanceVal, notes)
                    Toast.makeText(this@EditAccountActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                }

                // Return data
                val resultIntent = Intent().apply {
                    putExtra("accountName", accountName)
                    putExtra("accountType", accountType)
                    putExtra("balance", balance)
                    putExtra("notes", notes)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditAccountActivity, "Error saving account: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditAccountActivity", "Error saving account", e)
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