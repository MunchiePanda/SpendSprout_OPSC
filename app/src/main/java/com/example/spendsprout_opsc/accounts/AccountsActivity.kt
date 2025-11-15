package com.example.spendsprout_opsc.accounts

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendsprout_opsc.databinding.ActivityAccountsBinding
import com.example.spendsprout_opsc.edit.EditAccountActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountsBinding
    private val viewModel: AccountsViewModel by viewModels()
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeAccounts()

        binding.fabAddAccount.setOnClickListener {
            val intent = Intent(this, EditAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter(emptyList()) { account ->
            // When an account is clicked, open the edit screen with its ID
            val intent = Intent(this, EditAccountActivity::class.java).apply {
                putExtra(EditAccountActivity.EXTRA_ACCOUNT_ID, account.accountId)
            }
            startActivity(intent)
        }
        binding.recyclerViewAccounts.apply {
            layoutManager = LinearLayoutManager(this@AccountsActivity)
            adapter = accountAdapter
        }
    }

    private fun observeAccounts() {
        // This will automatically update the UI whenever the data changes in Firebase
        lifecycleScope.launch {
            viewModel.accounts.collect { accounts ->
                accountAdapter.updateData(accounts)
            }
        }
    }
}
