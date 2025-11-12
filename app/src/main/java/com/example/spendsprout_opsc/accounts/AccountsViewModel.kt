package com.example.spendsprout_opsc.accounts

import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.firebase.FirebaseRepositoryProvider
import com.example.spendsprout_opsc.model.toAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountsViewModel {

    private val accountRepository = FirebaseRepositoryProvider.accountRepository

    fun getAllAccounts(): List<Account> = emptyList()

    fun loadAccountsFromDatabase(callback: (List<Account>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entities = accountRepository.getAllAccounts().first()
                val accountList = entities.map { it.toAccount() }
                withContext(Dispatchers.Main) {
                    callback(accountList)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            }
        }
    }
}
