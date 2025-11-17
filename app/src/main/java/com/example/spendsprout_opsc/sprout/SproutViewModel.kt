package com.example.spendsprout_opsc.sprout

import android.content.Context
import android.content.SharedPreferences
import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.firebase.BudgetRepository
import com.example.spendsprout_opsc.firebase.CategoryRepository
import com.example.spendsprout_opsc.firebase.SubcategoryRepository
import com.example.spendsprout_opsc.firebase.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class SproutViewModel(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("SproutPrefs", Context.MODE_PRIVATE)
    private val budgetRepository = BudgetRepository()
    private val categoryRepository = CategoryRepository()
    private val subcategoryRepository = SubcategoryRepository()
    private val transactionRepository = TransactionRepository()
    
    // Calculate budget adherence (0-100%)
    fun calculateBudgetAdherence(callback: (Int) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val budgets = budgetRepository.getAllBudgets().first()
                if (budgets.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch { callback(0) }
                    return@launch
                }
                
                val currentBudget = budgets.first()
                val calendar = Calendar.getInstance()
                val startOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val endOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                
                // Get expenses for current month
                val expenses = transactionRepository.getTransactionsBetweenDates(startOfMonth, endOfMonth)
                val totalSpent = expenses
                    .filter { it.expenseType == ExpenseType.Expense }
                    .sumOf { it.expenseAmount }
                
                // Calculate adherence: staying within max budget = 100%, going over = penalty
                val maxBudget = currentBudget.budgetMaxGoal
                val minBudget = currentBudget.budgetMinGoal
                val targetBudget = (maxBudget + minBudget) / 2.0 // Target middle of range
                
                val adherence = when {
                    maxBudget <= 0 -> 0
                    totalSpent <= minBudget -> 100 // Under budget is good
                    totalSpent >= maxBudget -> {
                        // Over budget: penalty based on how much over
                        val overAmount = totalSpent - maxBudget
                        val penalty = (overAmount / maxBudget * 100).toInt()
                        (100 - penalty).coerceAtLeast(0)
                    }
                    else -> {
                        // Between min and max: calculate percentage
                        val range = maxBudget - minBudget
                        val position = (totalSpent - minBudget) / range
                        // Closer to min = better score (inverted)
                        (100 - (position * 50)).toInt() // At min = 100, at max = 50
                    }
                }
                
                CoroutineScope(Dispatchers.Main).launch {
                    callback(adherence.coerceIn(0, 100))
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(0) }
            }
        }
    }
    
    // Calculate consecutive check-in days
    fun calculateCheckInStreak(callback: (Int, Int) -> Unit) {
        // Returns: (consecutive days, total days checked in)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val checkInDates = getCheckInDates()
                
                if (checkInDates.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch { callback(0, 0) }
                    return@launch
                }
                
                // Sort dates descending
                val sortedDates = checkInDates.sortedDescending()
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                // Calculate consecutive days from today backwards
                var consecutiveDays = 0
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = today
                
                // Check if there's a check-in today or yesterday
                var expectedDate = today
                for (date in sortedDates) {
                    calendar.timeInMillis = date
                    val checkInDay = calendar.get(Calendar.DAY_OF_YEAR)
                    val checkInYear = calendar.get(Calendar.YEAR)
                    
                    calendar.timeInMillis = expectedDate
                    val expectedDay = calendar.get(Calendar.DAY_OF_YEAR)
                    val expectedYear = calendar.get(Calendar.YEAR)
                    
                    // Check if dates match (same day)
                    if (checkInYear == expectedYear && checkInDay == expectedDay) {
                        consecutiveDays++
                        // Move to previous day
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        expectedDate = calendar.timeInMillis
                    } else if (consecutiveDays == 0 && checkInYear == expectedYear) {
                        // Check if yesterday (for first check)
                        calendar.timeInMillis = today
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        if (calendar.get(Calendar.DAY_OF_YEAR) == checkInDay && 
                            calendar.get(Calendar.YEAR) == checkInYear) {
                            consecutiveDays = 1
                            calendar.add(Calendar.DAY_OF_MONTH, -1)
                            expectedDate = calendar.timeInMillis
                        } else {
                            break
                        }
                    } else {
                        break
                    }
                }
                
                // Progress bar: 100% = 30 consecutive days (adjustable)
                val maxStreak = 30
                val streakProgress = ((consecutiveDays.toDouble() / maxStreak) * 100).toInt().coerceIn(0, 100)
                
                CoroutineScope(Dispatchers.Main).launch {
                    callback(consecutiveDays, streakProgress)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(0, 0) }
            }
        }
    }
    
    // Calculate category limit adherence
    fun calculateCategoryAdherence(callback: (Int) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categories = categoryRepository.getAllCategories().first()
                val subcategories = subcategoryRepository.getAllSubcategories().first()
                
                if (categories.isEmpty() && subcategories.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch { callback(0) }
                    return@launch
                }
                
                val calendar = Calendar.getInstance()
                val startOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val endOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                
                val expenses = transactionRepository.getTransactionsBetweenDates(startOfMonth, endOfMonth)
                val expenseTransactions = expenses.filter { it.expenseType == ExpenseType.Expense }
                val expenseTotalsByName = expenseTransactions.groupBy { it.expenseCategory }
                    .mapValues { (_, list) -> list.sumOf { it.expenseAmount } }
                
                val subcategoryNamesByCategory = subcategories.groupBy { it.categoryId }
                    .mapValues { entry -> entry.value.map { it.subcategoryName }.toSet() }
                
                var totalAdherence = 0.0
                var categoryCount = 0
                
                // Check category adherence
                categories.forEach { category ->
                    val names = subcategoryNamesByCategory[category.id] ?: emptySet()
                    val categorySpending =
                        (expenseTotalsByName[category.categoryName] ?: 0.0) +
                                names.sumOf { subName -> expenseTotalsByName[subName] ?: 0.0 }
                    
                    if (category.categoryAllocation > 0) {
                        val adherence = if (categorySpending <= category.categoryAllocation) {
                            // Within limit
                            val percentage = (categorySpending / category.categoryAllocation * 100).coerceAtMost(100.0)
                            // Reward being closer to 80% (sweet spot)
                            when {
                                percentage <= 80 -> 100.0
                                percentage <= 100 -> 100.0 - ((percentage - 80) * 2.5) // Linear penalty from 80-100%
                                else -> {
                                    // Over budget
                                    val overAmount = ((percentage - 100) * category.categoryAllocation / 100)
                                    (50 - (overAmount / category.categoryAllocation * 50)).coerceAtLeast(0.0)
                                }
                            }
                        } else {
                            // Over budget - calculate penalty
                            val overAmount = categorySpending - category.categoryAllocation
                            val penalty = (overAmount / category.categoryAllocation * 100)
                            (50 - penalty).coerceAtLeast(0.0)
                        }
                        totalAdherence += adherence
                        categoryCount++
                    }
                }
                
                // Check subcategory adherence
                subcategories.forEach { subcategory ->
                    val subcategorySpending = expenseTotalsByName[subcategory.subcategoryName] ?: 0.0

                    if (subcategory.subcategoryAllocation > 0) {
                        val adherence = if (subcategorySpending <= subcategory.subcategoryAllocation) {
                            val percentage = (subcategorySpending / subcategory.subcategoryAllocation * 100).coerceAtMost(100.0)
                            when {
                                percentage <= 80 -> 100.0
                                percentage <= 100 -> 100.0 - ((percentage - 80) * 2.5)
                                else -> {
                                    val overAmount = ((percentage - 100) * subcategory.subcategoryAllocation / 100)
                                    (50 - (overAmount / subcategory.subcategoryAllocation * 50)).coerceAtLeast(0.0)
                                }
                            }
                        } else {
                            val overAmount = subcategorySpending - subcategory.subcategoryAllocation
                            val penalty = (overAmount / subcategory.subcategoryAllocation * 100)
                            (50 - penalty).coerceAtLeast(0.0)
                        }
                        totalAdherence += adherence
                        categoryCount++
                    }
                }
                
                val averageAdherence = if (categoryCount > 0) {
                    (totalAdherence / categoryCount).toInt().coerceIn(0, 100)
                } else {
                    0
                }
                
                CoroutineScope(Dispatchers.Main).launch {
                    callback(averageAdherence)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(0) }
            }
        }
    }
    
    // Calculate overall health score (weighted average)
    fun calculateOverallHealth(
        budgetAdherence: Int,
        checkInProgress: Int,
        categoryAdherence: Int,
        callback: (Int) -> Unit
    ) {
        // Weighted formula: Budget 40%, Check-in 30%, Categories 30%
        val overallHealth = (
            budgetAdherence * 0.40 +
            checkInProgress * 0.30 +
            categoryAdherence * 0.30
        ).toInt().coerceIn(0, 100)
        
        callback(overallHealth)
    }
    
    // Get plant state based on health percentage (4 quarters like Zygarde)
    fun getPlantState(healthPercentage: Int): PlantState {
        return when {
            healthPercentage >= 75 -> PlantState.FLOURISHING  // 75-100%
            healthPercentage >= 50 -> PlantState.GOOD         // 50-74%
            healthPercentage >= 25 -> PlantState.POOR         // 25-49%
            else -> PlantState.WILTED                         // 0-24%
        }
    }
    
    // Check-in functionality
    fun checkIn(callback: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val today = calendar.timeInMillis
                
                val checkInDates = getCheckInDates()
                
                // Check if already checked in today
                if (checkInDates.contains(today)) {
                    CoroutineScope(Dispatchers.Main).launch {
                        callback(false, "You've already checked in today!")
                    }
                    return@launch
                }
                
                // Save check-in
                checkInDates.add(today)
                saveCheckInDates(checkInDates)
                
                CoroutineScope(Dispatchers.Main).launch {
                    callback(true, "Check-in successful! Your plant thanks you! 🌱")
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback(false, "Failed to check in: ${e.message}")
                }
            }
        }
    }
    
    private fun getCheckInDates(): MutableSet<Long> {
        val datesString = prefs.getString("check_in_dates", "")
        if (datesString.isNullOrEmpty()) {
            return mutableSetOf()
        }
        return datesString.split(",").mapNotNull { it.toLongOrNull() }.toMutableSet()
    }
    
    private fun saveCheckInDates(dates: Set<Long>) {
        val datesString = dates.joinToString(",")
        prefs.edit().putString("check_in_dates", datesString).apply()
    }
    
    enum class PlantState {
        WILTED,      // 0-24%: Dead/wilted plant
        POOR,        // 25-49%: Struggling plant
        GOOD,        // 50-74%: Healthy plant
        FLOURISHING  // 75-100%: Thriving plant
    }
}

