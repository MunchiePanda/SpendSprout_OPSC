package com.example.spendsprout_opsc.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    fun saveTransaction(
        description: String,
        amount: Double,
        categoryId: String,
        date: Long,
        accountId: String
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                description = description,
                amount = amount,
                categoryId = categoryId,
                date = date,
                accountId = accountId
            )
            transactionRepository.addTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }

    fun getTransaction(transactionId: String, callback: (Transaction?) -> Unit) {
        viewModelScope.launch {
            callback(transactionRepository.getTransaction(transactionId))
        }
    }
}
