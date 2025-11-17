package com.example.spendsprout_opsc.edit

import android.util.Log
import com.example.spendsprout_opsc.firebase.BudgetRepository
import com.example.spendsprout_opsc.roomdb.Budget_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditBudgetViewModel {
    
    private val budgetRepository = BudgetRepository()
    
    suspend fun saveBudget(name: String, openingBalance: Double, minGoal: Double, maxGoal: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Budget name is required" }
        require(minGoal < maxGoal) { "Minimum goal must be less than maximum goal" }
        require(minGoal <= openingBalance && maxGoal <= openingBalance) { "Goals cannot exceed opening balance" }

        // Write to DB using repository (works with Firebase or Room)
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
            budgetRepository.insertBudget(entity)
            Log.d("EditBudgetViewModel", "Budget saved: $name opening=$openingBalance min=$minGoal max=$maxGoal")
        } catch (e: Exception) {
            Log.e("EditBudgetViewModel", "Error saving budget: ${e.message}", e)
            throw e // Re-throw to handle in Activity
        }
    }

    suspend fun updateBudget(id: Int, name: String, openingBalance: Double, minGoal: Double, maxGoal: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Budget name is required" }
        require(minGoal < maxGoal) { "Minimum goal must be less than maximum goal" }
        require(minGoal <= openingBalance && maxGoal <= openingBalance) { "Goals cannot exceed opening balance" }

        Log.d("EditBudgetViewModel", "Starting budget update for ID: $id, name: $name")

        // Write to DB using repository
        try {
            // Get the existing budget to preserve the current balance
            val existingBudget = budgetRepository.getBudgetById(id)
            Log.d("EditBudgetViewModel", "Found existing budget: $existingBudget")
            
            val entity = Budget_Entity(
                id = id,
                budgetName = name,
                openingBalance = openingBalance,
                budgetMinGoal = minGoal,
                budgetMaxGoal = maxGoal,
                budgetBalance = existingBudget?.budgetBalance ?: openingBalance, // Preserve current balance or use opening balance if not found
                budgetNotes = notes.ifBlank { null }
            )
            
            Log.d("EditBudgetViewModel", "Updating with entity: $entity")
            budgetRepository.updateBudget(entity)
            Log.d("EditBudgetViewModel", "Budget updated successfully: $name opening=$openingBalance min=$minGoal max=$maxGoal balance=${entity.budgetBalance}")
        } catch (e: Exception) {
            Log.e("EditBudgetViewModel", "Error updating budget: ${e.message}", e)
            throw e // Re-throw to handle in Activity
        }
    }

    private suspend fun getNextBudgetId(): Int {
        return try {
            val count = budgetRepository.getBudgetCount()
            count + 1
        } catch (e: Exception) {
            1
        }
    }
}