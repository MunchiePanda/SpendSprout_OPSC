package com.example.spendsprout_opsc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class TransactionsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<Transaction>()

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

        // Initialize with mock data
        transactions.addAll(listOf(
            Transaction("25 December 2025", "PnP Party Food", "Needs", "- R 1,200.00"),
            Transaction("24 December 2025", "DStv Subscription", "Wants", "- R 300.00"),
            Transaction("23 December 2025", "Salary", "Savings", "+ R 15,000.00")
        ))

        setupSpinners()
        setupTransactionRecyclerView()
        setupFab()
    }

    private fun setupSpinners() {
        val spinnerDateRange = findViewById<Spinner>(R.id.spinner_DateRange)
        val dateRanges = arrayOf("This Month", "Last Month", "This Year", "Custom")
        val dateRangeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateRanges)
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDateRange.adapter = dateRangeAdapter

        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
        val categories = arrayOf("All", "Needs", "Wants", "Savings")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
        val accounts = arrayOf("All", "Cash", "Bank", "Card")
        val accountAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts)
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccount.adapter = accountAdapter
    }

    private fun setupTransactionRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(transactions) { transaction ->
            // Handle transaction click
            val intent = Intent(this, EditTransactionActivity::class.java)
            startActivity(intent)
        }
        recyclerView.adapter = transactionAdapter
    }

    private fun setupFab() {
        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
        fabAddTransaction.setOnClickListener {
            startActivityForResult(Intent(this, EditTransactionActivity::class.java), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val description = data?.getStringExtra("description") ?: ""
            val amount = data?.getStringExtra("amount") ?: "R 0"
            val category = data?.getStringExtra("category") ?: "Needs"
            val date = data?.getStringExtra("date") ?: "Today"

            // Add the new transaction
            transactions.add(Transaction(date, description, category, amount))
            transactionAdapter.notifyDataSetChanged()
        }
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

    data class Transaction(val date: String, val name: String, val category: String, val amount: String)

    class TransactionAdapter(private val transactions: List<Transaction>, private val onItemClick: (Transaction) -> Unit) :
        RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

        class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val dateTextView: TextView = view.findViewById(R.id.txt_Date)
            val nameTextView: TextView = view.findViewById(R.id.txt_Name)
            val categoryTextView: TextView = view.findViewById(R.id.txt_Category)
            val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_layout, parent, false)
            return TransactionViewHolder(view)
        }

        override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
            val transaction = transactions[position]
            holder.dateTextView.text = transaction.date
            holder.nameTextView.text = transaction.name
            holder.categoryTextView.text = transaction.category
            holder.amountTextView.text = transaction.amount

            // Set color based on amount sign
            if (transaction.amount.startsWith("+")) {
                holder.amountTextView.setTextColor(holder.itemView.resources.getColor(R.color.PositiveBalanceColor))
            } else {
                holder.amountTextView.setTextColor(holder.itemView.resources.getColor(R.color.NegativeBalanceColor))
            }

            // Set click listener
            holder.view.setOnClickListener { onItemClick(transaction) }
        }

        override fun getItemCount(): Int = transactions.size
    }
}
