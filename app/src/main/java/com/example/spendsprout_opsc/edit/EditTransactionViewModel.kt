package com.example.spendsprout_opsc.edit

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionViewModel {
    
    fun saveTransaction(
        description: String,
        amount: Double,
        category: String,
        date: String,
        account: String,
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
                    expenseDate = parseUiDateToMillis(date),
                    expenseAmount = amount,
                    expenseType = if (oweOwed) ExpenseType.Expense else ExpenseType.Income,
                    expenseIsOwed = oweOwed,
                    expenseRepeat = parseRepeatType(repeat),
                    expenseNotes = notes.ifBlank { null },
                    expenseImage = imagePath,
                    expenseCategory = category,
                    expenseStart = null, // TODO: Add time picker support
                    expenseEnd = null    // TODO: Add time picker support
                )
                BudgetApp.db.expenseDao().insert(entity)
                android.util.Log.d("EditTransactionViewModel", "Transaction saved: $description, $amount, $category")
            } catch (e: Exception) {
                android.util.Log.e("EditTransactionViewModel", "Error saving transaction: ${e.message}", e)
            }
        }
    }

    fun loadTransactionById(id: Long, callback: (com.example.spendsprout_opsc.roomdb.Expense_Entity?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entity = BudgetApp.db.expenseDao().getById(id)
                CoroutineScope(Dispatchers.Main).launch { callback(entity) }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(null) }
            }
        }
    }
    
    private fun parseUiDateToMillis(ui: String): Long {
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
}

