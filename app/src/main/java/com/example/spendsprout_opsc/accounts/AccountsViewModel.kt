package com.example.spendsprout_opsc.accounts

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.accounts.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AccountsViewModel {
    
    fun getAllAccounts(): List<Account> {
        // For now, return empty list - will be populated by database queries
        return emptyList()
    }
    
    // New method to load accounts from database
    fun loadAccountsFromDatabase(callback: (List<Account>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accounts = BudgetApp.db.accountDao().getAll()
                val accountList = accounts.map { account ->
                    Account(
                        id = account.id.toString(),
                        name = account.accountName,
                        balance = formatAmount(account.accountBalance),
                        limit = "R 0.00", // No limit field in Account_Entity, using default
                        recentTransactions = getRecentTransactionsForAccount(account.id.toLong())
                    )
                }
                CoroutineScope(Dispatchers.Main).launch {
                    callback(accountList)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }
    
    private fun formatAmount(amount: Double): String {
        return "R ${String.format("%.2f", amount)}"
    }
    
    private fun getRecentTransactionsForAccount(accountId: Long): List<Transaction> {
        // TODO: Get recent transactions for this account from database
        // For now, return empty list
        return emptyList()
    }
}

