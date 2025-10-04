package com.example.spendsprout_opsc.edit

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.spendsprout_opsc.wants.model.Subcategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditCategoryViewModel {
    
    private val TAG = "EditCategoryViewModel"
    
    fun saveCategory(name: String, type: String, allocatedBudget: Double, color: String, notes: String, context: Context) {
        // Validate input
        require(name.isNotBlank()) { "Category name is required" }
        require(allocatedBudget > 0) { "Allocated budget must be greater than 0" }
        
        try {
            // Get existing subcategories from SharedPreferences
            val prefs = context.getSharedPreferences("spendsprout_data", Context.MODE_PRIVATE)
            val gson = Gson()
            val existingJson = prefs.getString("subcategories", "[]")
            val typeToken = object : TypeToken<List<Subcategory>>() {}.type
            val existingSubcategories = gson.fromJson<List<Subcategory>>(existingJson, typeToken).toMutableList()
            
            // Create new subcategory
            val newSubcategory = Subcategory(
                id = existingSubcategories.size + 1, // Simple ID generation
                categoryId = getCategoryIdFromType(type),
                name = name,
                color = parseColorToInt(color),
                balance = 0.0, // Start with 0 spent
                allocation = allocatedBudget,
                notes = notes
            )
            
            // Add to list
            existingSubcategories.add(newSubcategory)
            
            // Save back to SharedPreferences
            val updatedJson = gson.toJson(existingSubcategories)
            prefs.edit().putString("subcategories", updatedJson).apply()
            
            Log.d(TAG, "Subcategory saved: $name with budget $allocatedBudget")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving subcategory", e)
            throw RuntimeException("Failed to save subcategory: ${e.message}")
        }
    }
    
    private fun getCategoryIdFromType(type: String): Int {
        return when (type) {
            "Needs" -> 1
            "Wants" -> 2
            "Savings" -> 3
            else -> 1 // Default to Needs
        }
    }
    
    private fun parseColorToInt(colorHex: String): Int {
        return try {
            android.graphics.Color.parseColor(colorHex)
        } catch (e: Exception) {
            android.graphics.Color.BLACK
        }
    }
}

