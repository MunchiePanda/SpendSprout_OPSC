
package com.SBMH.SpendSprout.transactions

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
import com.SBMH.SpendSprout.edit.EditTransactionActivity
import com.SBMH.SpendSprout.overview.OverviewActivity
import com.SBMH.SpendSprout.reports.ReportsActivity
import com.SBMH.SpendSprout.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class TransactionsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        setupDrawer()

        viewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)

        setupRecyclerView()
        setupFab()

        observeViewModel()
        viewModel.loadTransactions()
    }

    private fun setupDrawer() {
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Transactions"

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
                R.id.nav_overview -> startActivity(Intent(this, OverviewActivity::class.java))
                R.id.nav_categories -> startActivity(Intent(this, CategoriesActivity::class.java))
                R.id.nav_transactions -> { /* already here */ }
                R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
                R.id.nav_reports -> startActivity(Intent(this, ReportsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList()) { expense ->
            val intent = Intent(this, EditTransactionActivity::class.java)
            intent.putExtra("transactionId", expense.id)
            startActivity(intent)
        }
        recyclerView.adapter = transactionAdapter
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
        fab.setOnClickListener {
            val intent = Intent(this, EditTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(this) { transactions ->
            transactionAdapter.updateData(transactions)
        }
    }
}
