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
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val transactionsViewModel: TransactionsViewModel by viewModels()
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
            intent.putExtra("transactionId", transaction.transactionId)
            startActivity(intent)
        }
        recyclerView.adapter = transactionAdapter
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

            // Trigger data observation to filter transactions
            observeData()
        }
    }

    private fun updateDateRangeDisplay() {
        if (startDate != null && endDate != null) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            txtDateRange.text = "${dateFormat.format(Date(startDate!!))} - ${dateFormat.format(Date(endDate!!))}"
        } else {
            txtDateRange.text = "All Time"
        }
    }

    private fun setupFab() {
        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
        fabAddTransaction.setOnClickListener {
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            transactionsViewModel.getAllTransactions().collectLatest { transactions ->
                val filteredList = if (startDate != null && endDate != null) {
                    transactions.filter { it.date in startDate!!..endDate!! }
                } else {
                    transactions
                }
                transactionAdapter.updateData(filteredList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.transactions_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        observeData()
        Toast.makeText(this, "Date filter cleared", Toast.LENGTH_SHORT).show()
    }

    private fun applyFilter(filter: String) {
        // Implement filtering logic here based on the selected string
        Toast.makeText(this, "Filter not implemented yet", Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> startActivity(Intent(this, OverviewActivity::class.java))
            R.id.nav_categories -> startActivity(Intent(this, CategoriesActivity::class.java))
            R.id.nav_transactions -> { /* Already here */ }
            R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
            R.id.nav_reports -> Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show()
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_exit -> finishAffinity()
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
