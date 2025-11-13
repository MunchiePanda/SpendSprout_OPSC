package com.example.spendsprout_opsc.reports

import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.firebase.FirebaseRepositoryProvider
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel {
    
    private val transactionRepository = FirebaseRepositoryProvider.transactionRepository
    
    fun loadMonthlySpent(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        callback: (Double) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
                val total = expenses
                    .filter { it.expenseType == ExpenseType.Expense }
                    .sumOf { it.expenseAmount }
                withContext(Dispatchers.Main) {
                    callback(total)
                }
            } catch (e: Exception) {
                android.util.Log.e("ReportsViewModel", "Error loading monthly spent: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    callback(0.0)
                }
            }
        }
    }

    fun loadDailySpendSeries(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        monthlyTarget: Double = 0.0,
        callback: (List<ChartDataPoint>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val expenses = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
                    .filter { it.expenseType == ExpenseType.Expense }

                val days = getDaysInRange(startDate, endDate)
                val perDayTarget = if (days.isNotEmpty() && monthlyTarget > 0) monthlyTarget / days.size else 0.0

                val dailySum = expenses.groupBy { sdf.format(Date(it.expenseDate)) }
                    .mapValues { (_, list) -> list.sumOf { it.expenseAmount } }

                val series = days.map { dayMillis ->
                    val key = sdf.format(Date(dayMillis))
                    val spent = dailySum[key] ?: 0.0
                    ChartDataPoint(
                        month = key,
                        revenue = spent,
                        target = perDayTarget
                    )
                }
                withContext(Dispatchers.Main) {
                    callback(series)
                }
            } catch (e: Exception) {
                android.util.Log.e("ReportsViewModel", "Error loading daily spend series: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            }
        }
    }

    fun loadCategoryTotals(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        callback: (List<CategoryTotal>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = transactionRepository.getTransactionsBetweenDates(startDate, endDate)
                val totals = expenses
                    .filter { it.expenseType == ExpenseType.Expense }
                    .groupBy { it.expenseCategory }
                    .map { (category, list) ->
                        CategoryTotal(category, list.sumOf { it.expenseAmount })
                    }
                    .sortedByDescending { it.total }
                withContext(Dispatchers.Main) {
                    callback(totals)
                }
            } catch (e: Exception) {
                android.util.Log.e("ReportsViewModel", "Error loading category totals: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            }
        }
    }

    private fun getDaysInRange(start: Long, end: Long): List<Long> {
        val days = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = start
        while (cal.timeInMillis <= end) {
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            days.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
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

data class CategoryTotal(val category: String, val total: Double)
