package com.example.spendsprout_opsc.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.firebase.FirebaseRepositoryProvider
import com.example.spendsprout_opsc.model.toAccountSummary
import com.example.spendsprout_opsc.model.toCategorySummary
import com.example.spendsprout_opsc.model.toOverviewTransaction
import com.example.spendsprout_opsc.overview.model.Transaction
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.AccountSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {
    
    private val accountRepository = FirebaseRepositoryProvider.accountRepository
    private val categoryRepository = FirebaseRepositoryProvider.categoryRepository
    private val transactionRepository = FirebaseRepositoryProvider.transactionRepository
    
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
            try {
                // Load accounts
                val accounts = accountRepository.getAllAccounts().first()
                _accountSummary.value = accounts.map { it.toAccountSummary() }
                _totalBalance.value = accountRepository.getTotalBalance()
                
                // Load categories
                val categories = categoryRepository.getAllCategories().first()
                _categorySummary.value = categories.map { it.toCategorySummary() }
                
                // Load recent transactions
                val recentTransactionsFlow = transactionRepository.getRecentTransactions(5)
                val recentTransactionsList = recentTransactionsFlow.first()
                _recentTransactions.value = recentTransactionsList.map { it.toOverviewTransaction() }
                
                // Load chart data
                _chartData.value = generateMockChartData()
                
                android.util.Log.d("OverviewViewModel", "Loaded ${accounts.size} accounts, ${categories.size} categories, ${recentTransactionsList.size} transactions")
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading data: ${e.message}", e)
            }
        }
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
    
    suspend fun getTotalAllocation(): Double {
        return try {
            categoryRepository.getTotalAllocation()
        } catch (e: Exception) {
            0.0
        }
    }
    
    fun getTotalBalance(): Double {
        return _totalBalance.value
    }
    
    fun refreshData() {
        loadData()
    }
    
    fun getRecentTransactions(): List<Transaction> {
        return _recentTransactions.value
    }
    
    fun getCategories(): List<CategorySummary> {
        return _categorySummary.value
    }
    
    fun getChartData(): List<ChartDataPoint> {
        return _chartData.value
    }
    
    // Budget-related stubs (not yet migrated to Firebase)
    val budgets: StateFlow<List<com.example.spendsprout_opsc.roomdb.Budget_Entity>> = MutableStateFlow(emptyList())
    
    private val _currentBudget = MutableStateFlow<com.example.spendsprout_opsc.roomdb.Budget_Entity?>(null)
    val currentBudget: StateFlow<com.example.spendsprout_opsc.roomdb.Budget_Entity?> = _currentBudget.asStateFlow()
    
    fun getCurrentBudget(): com.example.spendsprout_opsc.roomdb.Budget_Entity? {
        return _currentBudget.value
    }
    
    fun refreshCurrentBudget() {
        // TODO: Implement Firebase budget repository
        _currentBudget.value = null
    }
}
