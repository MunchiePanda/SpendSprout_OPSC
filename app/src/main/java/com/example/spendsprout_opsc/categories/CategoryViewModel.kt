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
        loadCategoriesWithSubcategoriesFromDatabase(null, null, callback)
    }
    
    // Overloaded method with date range filtering
    fun loadCategoriesWithSubcategoriesFromDatabase(startDate: Long?, endDate: Long?, callback: (List<HierarchicalCategoryAdapter.CategoryWithSubcategories>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Ensure main categories exist (Needs, Wants, Savings)
                ensureMainCategoriesExist()
                
                val categories = BudgetApp.db.categoryDao().getAll().first()
                val categoryList = mutableListOf<HierarchicalCategoryAdapter.CategoryWithSubcategories>()
                for (category in categories) {
                    val allSubcategories = getSubcategoriesForCategory(category.id.toLong(), startDate, endDate)
                    // Get only top 3 subcategories for overview
                    val top3Subcategories = allSubcategories.take(3)
                    
                    // Use date filtering if dates are provided
                    val categorySpent = if (startDate != null && endDate != null) {
                        android.util.Log.d("CategoryViewModel", "Using date filtering for ${category.categoryName}: $startDate to $endDate")
                        getCategorySpentForDateRange(category.id.toLong(), startDate, endDate)
                    } else {
                        android.util.Log.d("CategoryViewModel", "No date filtering for ${category.categoryName}")
                        getCategorySpent(category.id.toLong())
                    }
                    val subcategorySpent = calculateTotalSpent(allSubcategories) // Use all for total calculation
                    val totalSpent = categorySpent + subcategorySpent
                    
                    val mainCategory = Category(
                        id = category.id.toString(),
                        name = category.categoryName,
                        spent = formatAmount(totalSpent),
                        allocation = formatAmount(category.categoryAllocation),
                        color = getCategoryColor(category.categoryName)
                    )
                    // Convert wants.model.Subcategory to categories.model.Subcategory for the adapter
                    val convertedSubcategories = top3Subcategories.map { wantsSub ->
                        com.example.spendsprout_opsc.categories.model.Subcategory(
                            id = wantsSub.id,
                            name = wantsSub.name,
                            spent = wantsSub.spent,
                            allocation = wantsSub.allocation,
                            color = wantsSub.color
                        )
                    }
                    categoryList.add(HierarchicalCategoryAdapter.CategoryWithSubcategories(mainCategory, convertedSubcategories))
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
                val categoryList = mutableListOf<Category>()
                for (category in categories) {
                    val spent = getCategorySpent(category.id.toLong())
                    categoryList.add(
                        Category(
                            id = category.id.toString(),
                            name = category.categoryName,
                            spent = formatAmount(spent),
                            allocation = formatAmount(category.categoryAllocation),
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
    
    // Load categories for a specific date range
    fun loadCategoriesForDateRange(startDate: Long, endDate: Long, callback: (List<Category>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categories = BudgetApp.db.categoryDao().getAll().first()
                val categoryList = mutableListOf<Category>()
                for (category in categories) {
                    val spent = getCategorySpentForDateRange(category.id.toLong(), startDate, endDate)
                    categoryList.add(
                        Category(
                            id = category.id.toString(),
                            name = category.categoryName,
                            spent = formatAmount(spent),
                            allocation = formatAmount(category.categoryAllocation),
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
    
    private fun formatAmount(amount: Double): String {
        val sign = if (amount < 0) "-" else ""
        val absoluteAmount = kotlin.math.abs(amount)
        return "$sign R ${String.format("%.2f", absoluteAmount)}"
    }
    
    private suspend fun getCategorySpent(categoryId: Long): Double {
        return try {
            val expenses = BudgetApp.db.expenseDao().getAll()
            val categoryName = BudgetApp.db.categoryDao().getById(categoryId.toInt())?.categoryName
            if (categoryName != null) {
                expenses.filter { it.expenseCategory == categoryName }
                    .sumOf { expense ->
                        // Expenses should be negative values (decreases)
                        if (expense.expenseType.name == "Expense") {
                            -expense.expenseAmount  // Negative for expenses
                        } else {
                            expense.expenseAmount   // Positive for income
                        }
                    }
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }
    
    private suspend fun getCategorySpentForDateRange(categoryId: Long, startDate: Long, endDate: Long): Double {
        return try {
            val expenses = BudgetApp.db.expenseDao().getAll()
            val categoryName = BudgetApp.db.categoryDao().getById(categoryId.toInt())?.categoryName
            if (categoryName != null) {
                expenses.filter { 
                    it.expenseCategory == categoryName && 
                    it.expenseDate >= startDate && 
                    it.expenseDate <= endDate 
                }.sumOf { expense ->
                    // Expenses should be negative values (decreases)
                    if (expense.expenseType.name == "Expense") {
                        -expense.expenseAmount  // Negative for expenses
                    } else {
                        expense.expenseAmount   // Positive for income
                    }
                }
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }
    
    private suspend fun getSubcategoriesForCategory(categoryId: Long): List<com.example.spendsprout_opsc.wants.model.Subcategory> {
        return getSubcategoriesForCategory(categoryId, null, null)
    }
    
    private suspend fun getSubcategoriesForCategory(categoryId: Long, startDate: Long?, endDate: Long?): List<com.example.spendsprout_opsc.wants.model.Subcategory> {
        return try {
            val subcategoryEntities = BudgetApp.db.subcategoryDao().getByCategoryId(categoryId)
            subcategoryEntities.map { subcategory ->
                // Calculate actual spent amount from transactions for this subcategory
                val actualSpent = getSubcategorySpent(subcategory.id.toLong(), startDate, endDate)
                com.example.spendsprout_opsc.wants.model.Subcategory(
                    id = subcategory.id.toString(),
                    name = subcategory.subcategoryName,
                    spent = formatAmount(actualSpent),
                    allocation = formatAmount(subcategory.subcategoryAllocation),
                    color = getSubcategoryColor(subcategory.subcategoryColor)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private suspend fun getSubcategorySpent(subcategoryId: Long): Double {
        return getSubcategorySpent(subcategoryId, null, null)
    }
    
    private suspend fun getSubcategorySpent(subcategoryId: Long, startDate: Long?, endDate: Long?): Double {
        return try {
            val expenses = BudgetApp.db.expenseDao().getAll()
            val subcategoryName = BudgetApp.db.subcategoryDao().getById(subcategoryId.toInt())?.subcategoryName
            if (subcategoryName != null) {
                val filteredExpenses = if (startDate != null && endDate != null) {
                    android.util.Log.d("CategoryViewModel", "Filtering subcategory '$subcategoryName' by date: $startDate to $endDate")
                    expenses.filter { 
                        it.expenseCategory == subcategoryName && 
                        it.expenseDate >= startDate && 
                        it.expenseDate <= endDate 
                    }
                } else {
                    android.util.Log.d("CategoryViewModel", "No date filtering for subcategory '$subcategoryName'")
                    expenses.filter { it.expenseCategory == subcategoryName }
                }
                
                filteredExpenses.sumOf { expense ->
                    // Expenses should be negative values (decreases)
                    if (expense.expenseType.name == "Expense") {
                        -expense.expenseAmount  // Negative for expenses
                    } else {
                        expense.expenseAmount   // Positive for income
                    }
                }
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
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
    
    private fun calculateTotalSpent(subcategories: List<com.example.spendsprout_opsc.wants.model.Subcategory>): Double {
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
    
    fun loadSubcategoriesForCategory(categoryName: String, callback: (List<com.example.spendsprout_opsc.wants.model.Subcategory>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Find the category by name
                val categories = BudgetApp.db.categoryDao().getAll().first()
                val category = categories.find { it.categoryName.equals(categoryName, ignoreCase = true) }
                
                if (category != null) {
                    val subcategories = getSubcategoriesForCategory(category.id.toLong())
                    CoroutineScope(Dispatchers.Main).launch {
                        callback(subcategories)
                    }
                } else {
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
