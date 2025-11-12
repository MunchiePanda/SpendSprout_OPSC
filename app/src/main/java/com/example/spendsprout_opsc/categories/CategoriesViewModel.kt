package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.BudgetApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class CategoriesViewModel {
    
    fun getAllCategories(): List<Category> {
        return listOf(
            Category(
                id = "1",
                name = "Needs",
                spent = "R 8,900",
                allocation = "R 10,000",
                color = "#BD804A"
            ),
            Category(
                id = "2", 
                name = "Wants",
                spent = "- R 120",
                allocation = "R 6,000",
                color = "#88618E"
            ),
            Category(
                id = "3",
                name = "Savings", 
                spent = "R 4,000",
                allocation = "R 4,000",
                color = "#6EA19E"
            )
        )
    }
    
    fun getFilteredCategories(type: String): List<Category> {
        val allCategories = getAllCategories()
        return when (type) {
            "Needs" -> allCategories.filter { it.name == "Needs" }
            "Wants" -> allCategories.filter { it.name == "Wants" }
            "Savings" -> allCategories.filter { it.name == "Savings" }
            else -> allCategories
        }
    }
    
    fun loadMainCategoriesFromDatabase(callback: (List<Category>) -> Unit) {
        callback(emptyList())
    }
//
//    private suspend fun getCategorySpent(categoryId: Long): Double {
//        return try {
//            val expenses = BudgetApp.db.expenseDao().getAll()
//            val categoryName = BudgetApp.db.categoryDao().getById(categoryId.toInt())?.categoryName
//            if (categoryName != null) {
//                expenses.filter { it.expenseCategory == categoryName }
//                    .sumOf { expense ->
//                        // Expenses should be negative values (decreases)
//                        if (expense.expenseType.name == "Expense") {
//                            -expense.expenseAmount  // Negative for expenses
//                        } else {
//                            expense.expenseAmount   // Positive for income
//                        }
//                    }
//            } else {
//                0.0
//            }
//        } catch (e: Exception) {
//            0.0
//        }
//    }
    
    private fun getCategoryColor(categoryName: String): String {
        return when (categoryName.lowercase()) {
            "groceries" -> "#87CEEB"
            "needs" -> "#4169E1"
            "wants" -> "#9370DB"
            "savings" -> "#32CD32"
            else -> "#D3D3D3"
        }
    }
    
    fun loadSubcategoriesForCategory(
        categoryName: String,
        callback: (List<com.example.spendsprout_opsc.wants.model.Subcategory>) -> Unit
    ) {
        callback(emptyList())
    }
//
//    private suspend fun getSubcategoriesForCategory(categoryId: Long): List<com.example.spendsprout_opsc.wants.model.Subcategory> {
//        return try {
//            val subcategoryEntities = BudgetApp.db.subcategoryDao().getByCategoryId(categoryId)
//            android.util.Log.d("CategoriesViewModel", "Found ${subcategoryEntities.size} subcategories for categoryId: $categoryId")
//
//            subcategoryEntities.map { subcategory ->
//                val actualSpent = getSubcategorySpent(subcategory.id.toLong())
//                android.util.Log.d("CategoriesViewModel", "Subcategory: ${subcategory.subcategoryName}, Spent: $actualSpent")
//
//                com.example.spendsprout_opsc.wants.model.Subcategory(
//                    id = subcategory.id.toString(),
//                    name = subcategory.subcategoryName,
//                    spent = formatAmount(actualSpent),
//                    allocation = formatAmount(subcategory.subcategoryAllocation),
//                    color = getSubcategoryColor(subcategory.subcategoryColor)
//                )
//            }
//        } catch (e: Exception) {
//            android.util.Log.e("CategoriesViewModel", "Error getting subcategories: ${e.message}", e)
//            emptyList()
//        }
//    }
//
//    private suspend fun getSubcategorySpent(subcategoryId: Long): Double {
//        return try {
//            val expenses = BudgetApp.db.expenseDao().getAll()
//            val subcategoryName = BudgetApp.db.subcategoryDao().getById(subcategoryId.toInt())?.subcategoryName
//            if (subcategoryName != null) {
//                expenses.filter { it.expenseCategory == subcategoryName }
//                    .sumOf { expense ->
//                        if (expense.expenseType.name == "Expense") {
//                            -expense.expenseAmount
//                        } else {
//                            expense.expenseAmount
//                        }
//                    }
//            } else {
//                0.0
//            }
//        } catch (e: Exception) {
//            0.0
//        }
//    }
    
    private fun formatAmount(amount: Double): String {
        val sign = if (amount < 0) "-" else ""
        val absoluteAmount = kotlin.math.abs(amount)
        return "$sign R ${String.format("%.2f", absoluteAmount)}"
    }
    
    private fun getSubcategoryColor(colorInt: Int): String {
        // Convert integer color to hex string
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }
}

