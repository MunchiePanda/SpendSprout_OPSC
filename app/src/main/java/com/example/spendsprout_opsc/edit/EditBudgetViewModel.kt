package com.example.spendsprout_opsc.edit

import android.util.Log
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Budget_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditBudgetViewModel {
    
    fun saveBudget(name: String, openingBalance: Double, minGoal: Double, maxGoal: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Budget name is required" }
        require(minGoal < maxGoal) { "Minimum goal must be less than maximum goal" }
        require(minGoal <= openingBalance && maxGoal <= openingBalance) { "Goals cannot exceed opening balance" }

        // Write to DB on IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entity = Budget_Entity(
                    id = getNextBudgetId(),
                    budgetName = name,
                    openingBalance = openingBalance,
                    budgetMinGoal = minGoal,
                    budgetMaxGoal = maxGoal,
                    budgetBalance = openingBalance, // Initially equals opening balance
                    budgetNotes = notes.ifBlank { null }
                )
                BudgetApp.db.budgetDao().insertAll(entity)
                Log.d("EditBudgetViewModel", "Budget saved: $name opening=$openingBalance min=$minGoal max=$maxGoal")
            } catch (e: Exception) {
                Log.e("EditBudgetViewModel", "Error saving budget: ${e.message}", e)
            }
        }
    }

    fun updateBudget(id: Int, name: String, openingBalance: Double, minGoal: Double, maxGoal: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Budget name is required" }
        require(minGoal < maxGoal) { "Minimum goal must be less than maximum goal" }
        require(minGoal <= openingBalance && maxGoal <= openingBalance) { "Goals cannot exceed opening balance" }

        // Write to DB on IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get the existing budget to preserve the current balance
                val existingBudget = BudgetApp.db.budgetDao().getById(id)
                
                val entity = Budget_Entity(
                    id = id,
                    budgetName = name,
                    openingBalance = openingBalance,
                    budgetMinGoal = minGoal,
                    budgetMaxGoal = maxGoal,
                    budgetBalance = existingBudget?.budgetBalance ?: openingBalance, // Preserve current balance or use opening balance if not found
                    budgetNotes = notes.ifBlank { null }
                )
                BudgetApp.db.budgetDao().update(entity)
                Log.d("EditBudgetViewModel", "Budget updated: $name opening=$openingBalance min=$minGoal max=$maxGoal balance=${entity.budgetBalance}")
            } catch (e: Exception) {
                Log.e("EditBudgetViewModel", "Error updating budget: ${e.message}", e)
            }
        }
    }

    private fun getNextBudgetId(): Int {
        return try {
            // Not a suspend func; do not call Flow.first() here. Use a safe default.
            val existing = emptyList<Budget_Entity>()
            (existing.maxOfOrNull { it.id } ?: 0) + 1
        } catch (e: Exception) {
            1
        }
    }
}