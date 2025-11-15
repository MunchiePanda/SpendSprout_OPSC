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
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.model.Account
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    companion object {
        const val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"
    }

    private val editAccountViewModel: EditAccountViewModel by viewModels()
    private var existingAccount: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        val accountId = intent.getStringExtra(EXTRA_ACCOUNT_ID)

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
        loadAccountIfNeeded(accountId)

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveAccount)
            .setOnClickListener { saveAccount() }
    }

    private fun setupUI() {
        setupAccountTypeSpinner()
        setupButtons()
    }

    private fun loadAccountIfNeeded(accountId: String?) {
        if (accountId != null) {
            editAccountViewModel.getAccount(accountId) { account ->
                if (account != null) {
                    existingAccount = account
                    populateFields()
                } else {
                    Toast.makeText(this, "Error loading account data", Toast.LENGTH_SHORT).show()
                }
            }
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
        existingAccount?.let {
            findViewById<EditText>(R.id.edt_AccountName).setText(it.accountName)
            findViewById<EditText>(R.id.edt_Balance).setText(String.format("%.2f", it.accountBalance))

            val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
            val adapter = spinnerAccountType.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(it.accountType)
            if (position >= 0) {
                spinnerAccountType.setSelection(position)
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
            saveAccount()
        }
    }

    private fun saveAccount() {
        val edtAccountName = findViewById<EditText>(R.id.edt_AccountName)
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val edtBalance = findViewById<EditText>(R.id.edt_Balance)

        val accountName = edtAccountName.text.toString()
        val accountType = spinnerAccountType.selectedItem.toString()
        val balance = edtBalance.text.toString()

        if (accountName.isEmpty() || balance.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val balanceVal = balance.toDoubleOrNull()
        if (balanceVal == null) {
            Toast.makeText(this, "Balance must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        if (existingAccount != null) {
            val updatedAccount = existingAccount!!.copy(
                accountName = accountName,
                accountType = accountType,
                accountBalance = balanceVal
            )
            editAccountViewModel.updateAccount(updatedAccount)
            Toast.makeText(this@EditAccountActivity, "Account updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            editAccountViewModel.saveAccount(accountName, balanceVal, accountType)
            Toast.makeText(this@EditAccountActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
        }

        val resultIntent = Intent().apply {
            putExtra("accountName", accountName)
            putExtra("accountType", accountType)
            putExtra("balance", balance)
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
