
package com.SBMH.SpendSprout.overview

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.CategoryOverviewActivity
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.accounts.AccountsActivity
import com.SBMH.SpendSprout.categories.CategoriesActivity
import com.SBMH.SpendSprout.reports.ReportsActivity
import com.SBMH.SpendSprout.settings.SettingsActivity
import com.SBMH.SpendSprout.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class OverviewActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: OverviewViewModel
    private lateinit var accountSummaryAdapter: AccountSummaryAdapter
    private lateinit var categorySummaryAdapter: CategorySummaryAdapter
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        setupDrawer()

        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)

        setupRecyclerViews()

        observeViewModel()
        viewModel.loadData()
    }

    private fun setupDrawer() {
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Overview"

        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navView.getHeaderView(0)
        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        val currentUsername = sharedPreferences.getString("username", "User")
        txtUsername.text = currentUsername

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> { /* already here */ }
                R.id.nav_categories -> startActivity(Intent(this, CategoriesActivity::class.java))
                R.id.nav_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
                R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
                R.id.nav_reports -> startActivity(Intent(this, ReportsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRecyclerViews() {
        val accountRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        accountRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        accountSummaryAdapter = AccountSummaryAdapter(emptyList()) { }
        accountRecyclerView.adapter = accountSummaryAdapter

        val categoryRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categorySummaryAdapter = CategorySummaryAdapter(emptyList()) { }
        categoryRecyclerView.adapter = categorySummaryAdapter

        val transactionRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList()) { }
        transactionRecyclerView.adapter = transactionAdapter
    }

    private fun observeViewModel() {
        viewModel.accounts.observe(this) { accounts ->
            accountSummaryAdapter.updateData(accounts)
            val totalBalance = accounts.sumOf { it.accountBalance }
            val txtTotalBalance = findViewById<TextView>(R.id.txt_BudgetBalance)
            txtTotalBalance.text = String.format("$%.2f", totalBalance)
        }

        viewModel.categories.observe(this) { categories ->
            categorySummaryAdapter.updateData(categories)
        }

        viewModel.transactions.observe(this) { transactions ->
            transactionAdapter.updateData(transactions)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
