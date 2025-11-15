package com.example.spendsprout_opsc.transactions

import androidx.lifecycle.ViewModel
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactions()
    }
}
