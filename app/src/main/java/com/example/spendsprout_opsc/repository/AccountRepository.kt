package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun getAccount(accountId: String): Account?
    suspend fun addAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(accountId: String)
}
