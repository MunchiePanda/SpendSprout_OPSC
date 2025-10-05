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

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the drawer indicator in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Accounts"

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
            intent.putExtra("accountId", account.id)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

