package com.example.spendsprout_opsc.transactions

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.transactions.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TransactionsViewModel {
    
    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    
    fun getAllTransactions(): List<Transaction> {
        // For now, return empty list - will be populated by database queries
        return emptyList()
    }
    
    fun getFilteredTransactions(filter: String): List<Transaction> {
        // For now, return empty list - will be populated by database queries
        return emptyList()
    }
    
    // New method to load transactions from database
    fun loadTransactionsFromDatabase(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        callback: (List<Transaction>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = BudgetApp.db.expenseDao().getBetweenDates(startDate, endDate)
                val transactions = expenses.map { expense ->
                    Transaction(
                        id = expense.id.toString(),
                        date = dateFormat.format(Date(expense.expenseDate)),
                        description = expense.expenseName,
                        amount = formatAmount(expense.expenseAmount, expense.expenseType),
                        color = getCategoryColor(expense.expenseCategory),
                        imagePath = expense.expenseImage
                    )
                }
                CoroutineScope(Dispatchers.Main).launch {
                    callback(transactions)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }

    // Load all transactions regardless of date (useful if user dates vary)
    fun loadAllTransactionsFromDatabase(callback: (List<Transaction>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = BudgetApp.db.expenseDao().getAll()
                val transactions = expenses.sortedByDescending { it.expenseDate }.map { expense ->
                    Transaction(
                        id = expense.id.toString(),
                        date = dateFormat.format(Date(expense.expenseDate)),
                        description = expense.expenseName,
                        amount = formatAmount(expense.expenseAmount, expense.expenseType),
                        color = getCategoryColor(expense.expenseCategory)
                    )
                }
                CoroutineScope(Dispatchers.Main).launch { callback(transactions) }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(emptyList()) }
            }
        }
    }
    
    private fun formatAmount(amount: Double, expenseType: com.example.spendsprout_opsc.ExpenseType): String {
        val formattedAmount = "R ${String.format("%.2f", amount)}"
        return if (expenseType == com.example.spendsprout_opsc.ExpenseType.Expense) {
            "- $formattedAmount"
        } else {
            "+ $formattedAmount"
        }
    }
    
    private fun getCategoryColor(category: String): String {
        return when (category.lowercase()) {
            "groceries" -> "#87CEEB"
            "needs" -> "#4169E1"
            "wants" -> "#9370DB"
            "savings" -> "#32CD32"
            else -> "#D3D3D3"
        }
    }
    
    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

