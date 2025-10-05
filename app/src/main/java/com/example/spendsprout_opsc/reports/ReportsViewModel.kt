package com.example.spendsprout_opsc.reports

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.reports.model.ChartDataPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel {
    
    // Total spent this month from DB (expenses only)
    fun loadMonthlySpent(
        startDate: Long = getStartOfMonth(),
        endDate: Long = getEndOfMonth(),
        callback: (Double) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = BudgetApp.db.expenseDao().getBetweenDates(startDate, endDate)
                val total = expenses
                    .filter { it.expenseType == com.example.spendsprout_opsc.ExpenseType.Expense }
                    .sumOf { it.expenseAmount }
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
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val expenses = BudgetApp.db.expenseDao().getBetweenDates(startDate, endDate)
                    .filter { it.expenseType == com.example.spendsprout_opsc.ExpenseType.Expense }

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
                val totals = BudgetApp.db.expenseDao().totalsByCategory(startDate, endDate)
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

