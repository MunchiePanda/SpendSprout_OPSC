package com.example.spendsprout_opsc.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // Represents the UI state for the reports screen.
    data class ReportsUiState(
        val totalSpentThisMonth: Double = 0.0,
        val dailySpendSeries: List<ChartDataPoint> = emptyList(),
        val categoryTotals: Map<String, Double> = emptyMap()
    )

    // A StateFlow that holds the current UI state.
    val uiState: StateFlow<ReportsUiState> = transactionRepository.getAllTransactions()
        .map { transactions ->
            val (startOfMonth, endOfMonth) = getMonthDateRange()

            val monthlyTransactions = transactions.filter { it.date in startOfMonth..endOfMonth }

            val totalSpent = monthlyTransactions
                .filter { it.transactionAmount < 0 }
                .sumOf { kotlin.math.abs(it.transactionAmount) }

            val dailySpend = calculateDailySpend(monthlyTransactions, startOfMonth, endOfMonth)
            val categoryTotals = calculateCategoryTotals(monthlyTransactions)

            ReportsUiState(
                totalSpentThisMonth = totalSpent,
                dailySpendSeries = dailySpend,
                categoryTotals = categoryTotals
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportsUiState()
        )

    private fun calculateDailySpend(transactions: List<Transaction>, start: Long, end: Long): List<ChartDataPoint> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dailyMap = transactions
            .filter { it.transactionAmount < 0 }
            .groupBy { sdf.format(Date(it.date)) }
            .mapValues { (_, trans) -> trans.sumOf { kotlin.math.abs(it.transactionAmount) } }

        val days = getDaysInRange(start, end)
        return days.map { dayMillis ->
            val key = sdf.format(Date(dayMillis))
            ChartDataPoint(key, dailyMap[key] ?: 0.0, 0.0) // Target is not used here
        }
    }

    private fun calculateCategoryTotals(transactions: List<Transaction>): Map<String, Double> {
        return transactions
            .filter { it.transactionAmount < 0 }
            .groupBy { it.categoryName } // Assuming Transaction has categoryName
            .mapValues { (_, trans) -> trans.sumOf { kotlin.math.abs(it.transactionAmount) } }
    }

    private fun getMonthDateRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        // ... set other fields to 0
        val start = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        // ... set other fields to max
        val end = calendar.timeInMillis
        return start to end
    }

    private fun getDaysInRange(start: Long, end: Long): List<Long> {
        val days = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = start
        while (cal.timeInMillis <= end) {
            days.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
}

data class ChartDataPoint(val month: String, val revenue: Double, val target: Double)
