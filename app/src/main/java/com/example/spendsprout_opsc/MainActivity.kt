package com.example.spendsprout_opsc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView for transactions
        val recyclerViewTransactions = findViewById<RecyclerView>(R.id.recyclerViewTransactions)
        recyclerViewTransactions.layoutManager = LinearLayoutManager(this)

        // Sample transaction data
        val transactions = listOf(
            TransactionAdapter.Transaction("12 December 2025", "Groceries", -100.0, "Needs"),
            TransactionAdapter.Transaction("11 December 2025", "Salary", 5000.0, "Savings"),
            TransactionAdapter.Transaction("10 December 2025", "Entertainment", -50.0, "Wants"),
            TransactionAdapter.Transaction("9 December 2025", "Bonus", 1000.0, "Savings"),
            TransactionAdapter.Transaction("8 December 2025", "Rent", -1200.0, "Needs")
        )

        // Set up the adapter for transactions
        val transactionAdapter = TransactionAdapter(transactions)
        recyclerViewTransactions.adapter = transactionAdapter
    }
}