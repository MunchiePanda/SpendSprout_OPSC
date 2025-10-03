package com.example.spendsprout_opsc.transactions

import com.example.spendsprout_opsc.transactions.model.Transaction

class TransactionsViewModel {
    
    fun getAllTransactions(): List<Transaction> {
        return listOf(
            Transaction("1", "10 August 2025", "Petrol", "- R 1,500", "#87CEEB"),
            Transaction("2", "08 August 2025", "PnP Groceries", "- R 360", "#4169E1"),
            Transaction("3", "25 July 2025", "Salary", "+ R 20,000", "#32CD32"),
            Transaction("4", "DD Month YYYY", "Description", "+ R 300,000", "#D3D3D3"),
            Transaction("5", "DD Month YYYY", "Description", "- R 300,000", "#9370DB"),
            Transaction("6", "DD Month YYYY", "Description", "- R 10", "#FFA500"),
            Transaction("7", "DD Month YYYY", "Description", "- R 300,000", "#FFB6C1"),
            Transaction("8", "DD Month YYYY", "Description", "- R 100", "#87CEEB")
        )
    }
    
    fun getFilteredTransactions(filter: String): List<Transaction> {
        val allTransactions = getAllTransactions()
        return when (filter) {
            "Income" -> allTransactions.filter { it.amount.startsWith("+") }
            "Expenses" -> allTransactions.filter { it.amount.startsWith("-") }
            "This Month" -> allTransactions.filter { it.date.contains("August 2025") }
            "Last Month" -> allTransactions.filter { it.date.contains("July 2025") }
            else -> allTransactions
        }
    }
}

