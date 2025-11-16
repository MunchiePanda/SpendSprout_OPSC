package com.example.spendsprout_opsc.overview

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.databinding.ActivityOverviewBinding
import com.example.spendsprout_opsc.model.SpendingType
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OverviewActivity : AppCompatActivity() {

    private val viewModel: OverviewViewModel by viewModels()
    private lateinit var binding: ActivityOverviewBinding
    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        setupNavigation()
        setupUI()
        observeViewModel()
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.headerBar.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.headerBar.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = binding.navigationView.getHeaderView(0)
        val btnCloseMenu = headerView.findViewById<ImageButton>(R.id.btn_CloseMenu)
        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        val currentUsername = sharedPreferences.getString("username", "User")
        txtUsername.text = currentUsername

        btnCloseMenu.setOnClickListener {
            binding.drawerLayout.closeDrawer(binding.navigationView)
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_overview -> { /* Already here */ }
                R.id.nav_categories -> startActivity(Intent(this, CategoriesActivity::class.java))
                R.id.nav_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
                R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
                R.id.nav_reports -> startActivity(Intent(this, com.example.spendsprout_opsc.reports.ReportsActivity::class.java))
                R.id.nav_settings -> Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> logout()
                R.id.nav_exit -> finishAffinity()
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupUI() {
        // Setup RecyclerView for transactions
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter { /* TODO: Handle transaction click */ }
        binding.recyclerViewTransactions.adapter = transactionAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                // Update Total Balance, Income, and Expenses
                binding.totalBalanceTextview.text = String.format("R %.2f", uiState.totalBalance)
                binding.incomeTextview.text = String.format("R %.2f", uiState.totalIncome)
                binding.expensesTextview.text = String.format("R %.2f", uiState.totalExpenses)

                // Update Transactions
                transactionAdapter.submitList(uiState.transactions)

                // Update Pie Chart
                updatePieChart(uiState.spendingByType)
            }
        }
    }

    private fun updatePieChart(spendingByType: Map<SpendingType, Double>) {
        val entries = spendingByType.map { (spendingType, amount) ->
            PieEntry(amount.toFloat(), spendingType.name)
        }

        val dataSet = PieDataSet(entries, "Spending by Type")
        dataSet.colors = listOf(
            Color.parseColor("#FF6384"), // Needs - Red
            Color.parseColor("#36A2EB"), // Wants - Blue
            Color.parseColor("#FFCE56")  // Savings - Yellow
        )

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // refresh
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
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
