package com.example.spendsprout_opsc.overview

import androidx.lifecycle.ViewModel
import com.example.spendsprout_opsc.model.Account
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // A data class to hold all the calculated summary data for the overview screen
    data class OverviewUiState(
        val totalBalance: Double = 0.0,
        val totalIncome: Double = 0.0,
        val totalExpenses: Double = 0.0,
        val accounts: List<Account> = emptyList(),
        val transactions: List<Transaction> = emptyList(),
        val categorySpends: Map<String, Double> = emptyMap()
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
        val totalIncome = transactions.filter { it.transactionAmount > 0 }.sumOf { it.transactionAmount }
        val totalExpenses = transactions.filter { it.transactionAmount < 0 }.sumOf { it.transactionAmount }

        // Create a map of CategoryID to its name for easy lookup
        val categoryIdToNameMap = categories.associateBy({ it.categoryId }, { it.name })

        // Calculate spending per category
        val categorySpends = transactions
            .filter { it.transactionAmount < 0 } // Only consider expenses
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, transactionList) ->
                // Use the map to find the category name
                val categoryName = categoryIdToNameMap[categoryId]
                if (categoryName != null) {
                    // We sum the absolute value of the expenses
                    categoryName to transactionList.sumOf { kotlin.math.abs(it.transactionAmount) }
                } else {
                    null // Ignore transactions with no matching category
                }
            }
            .toMap()

        // Emit the final state object
        OverviewUiState(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            accounts = accounts,
            transactions = transactions.take(5), // Show the 5 most recent transactions
            categorySpends = categorySpends
        )
    }
}
