package com.example.spendsprout_opsc.database

import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.SubcategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseValidator @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    
    fun validateDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Validate that all repositories can access data
                val accountCount = accountRepository.getAllAccounts()
                val categoryCount = categoryRepository.getAllCategories()
                val subcategoryCount = subcategoryRepository.getAllSubcategories()
                val transactionCount = transactionRepository.getAllTransactions()
                
                // Log validation results
                android.util.Log.d("DatabaseValidator", "Database validation completed successfully")
                android.util.Log.d("DatabaseValidator", "Accounts: $accountCount")
                android.util.Log.d("DatabaseValidator", "Categories: $categoryCount")
                android.util.Log.d("DatabaseValidator", "Subcategories: $subcategoryCount")
                android.util.Log.d("DatabaseValidator", "Transactions: $transactionCount")
                
            } catch (e: Exception) {
                android.util.Log.e("DatabaseValidator", "Database validation failed", e)
                DatabaseErrorHandler.handleDatabaseError(e, "Database validation")
            }
        }
    }
    
    suspend fun isDatabaseHealthy(): Boolean {
        return try {
            // Test basic database operations
            accountRepository.getTotalBalance()
            categoryRepository.getTotalAllocation()
            transactionRepository.getTotalIncome()
            true
        } catch (e: Exception) {
            android.util.Log.e("DatabaseValidator", "Database health check failed", e)
            false
        }
    }
}
