package com.example.spendsprout_opsc.edit

import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.firebase.TransactionRepository
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionViewModel {
    
    private val transactionRepository = TransactionRepository()
    
    fun saveTransaction(
        description: String,
        amount: Double,
        category: String,
        date: Long,
        repeat: String,
        oweOwed: Boolean,
        notes: String,
        imagePath: String? = null
    ) {
        // Save transaction logic - for now just validate
        require(description.isNotBlank()) { "Description is required" }
        require(amount > 0) { "Amount must be greater than 0" }
        require(category.isNotBlank()) { "Category is required" }
        
        // Save to database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entity = Expense_Entity(
                    expenseName = description,
                    expenseDate = date,
                    expenseAmount = amount,
                    expenseType = if (oweOwed) ExpenseType.Income else ExpenseType.Expense,
                    expenseIsOwed = oweOwed,
                    expenseRepeat = parseRepeatType(repeat),
                    expenseNotes = notes.ifBlank { null },
                    expenseImage = imagePath,
                    expenseCategory = category,
                    expenseStart = null, // TODO: Add time picker support
                    expenseEnd = null    // TODO: Add time picker support
                )
                transactionRepository.insertTransaction(entity)
                android.util.Log.d("EditTransactionViewModel", "Transaction saved: $description, $amount, $category")
            } catch (e: Exception) {
                android.util.Log.e("EditTransactionViewModel", "Error saving transaction: ${e.message}", e)
            }
        }
    }

    fun parseUiDateToMillis(ui: String): Long {
        return try {
            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).parse(ui)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    private fun parseRepeatType(repeat: String): RepeatType {
        return when (repeat.lowercase()) {
            "daily" -> RepeatType.Daily
            "weekly" -> RepeatType.Weekly
            "monthly" -> RepeatType.Monthly
            else -> RepeatType.None
        }
    }
    
    fun loadTransactionById(transactionId: Long, callback: (Expense_Entity?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expense = transactionRepository.getTransactionById(transactionId)
                CoroutineScope(Dispatchers.Main).launch {
                    callback(expense)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(null)
                }
            }
        }
    }
    
    fun updateTransaction(
        transactionId: Long,
        name: String,
        amount: Double,
        category: String,
        date: Long,
        repeat: String,
        oweOwed: Boolean,
        notes: String,
        imagePath: String? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expense = Expense_Entity(
                    id = transactionId,
                    expenseName = name,
                    expenseAmount = amount,
                    expenseCategory = category,
                    expenseDate = date,
                    expenseIsOwed = oweOwed,
                    expenseRepeat = parseRepeatType(repeat),
                    expenseType = if (oweOwed) ExpenseType.Income else ExpenseType.Expense,
                    expenseNotes = notes.ifBlank { null },
                    expenseImage = imagePath,
                    expenseStart = null,
                    expenseEnd = null
                )
                
                transactionRepository.updateTransaction(expense)
                android.util.Log.d("EditTransactionViewModel", "Transaction updated: $name")
            } catch (e: Exception) {
                android.util.Log.e("EditTransactionViewModel", "Error updating transaction: ${e.message}", e)
            }
        }
    }
}
