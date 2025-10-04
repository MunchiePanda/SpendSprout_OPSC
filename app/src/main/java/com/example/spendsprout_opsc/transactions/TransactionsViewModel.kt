package com.example.spendsprout_opsc.transactions

import com.example.spendsprout_opsc.transactions.model.Transaction
import com.example.spendsprout_opsc.TransactionType
import com.example.spendsprout_opsc.RepeatType

class TransactionsViewModel {
    
    fun getAllTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                id = 1,
                subcategoryId = 1,
                accountId = 2,
                contactId = null,
                name = "Petrol",
                date = System.currentTimeMillis() - 86400000 * 2,
                amount = 1500.0,
                type = TransactionType.Expense,
                isOwed = false,
                repeat = RepeatType.None,
                notes = "Fuel for car",
                image = null
            ),
            Transaction(
                id = 2,
                subcategoryId = 3,
                accountId = 2,
                contactId = null,
                name = "Mug 'n Bean",
                date = System.currentTimeMillis() - 86400000 * 4,
                amount = 360.0,
                type = TransactionType.Expense,
                isOwed = false,
                repeat = RepeatType.None,
                notes = "Lunch with friends",
                image = null
            ),
            Transaction(
                id = 3,
                subcategoryId = 1,
                accountId = 2,
                contactId = null,
                name = "Salary",
                date = System.currentTimeMillis() - 86400000 * 10,
                amount = 20000.0,
                type = TransactionType.Income,
                isOwed = false,
                repeat = RepeatType.None,
                notes = "Monthly salary",
                image = null
            )
        )
    }
    
    fun getFilteredTransactions(filter: String): List<Transaction> {
        val allTransactions = getAllTransactions()
        return when (filter) {
            "Income" -> allTransactions.filter { it.type == TransactionType.Income }
            "Expenses" -> allTransactions.filter { it.type == TransactionType.Expense }
            "This Month" -> allTransactions.filter { 
                val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
                val transactionMonth = java.util.Calendar.getInstance().apply { 
                    timeInMillis = it.date 
                }.get(java.util.Calendar.MONTH)
                currentMonth == transactionMonth
            }
            "Last Month" -> allTransactions.filter { 
                val lastMonth = java.util.Calendar.getInstance().apply { 
                    add(java.util.Calendar.MONTH, -1) 
                }.get(java.util.Calendar.MONTH)
                val transactionMonth = java.util.Calendar.getInstance().apply { 
                    timeInMillis = it.date 
                }.get(java.util.Calendar.MONTH)
                lastMonth == transactionMonth
            }
            else -> allTransactions
        }
    }
}

