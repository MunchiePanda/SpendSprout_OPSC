package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransaction(transactionId: String): Transaction?
    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
}
