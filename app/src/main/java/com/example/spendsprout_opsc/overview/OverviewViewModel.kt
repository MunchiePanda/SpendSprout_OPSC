package com.example.spendsprout_opsc.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Account
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.SpendingType
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {

    data class CategorySpend(
        val categoryName: String,
        val amount: Double,
        val spendingType: SpendingType
    )

    data class OverviewUiState(
        val totalBalance: Double = 0.0,
        val totalIncome: Double = 0.0,
        val totalExpenses: Double = 0.0,
        val accounts: List<Account> = emptyList(),
        val transactions: List<Transaction> = emptyList(),
        val categorySpends: List<CategorySpend> = emptyList(),
        val spendingByType: Map<SpendingType, Double> = emptyMap()
    )

    private val _accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _transactions: StateFlow<List<Transaction>> = transactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<OverviewUiState> = combine(
        _accounts,
        _transactions,
        _categories
    ) { accounts, transactions, categories ->

        val totalBalance = accounts.sumOf { it.accountBalance }
        val totalIncome = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val totalExpenses = transactions.filter { it.amount < 0 }.sumOf { it.amount }

        val categoryIdToCategoryMap = categories.associateBy { it.categoryId }

        val categorySpends = transactions
            .filter { it.amount < 0 }
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, transactionList) ->
                val category = categoryIdToCategoryMap[categoryId]
                if (category != null) {
                    CategorySpend(
                        categoryName = category.name,
                        amount = transactionList.sumOf { kotlin.math.abs(it.amount) },
                        spendingType = category.spendingType
                    )
                } else {
                    null
                }
            }

        val spendingByType = categorySpends.groupBy { it.spendingType }
            .mapValues { (_, spends) -> spends.sumOf { it.amount } }

        OverviewUiState(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            accounts = accounts,
            transactions = transactions.take(5),
            categorySpends = categorySpends,
            spendingByType = spendingByType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverviewUiState())
}
