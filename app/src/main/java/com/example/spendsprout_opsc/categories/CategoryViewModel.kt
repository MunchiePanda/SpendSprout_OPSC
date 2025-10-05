package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.categories.model.Subcategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class CategoryViewModel {
    
    fun getAllCategories(): List<Category> {
        // For now, return empty list - will be populated by database queries
        return emptyList()
    }
    
    // New method to load categories with subcategories from database
    fun loadCategoriesWithSubcategoriesFromDatabase(callback: (List<HierarchicalCategoryAdapter.CategoryWithSubcategories>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Ensure main categories exist (Needs, Wants, Savings)
                ensureMainCategoriesExist()
                
                val categories = BudgetApp.db.categoryDao().getAll().first()
                val categoryList = categories.map { category ->
                    val subcategories = getSubcategoriesForCategory(category.id.toLong())
                    val totalSpent = calculateTotalSpent(subcategories)
                    
                    val mainCategory = Category(
                        id = category.id.toString(),
                        name = category.categoryName,
                        spent = formatAmount(totalSpent),
                        allocation = formatAmount(category.categoryAllocation),
                        color = getCategoryColor(category.categoryName)
                    )
                    HierarchicalCategoryAdapter.CategoryWithSubcategories(mainCategory, subcategories)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    callback(categoryList)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }
    
    // Legacy method for backward compatibility
    fun loadCategoriesFromDatabase(callback: (List<Category>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categories = BudgetApp.db.categoryDao().getAll().first()
                val categoryList = categories.map { category ->
                    Category(
                        id = category.id.toString(),
                        name = category.categoryName,
                        spent = formatAmount(getCategorySpent(category.id.toLong())),
                        allocation = formatAmount(category.categoryAllocation),
                        color = getCategoryColor(category.categoryName)
                    )
                }
                CoroutineScope(Dispatchers.Main).launch {
                    callback(categoryList)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }
    
    // Load categories for a specific date range
    fun loadCategoriesForDateRange(startDate: Long, endDate: Long, callback: (List<Category>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categories = BudgetApp.db.categoryDao().getAll().first()
                val categoryList = categories.map { category ->
                    Category(
                        id = category.id.toString(),
                        name = category.categoryName,
                        spent = formatAmount(getCategorySpentForDateRange(category.id.toLong(), startDate, endDate)),
                        allocation = formatAmount(category.categoryAllocation),
                        color = getCategoryColor(category.categoryName)
                    )
                }
                CoroutineScope(Dispatchers.Main).launch {
                    callback(categoryList)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }
    
    private fun formatAmount(amount: Double): String {
        return "R ${String.format("%.2f", amount)}"
    }
    
    private fun getCategorySpent(categoryId: Long): Double {
        // TODO: Get total spent for this category from database
        // For now, return 0
        return 0.0
    }
    
    private fun getCategorySpentForDateRange(categoryId: Long, startDate: Long, endDate: Long): Double {
        // TODO: Get total spent for this category within date range from database
        // For now, return 0
        return 0.0
    }
    
    private suspend fun getSubcategoriesForCategory(categoryId: Long): List<Subcategory> {
        return try {
            val subcategoryEntities = BudgetApp.db.subcategoryDao().getByCategoryId(categoryId)
            subcategoryEntities.map { subcategory ->
                Subcategory(
                    id = subcategory.id.toString(),
                    name = subcategory.subcategoryName,
                    spent = formatAmount(subcategory.subcategoryBalance),
                    allocation = formatAmount(subcategory.subcategoryAllocation),
                    color = getSubcategoryColor(subcategory.subcategoryColor)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun getSubcategoryColor(colorInt: Int): String {
        // Convert integer color to hex string
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }
    
    private suspend fun ensureMainCategoriesExist() {
        try {
            val existingCategories = BudgetApp.db.categoryDao().getAll().first()
            val existingNames = existingCategories.map { it.categoryName.lowercase() }.toSet()

            val toInsert = mutableListOf<com.example.spendsprout_opsc.roomdb.Category_Entity>()
            if ("needs" !in existingNames) {
                toInsert.add(
                    com.example.spendsprout_opsc.roomdb.Category_Entity(
                        id = (existingCategories.maxOfOrNull { it.id } ?: 0) + 1,
                        categoryName = "Needs",
                        categoryColor = android.graphics.Color.parseColor("#4169E1").toInt(),
                        categoryBalance = 0.0,
                        categoryAllocation = 0.0,
                        categoryNotes = "Essential expenses"
                    )
                )
            }
            if ("wants" !in existingNames) {
                toInsert.add(
                    com.example.spendsprout_opsc.roomdb.Category_Entity(
                        id = (existingCategories.maxOfOrNull { it.id } ?: 0) + 2,
                        categoryName = "Wants",
                        categoryColor = android.graphics.Color.parseColor("#9370DB").toInt(),
                        categoryBalance = 0.0,
                        categoryAllocation = 0.0,
                        categoryNotes = "Non-essential expenses"
                    )
                )
            }
            if ("savings" !in existingNames) {
                toInsert.add(
                    com.example.spendsprout_opsc.roomdb.Category_Entity(
                        id = (existingCategories.maxOfOrNull { it.id } ?: 0) + 3,
                        categoryName = "Savings",
                        categoryColor = android.graphics.Color.parseColor("#32CD32").toInt(),
                        categoryBalance = 0.0,
                        categoryAllocation = 0.0,
                        categoryNotes = "Savings and investments"
                    )
                )
            }
            if (toInsert.isNotEmpty()) {
                BudgetApp.db.categoryDao().insertAll(*toInsert.toTypedArray())
            }
        } catch (e: Exception) {
            android.util.Log.e("CategoryViewModel", "Error ensuring main categories: ${e.message}", e)
        }
    }
    
    private fun calculateTotalSpent(subcategories: List<Subcategory>): Double {
        return subcategories.sumOf { subcategory ->
            // Parse the amount from the formatted string (e.g., "R 3,500" -> 3500.0)
            parseAmountFromString(subcategory.spent)
        }
    }
    
    private fun parseAmountFromString(amountString: String): Double {
        return try {
            // Remove "R " prefix and any commas, then parse as double
            amountString.replace("R ", "").replace(",", "").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
    
    private fun getCategoryColor(categoryName: String): String {
        return when (categoryName.lowercase()) {
            "groceries" -> "#87CEEB"
            "needs" -> "#4169E1"
            "wants" -> "#9370DB"
            "savings" -> "#32CD32"
            else -> "#D3D3D3"
        }
    }
}
