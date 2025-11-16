package com.example.spendsprout_opsc.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.ExpenseType
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

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
            
            // Load recent transactions
            loadRecentTransactions()
            
            // Load top 3 categories
            loadTopCategories()
            
            // Load chart data
            loadChartData()
        }
    }
    
    private fun loadRecentTransactions() {
        viewModelScope.launch {
            try {
                val expenses = withContext(Dispatchers.IO) {
                    BudgetApp.db.expenseDao().getAll()
                        .sortedByDescending { it.expenseDate }
                        .take(5)
                }
                
                val transactions = expenses.map { expense ->
                    Transaction(
                        date = formatDate(expense.expenseDate),
                        description = expense.expenseName,
                        amount = formatAmount(expense.expenseAmount, if (expense.expenseType == ExpenseType.Expense) "Expense" else "Income"),
                        color = getCategoryColor(expense.expenseCategory)
                    )
                }
                
                _recentTransactions.value = transactions
                android.util.Log.d("OverviewViewModel", "Loaded ${transactions.size} recent transactions")
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading recent transactions: ${e.message}", e)
                _recentTransactions.value = emptyList()
            }
        }
    }
    
    private fun loadTopCategories() {
        viewModelScope.launch {
            try {
                val categories = withContext(Dispatchers.IO) {
                    BudgetApp.db.categoryDao().getAll().first()
                }
                
                // Calculate spent amount for each category from actual expenses and take top 3
                val categorySummaries = withContext(Dispatchers.IO) {
                    val expenses = BudgetApp.db.expenseDao().getAll()
                    
                    categories.map { category ->
                        // Get subcategories for this category
                        val subcategories = BudgetApp.db.subcategoryDao().getByCategoryId(category.id.toLong())
                        val subcategoryNames = subcategories.map { it.subcategoryName }.toSet()
                        
                        // Calculate category spent from expenses
                        // Expenses can be tagged with either category name or subcategory name
                        val categoryExpenses = expenses.filter { expense ->
                            expense.expenseCategory == category.categoryName || 
                            subcategoryNames.contains(expense.expenseCategory)
                        }
                        val categorySpent = categoryExpenses.sumOf { expense ->
                            if (expense.expenseType == ExpenseType.Expense) {
                                expense.expenseAmount  // Expenses are positive spending
                            } else {
                                0.0  // Ignore income for spending calculation
                            }
                        }
                        
                        // Calculate total allocation from subcategories
                        val categoryAllocation = subcategories.sumOf { it.subcategoryAllocation }
                        
                        CategorySummary(
                            name = category.categoryName,
                            spent = "R ${String.format("%.0f", categorySpent)}",
                            allocated = "R ${String.format("%.0f", categoryAllocation)}",
                            color = getColorFromInt(category.categoryColor)
                        )
                    }
                }
                    .sortedByDescending { catSummary -> parseMoneyToDouble(catSummary.spent) }
                    .take(3)
                
                _categorySummary.value = categorySummaries
                android.util.Log.d("OverviewViewModel", "Loaded ${categorySummaries.size} top categories")
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading top categories: ${e.message}", e)
                _categorySummary.value = emptyList()
            }
        }
    }
    
    private fun loadChartData() {
        viewModelScope.launch {
            try {
                val expenses = withContext(Dispatchers.IO) {
                    BudgetApp.db.expenseDao().getAll()
                }
                
                // Group expenses by month
                val calendar = Calendar.getInstance()
                val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val expensesByMonth = expenses.groupBy { expense ->
                    calendar.timeInMillis = expense.expenseDate
                    monthFormat.format(Calendar.getInstance().apply { timeInMillis = expense.expenseDate }.time)
                }
                
                // Calculate revenue (income) and target (expenses) for each month
                val chartData = expensesByMonth.map { (month, monthExpenses) ->
                    val revenue = monthExpenses
                        .filter { it.expenseType == ExpenseType.Income }
                        .sumOf { it.expenseAmount }
                    val target = monthExpenses
                        .filter { it.expenseType == ExpenseType.Expense }
                        .sumOf { it.expenseAmount }
                    
                    ChartDataPoint(month, revenue, target)
                }
                    .sortedBy { it.month }
                    .takeLast(6) // Last 6 months
                
                _chartData.value = chartData
                android.util.Log.d("OverviewViewModel", "Loaded chart data for ${chartData.size} months")
            } catch (e: Exception) {
                android.util.Log.e("OverviewViewModel", "Error loading chart data: ${e.message}", e)
                _chartData.value = emptyList()
            }
        }
    }
    
    private fun parseMoneyToDouble(moneyString: String): Double {
        return try {
            moneyString.replace("R", "").replace(",", "").replace(" ", "").trim().toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
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
    
    private suspend fun getCategoryColor(categoryName: String): String {
        // Get color from category in database or use default
        return when (categoryName.lowercase()) {
            "needs" -> "#BD804A"
            "wants" -> "#88618E"
            "savings" -> "#6EA19E"
            else -> {
                // Try to get from database
                try {
                    val categories = withContext(Dispatchers.IO) {
                        BudgetApp.db.categoryDao().getAll().first()
                    }
                    val category = categories.find { cat ->
                        cat.categoryName.equals(categoryName, ignoreCase = true)
                    }
                    if (category != null) {
                        getColorFromInt(category.categoryColor)
                    } else {
                        "#D3D3D3"
                    }
                } catch (e: Exception) {
                    "#D3D3D3"
                }
            }
        }
    }
    
    private fun getColorFromInt(colorInt: Int): String {
        return String.format("#%06X", 0xFFFFFF and colorInt)
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


