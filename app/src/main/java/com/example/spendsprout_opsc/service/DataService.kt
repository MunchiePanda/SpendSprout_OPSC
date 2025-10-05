package com.example.spendsprout_opsc.service

import com.example.spendsprout_opsc.model.*
import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.SubcategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.wants.model.Subcategory
import com.example.spendsprout_opsc.transactions.model.Transaction
import com.example.spendsprout_opsc.overview.model.AccountSummary
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.Transaction as OverviewTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataService - Essential Data Flow Service
 * 
 * This service connects Room database operations to application logic
 * and provides a clean interface for data operations across the app.
 */
@Singleton
class DataService @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val transactionRepository: TransactionRepository
) {

    // ==================== ACCOUNT OPERATIONS ====================
    
    fun getAllAccounts(): Flow<List<Account>> {
        return accountRepository.getAllAccounts().map { entities ->
            entities.map { it.toAccount() }
        }
    }

    fun getAccountById(id: Int): Flow<Account?> {
        return accountRepository.getAllAccounts().map { entities ->
            entities.find { it.id == id }?.toAccount()
        }
    }

    suspend fun insertAccount(account: Account) {
        accountRepository.insertAccount(account.toAccountEntity())
    }

    suspend fun updateAccount(account: Account) {
        accountRepository.updateAccount(account.toAccountEntity())
    }

    suspend fun deleteAccount(account: Account) {
        accountRepository.deleteAccount(account.toAccountEntity())
    }

    fun getAccountSummaries(): Flow<List<AccountSummary>> {
        return accountRepository.getAllAccounts().map { entities ->
            entities.map { it.toAccountSummary() }
        }
    }

    suspend fun getTotalBalance(): Double {
        return accountRepository.getTotalBalance()
    }

    // ==================== CATEGORY OPERATIONS ====================
    
    fun getAllCategories(): Flow<List<Category>> {
        return categoryRepository.getAllCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    fun getCategoryById(id: Int): Flow<Category?> {
        return categoryRepository.getAllCategories().map { entities ->
            entities.find { it.id == id }?.toCategory()
        }
    }

    suspend fun insertCategory(category: Category) {
        categoryRepository.insertCategory(category.toCategoryEntity())
    }

    suspend fun updateCategory(category: Category) {
        categoryRepository.updateCategory(category.toCategoryEntity())
    }

    suspend fun deleteCategory(category: Category) {
        categoryRepository.deleteCategory(category.toCategoryEntity())
    }

    fun getCategorySummaries(): Flow<List<CategorySummary>> {
        return categoryRepository.getAllCategories().map { entities ->
            entities.map { it.toCategorySummary() }
        }
    }

    suspend fun getTotalAllocation(): Double {
        return categoryRepository.getTotalAllocation()
    }

    suspend fun getTotalSpent(): Double {
        return categoryRepository.getTotalSpent()
    }

    // ==================== SUBCATEGORY OPERATIONS ====================
    
    fun getAllSubcategories(): Flow<List<Subcategory>> {
        return subcategoryRepository.getAllSubcategories().map { entities ->
            entities.map { it.toSubcategory() }
        }
    }

    fun getSubcategoryById(id: Int): Flow<Subcategory?> {
        return subcategoryRepository.getAllSubcategories().map { entities ->
            entities.find { it.id == id }?.toSubcategory()
        }
    }

    fun getSubcategoriesByCategory(categoryId: Int): Flow<List<Subcategory>> {
        return subcategoryRepository.getAllSubcategories().map { entities ->
            entities.filter { it.categoryId == categoryId }.map { it.toSubcategory() }
        }
    }

    suspend fun insertSubcategory(subcategory: Subcategory) {
        subcategoryRepository.insertSubcategory(subcategory.toSubcategoryEntity())
    }

    suspend fun updateSubcategory(subcategory: Subcategory) {
        subcategoryRepository.updateSubcategory(subcategory.toSubcategoryEntity())
    }

    suspend fun deleteSubcategory(subcategory: Subcategory) {
        subcategoryRepository.deleteSubcategory(subcategory.toSubcategoryEntity())
    }

    // ==================== TRANSACTION OPERATIONS ====================
    
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactions().map { entities ->
            entities.map { it.toUiTransaction() }
        }
    }

    fun getTransactionById(id: Int): Flow<Transaction?> {
        return transactionRepository.getAllTransactions().map { entities ->
            entities.find { it.id.toString() == id.toString() }?.toUiTransaction()
        }
    }

    fun getRecentTransactions(limit: Int): Flow<List<OverviewTransaction>> {
        return transactionRepository.getRecentTransactions(limit).map { entities ->
            entities.map { it.toOverviewTransaction() }
        }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction.toExpenseEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionRepository.updateTransaction(transaction.toExpenseEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionRepository.deleteTransaction(transaction.toExpenseEntity())
    }

    suspend fun getTotalIncome(): Double {
        return transactionRepository.getTotalIncome()
    }

    suspend fun getTotalExpenses(): Double {
        return transactionRepository.getTotalExpenses()
    }

    // ==================== DASHBOARD DATA ====================
    
    fun getDashboardData(): Flow<DashboardData> {
        return kotlinx.coroutines.flow.combine(
            getAccountSummaries(),
            getCategorySummaries(),
            getRecentTransactions(5)
        ) { accounts, categories, transactions ->
            DashboardData(
                accounts = accounts,
                categories = categories,
                recentTransactions = transactions
            )
        }
    }
}

/**
 * Dashboard Data Model
 */
data class DashboardData(
    val accounts: List<AccountSummary>,
    val categories: List<CategorySummary>,
    val recentTransactions: List<OverviewTransaction>
)
