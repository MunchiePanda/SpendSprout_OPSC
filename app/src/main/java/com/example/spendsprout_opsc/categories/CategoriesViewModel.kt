package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.firebase.CategoryRepository
import com.example.spendsprout_opsc.firebase.SubcategoryRepository
import com.example.spendsprout_opsc.firebase.TransactionRepository
import com.example.spendsprout_opsc.ExpenseType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class CategoriesViewModel {
    
    private val categoryRepository = CategoryRepository()
    private val subcategoryRepository = SubcategoryRepository()
    private val transactionRepository = TransactionRepository()
    
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categories = categoryRepository.getAllCategories().first()
                val categoryList = mutableListOf<Category>()
                for (category in categories) {
                    val categoryId = category.id.toLong()
                    val spent = getCategorySpent(categoryId)
                    val allocation = calculateCategoryAllocation(categoryId, category.categoryAllocation)
                    categoryList.add(
                        Category(
                            id = category.id.toString(),
                            name = category.categoryName,
                            spent = formatAmount(spent),
                            allocation = formatAmount(allocation),
                            color = getCategoryColor(category.categoryName)
                        )
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
    
    private suspend fun getCategorySpent(categoryId: Long): Double {
        return try {
            val category = categoryRepository.getCategoryById(categoryId.toInt()) ?: return 0.0
            val subcategories = subcategoryRepository.getByCategoryId(categoryId)
            val subcategoryNames = subcategories.map { it.subcategoryName }.toSet()
            val expenses = transactionRepository.getAllTransactionsSnapshot()
            expenses.filter { expense ->
                expense.expenseCategory == category.categoryName ||
                        subcategoryNames.contains(expense.expenseCategory)
            }.sumOf { expense ->
                if (expense.expenseType == ExpenseType.Expense) expense.expenseAmount else 0.0
            }
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
    
    fun loadSubcategoriesForCategory(categoryName: String, callback: (List<com.example.spendsprout_opsc.wants.model.Subcategory>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Find the category by name
                val categories = categoryRepository.getAllCategories().first()
                val category = categories.find { it.categoryName.equals(categoryName, ignoreCase = true) }
                
                if (category != null) {
                    // Only get subcategories that actually belong to this category
                    val subcategories = getSubcategoriesForCategory(category.id.toLong())
                    CoroutineScope(Dispatchers.Main).launch {
                        callback(subcategories)
                    }
                } else {
                    // If category not found, return empty list
                    CoroutineScope(Dispatchers.Main).launch {
                        callback(emptyList())
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }
    
    private suspend fun getSubcategoriesForCategory(categoryId: Long): List<com.example.spendsprout_opsc.wants.model.Subcategory> {
        return try {
            val subcategoryEntities = subcategoryRepository.getByCategoryId(categoryId)
            android.util.Log.d("CategoriesViewModel", "Found ${subcategoryEntities.size} subcategories for categoryId: $categoryId")
            
            subcategoryEntities.map { subcategory ->
                // Calculate actual spent amount from transactions for this subcategory
                val actualSpent = getSubcategorySpent(subcategory.id.toLong())
                android.util.Log.d("CategoriesViewModel", "Subcategory: ${subcategory.subcategoryName}, Spent: $actualSpent")
                
                com.example.spendsprout_opsc.wants.model.Subcategory(
                    id = subcategory.id.toString(),
                    name = subcategory.subcategoryName,
                    spent = formatAmount(actualSpent),
                    allocation = formatAmount(subcategory.subcategoryAllocation),
                    color = getSubcategoryColor(subcategory.subcategoryColor)
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("CategoriesViewModel", "Error getting subcategories: ${e.message}", e)
            emptyList()
        }
    }
    
    private suspend fun getSubcategorySpent(subcategoryId: Long): Double {
        return try {
            val expenses = transactionRepository.getAllTransactionsSnapshot()
            val subcategoryName = subcategoryRepository.getSubcategoryById(subcategoryId.toInt())?.subcategoryName
            if (subcategoryName != null) {
                val filteredExpenses = expenses.filter { it.expenseCategory == subcategoryName }
                android.util.Log.d("CategoriesViewModel", "Found ${filteredExpenses.size} transactions for subcategory '$subcategoryName'")
                
                val totalSpent = filteredExpenses.sumOf { expense ->
                    // For expenses, use positive amount (how much was spent)
                    // For income, subtract it (income reduces spending)
                    if (expense.expenseType.name == "Expense") {
                        expense.expenseAmount  // Positive amount for expenses
                    } else {
                        -expense.expenseAmount   // Negative for income (reduces spending)
                    }
                }
                
                android.util.Log.d("CategoriesViewModel", "Subcategory '$subcategoryName' total spent: $totalSpent")
                totalSpent
            } else {
                0.0
            }
        } catch (e: Exception) {
            android.util.Log.e("CategoriesViewModel", "Error calculating subcategory spent: ${e.message}", e)
            0.0
        }
    }

    private suspend fun calculateCategoryAllocation(categoryId: Long, fallback: Double): Double {
        return try {
            val subcategories = subcategoryRepository.getByCategoryId(categoryId)
            if (subcategories.isNotEmpty()) {
                subcategories.sumOf { it.subcategoryAllocation }
            } else {
                fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }
    
    private fun formatAmount(amount: Double): String {
        // Spent amounts should always be positive (representing how much was spent)
        // Negative values indicate income that reduces spending
        val absoluteAmount = kotlin.math.abs(amount)
        val prefix = if (amount < 0) "- R" else "R"
        // Use Locale.US to ensure consistent formatting with dot as decimal separator
        return "$prefix ${String.format(java.util.Locale.US, "%.2f", absoluteAmount)}"
    }
    
    private fun getSubcategoryColor(colorInt: Int): String {
        // Convert integer color to hex string
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }
}

