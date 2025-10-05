package com.example.spendsprout_opsc.accounts

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.model.toAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class AccountsViewModel {
    
    fun getAllAccounts(): List<Account> {
        // For now, return empty list - will be populated by database queries
        return emptyList()
    }
    
    // New method to load accounts from database
    fun loadAccountsFromDatabase(callback: (List<Account>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entities = BudgetApp.db.accountDao().getAll().first()
                val accountList = entities.map { it.toAccount() }
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
}

