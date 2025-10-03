package com.example.spendsprout_opsc.transactions

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class TransactionsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var transactionsViewModel: TransactionsViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

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
        supportActionBar?.title = "Transactions"

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        transactionsViewModel = TransactionsViewModel()

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupTransactionRecyclerView()
        setupFab()
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val transactions = transactionsViewModel.getAllTransactions()
        transactionAdapter = TransactionAdapter(transactions) { transaction ->
            // Transaction items are not clickable - they just display information
            // Only the FAB should navigate to edit screen
        }
        recyclerView.adapter = transactionAdapter
    }

    private fun setupFab() {
        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
        fabAddTransaction.setOnClickListener {
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        // Observe ViewModel data changes
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1001, 0, "Filters")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1001) {
            showFilterDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        val filters = arrayOf("All", "Income", "Expenses", "This Month", "Last Month")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Filter Transactions")
            .setItems(filters) { _, which ->
                val selected = filters[which]
                applyFilter(selected)
            }
            .show()
    }

    private fun applyFilter(filter: String) {
        val filteredTransactions = transactionsViewModel.getFilteredTransactions(filter)
        transactionAdapter.updateData(filteredTransactions)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                startActivity(Intent(this, CategoriesActivity::class.java))
            }
            R.id.nav_transactions -> {
                // Already in Transactions, do nothing
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

