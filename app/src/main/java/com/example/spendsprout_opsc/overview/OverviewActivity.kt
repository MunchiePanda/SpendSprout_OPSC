
package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.edit.EditBudgetActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class OverviewActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var overviewViewModel: OverviewViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        setupDrawer()
        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val headerBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(headerBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val btnMenu = findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navigationView.getHeaderView(0)
        val btnCloseMenu = headerView.findViewById<ImageButton>(R.id.btn_CloseMenu)
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        txtUsername.text = sharedPreferences.getString("username", "User")

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_overview -> { /* Already here */ }
                R.id.nav_categories -> startActivity(Intent(this, com.example.spendsprout_opsc.CategoryOverviewActivity::class.java))
                R.id.nav_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
                R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
                R.id.nav_reports -> Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_logout -> logout()
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupViewModel() {
        overviewViewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
    }

    private fun setupUI() {
        val transactionsRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionsRecyclerView.adapter = TransactionAdapter(emptyList())

        val categoriesRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = CategorySummaryAdapter(emptyList())

        val accountsRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Accounts)
        accountsRecyclerView.layoutManager = LinearLayoutManager(this)
        accountsRecyclerView.adapter = AccountSummaryAdapter(emptyList())

        findViewById<ImageButton>(R.id.btn_editBudget).setOnClickListener {
            startActivity(Intent(this, EditBudgetActivity::class.java))
        }
    }

    private fun observeViewModel() {
        overviewViewModel.loadData()

        overviewViewModel.totalBalance.observe(this) { balance ->
            findViewById<TextView>(R.id.txt_TotalBalance).text = String.format("R %.2f", balance)
        }

        overviewViewModel.recentTransactions.observe(this) {
            val adapter = findViewById<RecyclerView>(R.id.recyclerView_Transactions).adapter as TransactionAdapter
            adapter.submitList(it)
        }

        overviewViewModel.categorySummary.observe(this) {
            val adapter = findViewById<RecyclerView>(R.id.recyclerView_Categories).adapter as CategorySummaryAdapter
            adapter.submitList(it)
        }

        overviewViewModel.accountSummary.observe(this) {
            val adapter = findViewById<RecyclerView>(R.id.recyclerView_Accounts).adapter as AccountSummaryAdapter
            adapter.submitList(it)
        }
    }

    private fun logout() {
        sharedPreferences.edit().putBoolean("is_logged_in", false).remove("username").apply()
        val intent = Intent(this, com.example.spendsprout_opsc.login.LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
