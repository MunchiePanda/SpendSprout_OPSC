package com.example.spendsprout_opsc.overview

import androidx.lifecycle.ViewModel
import com.example.spendsprout_opsc.model.Account
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.SpendingType
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    data class CategorySpend(
        val categoryName: String,
        val amount: Double,
        val spendingType: SpendingType
    )

    // A data class to hold all the calculated summary data for the overview screen
    data class OverviewUiState(
        val totalBalance: Double = 0.0,
        val totalIncome: Double = 0.0,
        val totalExpenses: Double = 0.0,
        val accounts: List<Account> = emptyList(),
        val transactions: List<Transaction> = emptyList(),
        val categorySpends: List<CategorySpend> = emptyList(),
        val spendingByType: Map<SpendingType, Double> = emptyMap()
    )

    // This is a powerful flow that combines data from all three repositories.
    // Whenever any transaction, account, or category changes in Firebase, this will automatically
    // recalculate and emit a new, updated UiState for your screen.
    val uiState: Flow<OverviewUiState> = combine(
        accountRepository.getAllAccounts(),
        transactionRepository.getAllTransactions(),
        categoryRepository.getAllCategories()
    ) { accounts, transactions, categories ->

        val totalBalance = accounts.sumOf { it.accountBalance }
        val totalIncome = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val totalExpenses = transactions.filter { it.amount < 0 }.sumOf { it.amount }

        // Create a map of CategoryID to its object for easy lookup
        val categoryIdToCategoryMap = categories.associateBy { it.categoryId }

        // Calculate spending per category
        val categorySpends = transactions
            .filter { it.amount < 0 } // Only consider expenses
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
                    null // Ignore transactions with no matching category
                }
            }

        val spendingByType = categorySpends.groupBy { it.spendingType }
            .mapValues { (_, spends) -> spends.sumOf { it.amount } }

        // Emit the final state object
        OverviewUiState(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            accounts = accounts,
            transactions = transactions.take(5), // Show the 5 most recent transactions
            categorySpends = categorySpends,
            spendingByType = spendingByType
        )
    }.flowOn(Dispatchers.IO)
}
