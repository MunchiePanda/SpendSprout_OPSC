package com.example.spendsprout_opsc.accounts

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class AccountsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var accountsViewModel: AccountsViewModel
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Accounts"
        
        // Set up menu button click listener
        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        accountsViewModel = AccountsViewModel()

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupAccountRecyclerView()
        setupFab()
    }

    private fun setupAccountRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Accounts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize with empty list, will be populated from database
        accountAdapter = AccountAdapter(emptyList()) { account ->
            // Handle account click - open edit screen
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditAccountActivity::class.java)
            // Convert UI model to database entity
            val accountEntity = com.example.spendsprout_opsc.roomdb.Account_Entity(
                id = account.id,
                accountName = account.name,
                accountType = account.type,
                accountBalance = account.balance,
                accountNotes = account.notes
            )
            intent.putExtra("account", accountEntity)
            startActivity(intent)
        }
        recyclerView.adapter = accountAdapter

        // Load accounts from database
        loadAccountsFromDatabase()
    }

    private fun setupFab() {
        val fabAddAccount = findViewById<FloatingActionButton>(R.id.fab_AddAccount)
        fabAddAccount.setOnClickListener {
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        // Observe ViewModel data changes
    }
    
    private fun loadAccountsFromDatabase() {
        accountsViewModel.loadAccountsFromDatabase { accounts ->
            accountAdapter.updateData(accounts)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reload accounts when returning to this activity
        loadAccountsFromDatabase()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                startActivity(Intent(this, com.example.spendsprout_opsc.CategoryOverviewActivity::class.java))
            }
            R.id.nav_transactions -> {
                startActivity(Intent(this, TransactionsActivity::class.java))
            }
            R.id.nav_accounts -> {
                // Already in Accounts, do nothing
            }
            R.id.nav_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
            }
            R.id.nav_sprout -> {
                startActivity(Intent(this, com.example.spendsprout_opsc.sprout.SproutActivity::class.java))
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

