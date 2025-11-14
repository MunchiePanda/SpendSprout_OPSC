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
import com.example.spendsprout_opsc.roomdb.Budget_Entity
// Removed old Payment_Entity import; using DataService via DataFlow for data
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
    
    // Budget data
    private val _budgets = MutableStateFlow<List<Budget_Entity>>(emptyList())
    val budgets: StateFlow<List<Budget_Entity>> = _budgets.asStateFlow()
    
    private val _currentBudget = MutableStateFlow<Budget_Entity?>(null)
    val currentBudget: StateFlow<Budget_Entity?> = _currentBudget.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Load real data from database
            loadBudgets()
            loadRecentTransactions()
            loadCategorySummary()
            loadAccountSummary()
            loadChartData()
        }
    }

    private fun loadRecentTransactions() {
        viewModelScope.launch {
            try {
                val transactionRepository = TransactionRepository()
                transactionRepository.getAllTransactions().collect { transactions ->
                    _recentTransactions.value = transactions.map { transaction ->
                        Transaction(
                            date = formatDate(transaction.date),
                            description = transaction.description,
                            amount = formatAmount(transaction.amount, transaction.type),
                            color = getCategoryColor(transaction.subcategoryId)
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading transactions: ${e.message}", e)
            }
        }
    }

    private fun loadCategorySummary() {
        viewModelScope.launch {
            try {
                val categoryRepository = CategoryRepository()
                categoryRepository.getAllCategories().collect { categories ->
                    _categorySummary.value = categories.map { category ->
                        CategorySummary(
                            name = category.name,
                            spent = "R ${category.spent}",
                            allocated = "R ${category.allocation}",
                            color = category.color
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading categories: ${e.message}", e)
            }
        }
    }

    private fun loadAccountSummary() {
        viewModelScope.launch {
            try {
                val accountRepository = AccountRepository()
                accountRepository.getAllAccounts().collect { accounts ->
                    _accountSummary.value = accounts.map { account ->
                        AccountSummary(
                            name = account.name,
                            balance = "R ${account.balance}",
                            limit = "R ${account.limit}"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading accounts: ${e.message}", e)
            }
        }
    }

    private fun loadChartData() {
        viewModelScope.launch {
            try {
                // Load real chart data from database
                // For now, use mock data until real data is available
                _chartData.value = generateMockChartData()
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading chart data: ${e.message}", e)
            }
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    private fun formatAmount(amount: Double, type: String): String {
        val sign = if (type == "Expense") "-" else "+"
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
    
    // Database loading methods
    private fun loadBudgets() {
        viewModelScope.launch {
            try {
                val budgetFlow = com.example.spendsprout_opsc.BudgetApp.db.budgetDao().getAll()
                budgetFlow.collect { budgetList ->
                    _budgets.value = budgetList
                    
                    // Update current budget if it exists in the new list, or set first if none selected
                    val currentBudget = _currentBudget.value
                    if (currentBudget != null) {
                        // Find the updated budget with the same ID
                        val updatedBudget = budgetList.find { it.id == currentBudget.id }
                        if (updatedBudget != null) {
                            _currentBudget.value = updatedBudget
                            android.util.Log.d("OverviewViewModel", "Updated current budget: ${updatedBudget.budgetName}")
                        }
                    } else if (budgetList.isNotEmpty()) {
                        // Set the first budget as current if none is selected
                        _currentBudget.value = budgetList.first()
                        android.util.Log.d("OverviewViewModel", "Set first budget as current: ${budgetList.first().budgetName}")
                    }
                    
                    // Update total balance with real budget data
                    _totalBalance.value = getTotalBalance()
                    
                    android.util.Log.d("OverviewViewModel", "Loaded ${budgetList.size} budgets, total balance: ${_totalBalance.value}")
                }
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading budgets: ${e.message}", e)
            }
        }
    }
    
    
    fun getCurrentBudget(): Budget_Entity? {
        return _currentBudget.value
    }
    
    fun getTotalAllocation(): Double {
        return _budgets.value.sumOf { it.openingBalance }
    }
    
    fun getTotalBalance(): Double {
        return _budgets.value.sumOf { it.budgetBalance }
    }
    
    fun getTotalMinGoal(): Double {
        return _budgets.value.sumOf { it.budgetMinGoal }
    }
    
    fun getTotalMaxGoal(): Double {
        return _budgets.value.sumOf { it.budgetMaxGoal }
    }
    
    fun refreshData() {
        loadData()
    }
    
    fun refreshCurrentBudget() {
        viewModelScope.launch {
            try {
                val currentBudget = _currentBudget.value
                if (currentBudget != null) {
                    val updatedBudget = com.example.spendsprout_opsc.BudgetApp.db.budgetDao().getById(currentBudget.id)
                    if (updatedBudget != null) {
                        _currentBudget.value = updatedBudget
                        android.util.Log.d("OverviewViewModel", "Refreshed current budget: ${updatedBudget.budgetName}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error refreshing current budget: ${e.message}", e)
            }
        }
    }
    
    // Methods that were referenced in the commented code in OverviewActivity
    fun getRecentTransactions(): List<Transaction> {
        return _recentTransactions.value
    }
    
    fun getCategories(): List<CategorySummary> {
        return _categorySummary.value
    }
    
    fun getChartData(): List<ChartDataPoint> {
        return _chartData.value
    }
    
}


