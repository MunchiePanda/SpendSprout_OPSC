package com.example.spendsprout_opsc.transactions

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
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
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.edit.EditTransactionActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class TransactionsActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var transactionsViewModel: TransactionsViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        setupUI()
        setupViewModel()
        setupListeners()
    }

    private fun setupUI() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val headerBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.header_bar)
        setSupportActionBar(headerBar)

        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val rvTransactions = findViewById<RecyclerView>(R.id.recyclerView_Transactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            val intent = Intent(this, EditTransactionActivity::class.java)
            intent.putExtra("transactionId", transaction.id)
            startActivity(intent)
        }
        rvTransactions.adapter = transactionAdapter
    }

    private fun setupViewModel() {
        transactionsViewModel = ViewModelProvider(this).get(TransactionsViewModel::class.java)
        transactionsViewModel.transactions.observe(this) { transactions ->
            transactionAdapter.updateData(transactions)
        }
        transactionsViewModel.loadAllTransactions()
    }

    private fun setupListeners() {
        val btnDateFilter = findViewById<Button>(R.id.btn_selectDateRange)
        btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }

        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
        fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, EditTransactionActivity::class.java))
        }
    }

    private fun showDateRangePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = Date(selection.first)
            val endDate = Date(selection.second)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateRangeString = "${sdf.format(startDate)} - ${sdf.format(endDate)}"
            findViewById<TextView>(R.id.txt_dateRange).text = dateRangeString
            transactionsViewModel.loadTransactionsByDateRange(startDate, endDate)
        }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
