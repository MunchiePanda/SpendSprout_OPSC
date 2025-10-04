package com.example.spendsprout_opsc.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.overview.model.Transaction
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.AccountSummary
import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import com.example.spendsprout_opsc.roomdb.Account_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import com.example.spendsprout_opsc.roomdb.Payment_Entity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {
    
    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    
    private val _recentTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val recentTransactions: StateFlow<List<Transaction>> = _recentTransactions.asStateFlow()
    
    private val _categorySummary = MutableStateFlow<List<CategorySummary>>(emptyList())
    val categorySummary: StateFlow<List<CategorySummary>> = _categorySummary.asStateFlow()
    
    private val _accountSummary = MutableStateFlow<List<AccountSummary>>(emptyList())
    val accountSummary: StateFlow<List<AccountSummary>> = _accountSummary.asStateFlow()
    
    private val _chartData = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val chartData: StateFlow<List<ChartDataPoint>> = _chartData.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Mock data for now - will be replaced with real database once build environment is fixed
            _totalBalance.value = 1880.0

            // Mock recent transactions
            _recentTransactions.value = listOf(
                Transaction(
                    date = "02 October 2025",
                    description = "Petrol",
                    amount = "- R 1,500",
                    color = "#87CEEB"
                ),
                Transaction(
                    date = "30 September 2025",
                    description = "Mug 'n Bean",
                    amount = "- R 360",
                    color = "#4169E1"
                ),
                Transaction(
                    date = "25 September 2025",
                    description = "Salary",
                    amount = "+ R 20,000",
                    color = "#32CD32"
                )
            )

            // Mock category summary
            _categorySummary.value = listOf(
                CategorySummary(
                    name = "Needs",
                    spent = "R 8,900",
                    allocated = "R 10,000",
                    color = "#BD804A"
                ),
                CategorySummary(
                    name = "Wants",
                    spent = "R 120",
                    allocated = "R 6,000",
                    color = "#88618E"
                ),
                CategorySummary(
                    name = "Savings",
                    spent = "R 4,000",
                    allocated = "R 4,000",
                    color = "#6EA19E"
                )
            )

            // Mock account summary
            _accountSummary.value = listOf(
                AccountSummary(
                    name = "Cash",
                    balance = "R 160",
                    limit = "R 240"
                ),
                AccountSummary(
                    name = "FNB Next Transact",
                    balance = "R 1,720",
                    limit = "R 2,580"
                )
            )

            // Load chart data (mock for now)
            _chartData.value = generateMockChartData()
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    private fun formatAmount(amount: Double, type: String): String {
        val sign = if (type == "Income") "+" else "-"
        return "$sign R ${String.format("%.0f", amount)}"
    }
    
    private fun getCategoryColor(subcategoryId: Int): String {
        // Mock color based on subcategory ID
        val colors = listOf("#FF6B6B", "#FFB6C1", "#9370DB", "#4ECDC4", "#45B7D1")
        return colors[subcategoryId % colors.size]
    }
    
    private fun getColorFromInt(colorInt: Int): String {
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }
    
    private fun generateMockChartData(): List<ChartDataPoint> {
        return listOf(
            ChartDataPoint("2025-01", 15000.0, 12000.0),
            ChartDataPoint("2025-02", 18000.0, 15000.0),
            ChartDataPoint("2025-03", 20000.0, 18000.0),
            ChartDataPoint("2025-04", 22000.0, 20000.0),
            ChartDataPoint("2025-05", 25000.0, 22000.0),
            ChartDataPoint("2025-06", 28000.0, 25000.0)
        )
    }
}


