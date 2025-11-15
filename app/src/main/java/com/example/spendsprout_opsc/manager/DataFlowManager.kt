/*
package com.example.spendsprout_opsc.manager

import com.example.spendsprout_opsc.service.DataService
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.wants.model.Subcategory
import com.example.spendsprout_opsc.transactions.model.Transaction
import com.example.spendsprout_opsc.overview.model.AccountSummary
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.Transaction as OverviewTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataFlowManager - Essential Data Flow Controller
 * 
 * This manager coordinates data flow between Room database and UI components,
 * providing a centralized interface for all data operations in the application.
 */
@Singleton
class DataFlowManager @Inject constructor(
    private val dataService: DataService
) {

    // ==================== ACCOUNT DATA FLOW ====================
    
    /**
     * Get all accounts for AccountsActivity
     */
    fun getAccountsForAccountsScreen(): Flow<List<Account>> {
        return dataService.getAllAccounts()
    }

    /**
     * Get account summaries for OverviewActivity
     */
    fun getAccountSummariesForOverview(): Flow<List<AccountSummary>> {
        return dataService.getAccountSummaries()
    }

    /**
     * Get total balance for OverviewActivity
     */
    suspend fun getTotalBalanceForOverview(): Double {
        return dataService.getTotalBalance()
    }

    /**
     * Create new account from EditAccountActivity
     */
    suspend fun createAccount(account: Account) {
        dataService.insertAccount(account)
    }

    /**
     * Update account from EditAccountActivity
     */
    suspend fun updateAccount(account: Account) {
        dataService.updateAccount(account)
    }

    /**
     * Delete account from AccountsActivity
     */
    suspend fun deleteAccount(account: Account) {
        dataService.deleteAccount(account)
    }

    // ==================== CATEGORY DATA FLOW ====================
    
    /**
     * Get all categories for CategoriesActivity
     */
    fun getCategoriesForCategoriesScreen(): Flow<List<Category>> {
        return dataService.getAllCategories()
    }

    /**
     * Get category summaries for OverviewActivity
     */
    fun getCategorySummariesForOverview(): Flow<List<CategorySummary>> {
        return dataService.getCategorySummaries()
    }

    /**
     * Get total allocation for ReportsActivity
     */
    suspend fun getTotalAllocationForReports(): Double {
        return dataService.getTotalAllocation()
    }

    /**
     * Get total spent for ReportsActivity
     */
    suspend fun getTotalSpentForReports(): Double {
        return dataService.getTotalSpent()
    }

    /**
     * Create new category from EditCategoryActivity
     */
    suspend fun createCategory(category: Category) {
        dataService.insertCategory(category)
    }

    /**
     * Update category from EditCategoryActivity
     */
    suspend fun updateCategory(category: Category) {
        dataService.updateCategory(category)
    }

    /**
     * Delete category from CategoriesActivity
     */
    suspend fun deleteCategory(category: Category) {
        dataService.deleteCategory(category)
    }

    // ==================== SUBCATEGORY DATA FLOW ====================
    
    /**
     * Get all subcategories for WantsCategoryActivity
     */
    fun getSubcategoriesForWantsScreen(): Flow<List<Subcategory>> {
        return dataService.getAllSubcategories()
    }

    /**
     * Get subcategories by category for CategoriesActivity
     */
    fun getSubcategoriesByCategory(categoryId: Int): Flow<List<Subcategory>> {
        return dataService.getSubcategoriesByCategory(categoryId)
    }

    /**
     * Create new subcategory from EditCategoryActivity
     */
    suspend fun createSubcategory(subcategory: Subcategory) {
        dataService.insertSubcategory(subcategory)
    }

    /**
     * Update subcategory from EditCategoryActivity
     */
    suspend fun updateSubcategory(subcategory: Subcategory) {
        dataService.updateSubcategory(subcategory)
    }

    /**
     * Delete subcategory from WantsCategoryActivity
     */
    suspend fun deleteSubcategory(subcategory: Subcategory) {
        dataService.deleteSubcategory(subcategory)
    }

    // ==================== TRANSACTION DATA FLOW ====================
    
    /**
     * Get all transactions for TransactionsActivity
     */
    fun getTransactionsForTransactionsScreen(): Flow<List<Transaction>> {
        return dataService.getAllTransactions()
    }

    /**
     * Get recent transactions for OverviewActivity
     */
    fun getRecentTransactionsForOverview(limit: Int = 5): Flow<List<OverviewTransaction>> {
        return dataService.getRecentTransactions(limit)
    }

    /**
     * Get total income for ReportsActivity
     */
    suspend fun getTotalIncomeForReports(): Double {
        return dataService.getTotalIncome()
    }

    /**
     * Get total expenses for ReportsActivity
     */
    suspend fun getTotalExpensesForReports(): Double {
        return dataService.getTotalExpenses()
    }

    /**
     * Create new transaction from EditTransactionActivity
     */
    suspend fun createTransaction(transaction: Transaction) {
        dataService.insertTransaction(transaction)
    }

    /**
     * Update transaction from EditTransactionActivity
     */
    suspend fun updateTransaction(transaction: Transaction) {
        dataService.updateTransaction(transaction)
    }

    /**
     * Delete transaction from TransactionsActivity
     */
    suspend fun deleteTransaction(transaction: Transaction) {
        dataService.deleteTransaction(transaction)
    }

    // ==================== DASHBOARD DATA FLOW ====================
    
    /**
     * Get complete dashboard data for OverviewActivity
     */
    fun getDashboardDataForOverview(): Flow<com.example.spendsprout_opsc.service.DashboardData> {
        return dataService.getDashboardData()
    }

    // ==================== DATA VALIDATION ====================
    
    /**
     * Validate account data before saving
     */
    fun validateAccount(account: Account): Boolean {
        return account.name.isNotBlank() && account.balance >= 0
    }

    /**
     * Validate category data before saving
     */
    fun validateCategory(category: Category): Boolean {
        return category.name.isNotBlank()
    }

    /**
     * Validate subcategory data before saving
     */
    fun validateSubcategory(subcategory: Subcategory): Boolean {
        return subcategory.name.isNotBlank()
    }

    /**
     * Validate transaction data before saving
     */
    fun validateTransaction(transaction: Transaction): Boolean {
        return transaction.description.isNotBlank()
    }
}
*/