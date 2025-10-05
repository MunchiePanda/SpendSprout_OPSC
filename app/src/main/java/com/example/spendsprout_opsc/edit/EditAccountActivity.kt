package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Bundle
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
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        //MENU DRAWER SETUP
        //MenuDrawer: Drawer Layout/ Menu Code and connections
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        
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
                    Toast.makeText(this, "Navigating to Reports", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ReportsActivity::class.java))
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
        setupButtons()
    }

    private fun setupAccountTypeSpinner() {
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val accountTypes = arrayOf("Cash", "Bank", "Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountType.adapter = adapter
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

        // Save account using ViewModel
        editAccountViewModel.saveAccount(accountName, accountType, balanceVal, notes)

        // Return data
        val resultIntent = Intent().apply {
            putExtra("accountName", accountName)
            putExtra("accountType", accountType)
            putExtra("balance", balance)
            putExtra("notes", notes)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    //MenuDrawer: Drawer Layout/ Menu Code
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}