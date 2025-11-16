package com.example.spendsprout_opsc.transactions

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendsprout_opsc.databinding.ActivityTransactionsBinding
import com.example.spendsprout_opsc.edit.EditTransactionActivity
import com.example.spendsprout_opsc.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity() {

    private val transactionsViewModel: TransactionsViewModel by viewModels()
    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            val intent = Intent(this, EditTransactionActivity::class.java)
            intent.putExtra(EditTransactionActivity.EXTRA_TRANSACTION_ID, transaction.id)
            startActivity(intent)
        }
        binding.recyclerViewTransactions.adapter = transactionAdapter
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            transactionsViewModel.getAllTransactions().collectLatest { transactions ->
                transactionAdapter.updateData(transactions)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transactions_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                // Handle filter action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
