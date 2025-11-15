package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.edit.EditBudgetActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionAdapter
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OverviewActivity : AppCompatActivity() {

    private val viewModel: OverviewViewModel by viewModels()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnCloseMenu: ImageButton
    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        setupNavigation()
        setupUI()
        observeViewModel()
    }

    private fun setupNavigation() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

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

        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        val currentUsername = sharedPreferences.getString("username", "User")
        txtUsername.text = currentUsername

        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_overview -> { /* Already here */ }
                R.id.nav_categories -> startActivity(Intent(this, CategoriesActivity::class.java))
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

    private fun setupUI() {
        // Setup RecyclerView for transactions
        val transactionsRecyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList())
        transactionsRecyclerView.adapter = transactionAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                // Update Total Balance, Income, and Expenses
                findViewById<TextView>(R.id.total_balance_textview).text = String.format("R %.2f", uiState.totalBalance)
                findViewById<TextView>(R.id.income_textview).text = String.format("R %.2f", uiState.totalIncome)
                findViewById<TextView>(R.id.expenses_textview).text = String.format("R %.2f", uiState.totalExpenses)

                // Update Transactions
                transactionAdapter.updateData(uiState.transactions)

                // Update Pie Chart
                updatePieChart(uiState.categorySpends)
            }
        }
    }

    private fun updatePieChart(categorySpends: Map<String, Double>) {
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val entries = categorySpends.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Category Spends")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate() // refresh
    }

    private fun logout() {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", false)
            .remove("username")
            .apply()

        val intent = Intent(this, com.example.spendsprout_opsc.login.LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
