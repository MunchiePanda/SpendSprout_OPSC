package com.example.spendsprout_opsc.reports

import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.firebase.TransactionRepository
import com.example.spendsprout_opsc.firebase.BudgetRepository
import com.example.spendsprout_opsc.firebase.CategoryRepository
import com.example.spendsprout_opsc.firebase.SubcategoryRepository
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import com.example.spendsprout_opsc.roomdb.Budget_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel {
    
    private val transactionRepository = TransactionRepository()
    private val budgetRepository = BudgetRepository()
    private val categoryRepository = CategoryRepository()
    private val subcategoryRepository = SubcategoryRepository()
    
    // Total spent this month from DB (expenses only)
    fun loadMonthlySpent(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        callback: (Double) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
                // Calculate total spent (expenses are positive, income is negative)
                val total = expenses.sumOf { entity ->
                    if (entity.expenseType == ExpenseType.Expense) {
                        entity.expenseAmount
                    } else {
                        -entity.expenseAmount
                    }
                }
                CoroutineScope(Dispatchers.Main).launch { callback(total) }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(0.0) }
            }
        }
    }

    // Daily spend series for the chart (revenue = daily spent; target = daily target if provided)
    fun loadDailySpendSeries(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        monthlyTarget: Double = 0.0,
        callback: (List<ChartDataPoint>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load actual expenses from database
                val expenses = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
                
                // Group expenses by day and calculate daily totals
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val totalsByDay = expenses.groupBy { sdf.format(Date(it.expenseDate)) }
                    .mapValues { (_, list) ->
                        list.sumOf { entity ->
                            if (entity.expenseType == ExpenseType.Expense) {
                                entity.expenseAmount
                            } else {
                                -entity.expenseAmount
                            }
                        }
                    }

                val days = getDaysInRange(startDate, endDate)
                val perDayTarget = if (days.isNotEmpty() && monthlyTarget > 0) monthlyTarget / days.size else 0.0

                // Create series from actual data
                val series = days.map { dayMillis ->
                    val key = sdf.format(Date(dayMillis))
                    val spent = totalsByDay[key] ?: 0.0
                    ChartDataPoint(
                        month = key,
                        revenue = spent,
                        target = perDayTarget
                    )
                }
                CoroutineScope(Dispatchers.Main).launch { callback(series) }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(emptyList()) }
            }
        }
    }

    // Existing: category totals (kept)
    fun loadCategoryTotals(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        callback: (List<com.example.spendsprout_opsc.roomdb.CategoryTotal>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load actual category totals from database
                val totals = transactionRepository.getCategoryTotals(startDate, endDate)
                CoroutineScope(Dispatchers.Main).launch { callback(totals) }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { callback(emptyList()) }
            }
        }
    }

    private fun getDaysInRange(start: Long, end: Long): List<Long> {
        val days = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = start
        while (cal.timeInMillis <= end) {
            // normalize to midnight
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            days.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    suspend fun getTransactionsBetweenDates(start: Long, end: Long): List<Expense_Entity> {
        return transactionRepository.getTransactionsBetweenDates(start, end)
    }

    suspend fun getBudgetsSnapshot(): List<Budget_Entity> {
        return budgetRepository.getAllBudgets().first()
    }

    suspend fun getCategoriesSnapshot(): List<Category_Entity> {
        return categoryRepository.getAllCategories().first()
    }

    suspend fun getSubcategoriesSnapshot(): List<Subcategory_Entity> {
        return subcategoryRepository.getAllSubcategories().first()
    }

    suspend fun getAllTransactionsSnapshot(): List<Expense_Entity> {
        return transactionRepository.getAllTransactionsSnapshot()
    }

    fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

