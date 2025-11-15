package com.example.spendsprout_opsc.transactions

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.SBMH.SpendSprout.model.Expense
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class TransactionsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var transactionsViewModel: TransactionsViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    // Date range picker components
    private lateinit var btnSelectDateRange: MaterialButton
    private lateinit var txtDateRange: TextView
    private var startDate: Long? = null
    private var endDate: Long? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar from the included header bar
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Transactions"

        // Set up menu button click listener
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)

        // Initialize SharedPreferences for login management
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        // Set the username in the navigation header
        val headerView = navView.getHeaderView(0)
        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        val currentUsername = sharedPreferences.getString("username", "User")
        txtUsername.text = currentUsername

        // Initialize ViewModel
        transactionsViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupDateRangePicker()
        setupTransactionRecyclerView()
        setupFab()
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize with empty list, will be populated from database
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            // Handle transaction click - open edit screen
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditTransactionActivity::class.java)
            intent.putExtra("transactionId", transaction.id)
            startActivity(intent)
        }
        recyclerView.adapter = transactionAdapter

        // Load transactions from database
        loadTransactionsFromDatabase()
    }

    private fun setupDateRangePicker() {
        btnSelectDateRange = findViewById(R.id.btn_selectDateRange)
        txtDateRange = findViewById(R.id.txt_dateRange)

        // Set default to show all transactions
        updateDateRangeDisplay()

        btnSelectDateRange.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        // Create date range picker
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .setSelection(
                androidx.core.util.Pair(
                    startDate ?: MaterialDatePicker.todayInUtcMilliseconds(),
                    endDate ?: MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        // Show the picker
        dateRangePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        // Handle the selection
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            startDate = selection.first
            endDate = selection.second

            // Update display
            updateDateRangeDisplay()

            // Filter transactions based on date range
            filterTransactionsByDateRange()
        }
    }

    private fun updateDateRangeDisplay() {
        if (startDate != null && endDate != null) {
            val dateFormat = SimpleDateFormat("MMM dd - MMM dd, yyyy", Locale.getDefault())
            val startDateStr = dateFormat.format(Date(startDate!!))
            val endDateStr = dateFormat.format(Date(endDate!!))

            // Calculate days difference for a more user-friendly display
            val daysDifference = ((endDate!! - startDate!!) / (1000 * 60 * 60 * 24)).toInt()

            when {
                daysDifference == 0 -> txtDateRange.text = "Today"
                daysDifference == 1 -> txtDateRange.text = "Yesterday"
                daysDifference < 7 -> txtDateRange.text = "Last $daysDifference days"
                daysDifference < 30 -> txtDateRange.text = "Last ${daysDifference / 7} weeks"
                daysDifference < 365 -> txtDateRange.text = "Last ${daysDifference / 30} months"
                else -> txtDateRange.text = "Last ${daysDifference / 365} years"
            }
        } else {
            txtDateRange.text = "All Time"
        }
    }

    private fun filterTransactionsByDateRange() {
        // Reload transactions with the new date range
        loadTransactionsFromDatabase()

        // Log the selected dates for debugging
        if (startDate != null && endDate != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateStr = dateFormat.format(Date(startDate!!))
            val endDateStr = dateFormat.format(Date(endDate!!))

            println("Filtering transactions from $startDateStr to $endDateStr")
            Toast.makeText(this, "Filtering from $startDateStr to $endDateStr", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFab() {
        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
        if (fabAddTransaction != null) {
            fabAddTransaction.setOnClickListener {
                val intent = Intent(this, com.example.spendsprout_opsc.edit.EditTransactionActivity::class.java)
                startActivity(intent)
            }
            // Make sure FAB is visible
            fabAddTransaction.visibility = android.view.View.VISIBLE
        } else {
            Toast.makeText(this, "FAB not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        transactionsViewModel.transactions.observe(this) { transactions ->
            transactionAdapter.submitList(transactions)
        }
    }

    private fun loadTransactionsFromDatabase() {
        if (startDate != null && endDate != null) {
            // Load transactions with date filtering
            transactionsViewModel.loadTransactionsByDateRange(startDate!!, endDate!!)
        } else {
            // Load all transactions when no date range is selected
            transactionsViewModel.loadAllTransactions()
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload transactions when returning to this activity
        loadTransactionsFromDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1001, 0, "Filters")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle back button - finish this activity
                finish()
                return true
            }
            1001 -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        val filters = arrayOf("All", "Income", "Expenses", "This Month", "Last Month", "Clear Date Filter")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Filter Transactions")
            .setItems(filters) { _, which ->
                val selected = filters[which]
                if (selected == "Clear Date Filter") {
                    clearDateFilter()
                } else {
                    applyFilter(selected)
                }
            }
            .show()
    }

    private fun clearDateFilter() {
        startDate = null
        endDate = null
        updateDateRangeDisplay()
        loadTransactionsFromDatabase()
        Toast.makeText(this, "Date filter cleared - showing all transactions", Toast.LENGTH_SHORT).show()
    }

    private fun applyFilter(filter: String) {
        // The filtering logic is now handled in the ViewModel
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
                // Already in Transactions, do nothing
            }
            R.id.nav_accounts -> {
                startActivity(Intent(this, AccountsActivity::class.java))
            }
            R.id.nav_reports -> {
                android.widget.Toast.makeText(this, "Reports coming soon!", android.widget.Toast.LENGTH_SHORT).show()
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
