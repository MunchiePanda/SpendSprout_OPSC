package com.example.spendsprout_opsc.edit

import android.util.Log
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditSubcategoryViewModel {
    
    suspend fun saveSubcategory(
        name: String, 
        categoryId: Int, 
        color: Int, 
        balance: Double, 
        allocation: Double, 
        notes: String
    ) {
        // Validate
        require(name.isNotBlank()) { "Subcategory name is required" }
        require(categoryId > 0) { "Category ID must be valid" }

        // Write to DB synchronously
        try {
            val entity = Subcategory_Entity(
                id = getNextSubcategoryId(),
                categoryId = categoryId,
                subcategoryName = name,
                subcategoryColor = color,
                subcategoryBalance = balance,
                subcategoryAllocation = allocation,
                subcategoryNotes = notes.ifBlank { null }
            )
            BudgetApp.db.subcategoryDao().insert(entity)
            Log.d("EditSubcategoryViewModel", "Subcategory saved: $name categoryId=$categoryId balance=$balance allocation=$allocation")
        } catch (e: Exception) {
            Log.e("EditSubcategoryViewModel", "Error saving subcategory: ${e.message}", e)
            throw e // Re-throw to handle in Activity
        }
    }

    suspend fun updateSubcategory(
        id: Int, 
        name: String, 
        categoryId: Int, 
        color: Int, 
        balance: Double, 
        allocation: Double, 
        notes: String
    ) {
        // Validate
        require(name.isNotBlank()) { "Subcategory name is required" }
        require(categoryId > 0) { "Category ID must be valid" }

        Log.d("EditSubcategoryViewModel", "Starting subcategory update for ID: $id, name: $name")

        // Write to DB synchronously
        try {
            val entity = Subcategory_Entity(
                id = id,
                categoryId = categoryId,
                subcategoryName = name,
                subcategoryColor = color,
                subcategoryBalance = balance,
                subcategoryAllocation = allocation,
                subcategoryNotes = notes.ifBlank { null }
            )
            
            Log.d("EditSubcategoryViewModel", "Updating with entity: $entity")
            val result = BudgetApp.db.subcategoryDao().update(entity)
            Log.d("EditSubcategoryViewModel", "Update result: $result")
            Log.d("EditSubcategoryViewModel", "Subcategory updated successfully: $name categoryId=$categoryId balance=$balance allocation=$allocation")
        } catch (e: Exception) {
            Log.e("EditSubcategoryViewModel", "Error updating subcategory: ${e.message}", e)
            throw e // Re-throw to handle in Activity
        }
    }

    private suspend fun getNextSubcategoryId(): Int {
        return try {
            val count = BudgetApp.db.subcategoryDao().getCount()
            count + 1
        } catch (e: Exception) {
            1
        }
    }
}
