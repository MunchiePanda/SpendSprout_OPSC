package com.example.spendsprout_opsc.edit

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.graphics.Color
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class EditCategoryViewModel {
    
    fun saveCategory(name: String, type: String, allocatedBudget: Double, color: String, notes: String) {
        // Save category logic - validate first
        require(name.isNotBlank()) { "Category name is required" }
        require(allocatedBudget > 0) { "Allocated budget must be greater than 0" }
        
        // Resolve the parent category id by name (create if missing)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categoryId = resolveOrCreateCategoryId(type)
                val subcategory = Subcategory_Entity(
                    id = getNextSubcategoryId(),
                    categoryId = categoryId,
                    subcategoryName = name,
                    subcategoryColor = Color.parseColor(color),
                    subcategoryBalance = 0.0, // Start with 0 spent
                    subcategoryAllocation = allocatedBudget,
                    subcategoryNotes = notes.ifBlank { null }
                )
                BudgetApp.db.subcategoryDao().insertAll(subcategory)
                android.util.Log.d("EditCategoryViewModel", "Subcategory saved: $name under $type")
            } catch (e: Exception) {
                android.util.Log.e("EditCategoryViewModel", "Error saving subcategory: ${e.message}", e)
            }
        }
    }
    
    private suspend fun resolveOrCreateCategoryId(type: String): Int {
        val normalized = when (type.lowercase()) {
            "needs" -> "Needs"
            "wants" -> "Wants"
            "savings" -> "Savings"
            else -> type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        val existing = BudgetApp.db.categoryDao().loadAllByNames(listOf(normalized))
        if (existing.isNotEmpty()) return existing.first().id

        val nextId = getNextCategoryId()
        val entity = Category_Entity(
            id = nextId,
            categoryName = normalized,
            categoryColor = Color.parseColor("#4169E1"),
            categoryBalance = 0.0,
            categoryAllocation = 0.0,
            categoryNotes = null
        )
        BudgetApp.db.categoryDao().insertAll(entity)
        return nextId
    }

    private suspend fun getNextCategoryId(): Int {
        val existing = BudgetApp.db.categoryDao().getAll().first()
        return (existing.maxOfOrNull { it.id } ?: 0) + 1
    }

    private suspend fun getNextSubcategoryId(): Int {
        return try {
            val existingSubcategories = BudgetApp.db.subcategoryDao().getAll()
            (existingSubcategories.maxOfOrNull { it.id } ?: 0) + 1
        } catch (e: Exception) {
            1 // Default to 1 if there's an error
        }
    }
    
    fun loadSubcategoryById(subcategoryId: Int, callback: (Subcategory_Entity?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val subcategory = BudgetApp.db.subcategoryDao().getById(subcategoryId)
                CoroutineScope(Dispatchers.Main).launch {
                    callback(subcategory)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(null)
                }
            }
        }
    }
    
    fun getParentCategoryName(categoryId: Int): String {
        return try {
            runBlocking {
                val categories = BudgetApp.db.categoryDao().getAll().first()
                categories.find { it.id == categoryId }?.categoryName ?: "Needs"
            }
        } catch (e: Exception) {
            "Needs"
        }
    }
}

