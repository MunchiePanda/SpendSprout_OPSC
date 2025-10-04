package com.example.spendsprout_opsc.database

import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.TransactionType
import com.example.spendsprout_opsc.roomdb.Account_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import com.example.spendsprout_opsc.roomdb.Payment_Entity
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
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
class DatabaseInitializer @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    
    fun initializeDatabase() {
        DatabaseErrorHandler.safeDatabaseOperation(
            CoroutineScope(Dispatchers.IO),
            {
                // Initialize categories
                initializeCategories()
                
                // Initialize subcategories
                initializeSubcategories()
                
                // Initialize accounts
                initializeAccounts()
                
                // Initialize transactions
                initializeTransactions()
            }
        )
    }
    
    private suspend fun initializeCategories() {
        val categories = listOf(
            Category_Entity(1, "Needs", 0xFFBD804A.toInt(), 8900.0, 10000.0, "Essential expenses"),
            Category_Entity(2, "Wants", 0xFF88618E.toInt(), 120.0, 6000.0, "Non-essential expenses"),
            Category_Entity(3, "Savings", 0xFF6EA19E.toInt(), 4000.0, 4000.0, "Savings and investments")
        )
        
        categories.forEach { category ->
            categoryRepository.insertCategory(category)
        }
    }
    
    private suspend fun initializeSubcategories() {
        val subcategories = listOf(
            Subcategory_Entity(1, 1, "Groceries", 0xFF4ECDC4.toInt(), 3000.0, 4000.0, "Food and household items"),
            Subcategory_Entity(2, 1, "Transport", 0xFF45B7D1.toInt(), 2500.0, 3000.0, "Petrol and public transport"),
            Subcategory_Entity(3, 1, "Utilities", 0xFF96CEB4.toInt(), 2000.0, 2000.0, "Electricity, water, internet"),
            Subcategory_Entity(4, 1, "Rent", 0xFFFECA57.toInt(), 1400.0, 1000.0, "Monthly rent"),
            Subcategory_Entity(5, 2, "Entertainment", 0xFFFF6B6B.toInt(), 80.0, 2000.0, "Movies, games, hobbies"),
            Subcategory_Entity(6, 2, "Dining Out", 0xFFFFB6C1.toInt(), 40.0, 1000.0, "Restaurants and cafes"),
            Subcategory_Entity(7, 2, "Shopping", 0xFF9370DB.toInt(), 0.0, 3000.0, "Clothes and personal items"),
            Subcategory_Entity(8, 3, "Emergency Fund", 0xFF4ECDC4.toInt(), 2000.0, 2000.0, "Emergency savings"),
            Subcategory_Entity(9, 3, "Investment", 0xFF45B7D1.toInt(), 2000.0, 2000.0, "Stocks and bonds")
        )
        
        subcategories.forEach { subcategory ->
            subcategoryRepository.insertSubcategory(subcategory)
        }
    }
    
    private suspend fun initializeAccounts() {
        val accounts = listOf(
            Account_Entity(1, "Cash", AccountType.Cash, 160.0, "Physical cash on hand"),
            Account_Entity(2, "FNB Next Transact", AccountType.Debit, 1720.0, "Primary checking account"),
            Account_Entity(3, "Standard Bank Credit", AccountType.Credit, -500.0, "Credit card account")
        )
        
        accounts.forEach { account ->
            accountRepository.insertAccount(account)
        }
    }
    
    private suspend fun initializeTransactions() {
        val currentTime = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        
        val transactions = listOf(
            Payment_Entity(1, 1, 2, null, "Petrol", currentTime - oneDay, 1500.0, TransactionType.Expense, false, RepeatType.None, "Fuel for car", null),
            Payment_Entity(2, 6, 2, null, "Mug 'n Bean", currentTime - (3 * oneDay), 360.0, TransactionType.Expense, false, RepeatType.None, "Coffee and lunch", null),
            Payment_Entity(3, 8, 1, null, "Salary", currentTime - (7 * oneDay), 20000.0, TransactionType.Income, false, RepeatType.Monthly, "Monthly salary", null),
            Payment_Entity(4, 1, 2, null, "Groceries", currentTime - (2 * oneDay), 800.0, TransactionType.Expense, false, RepeatType.None, "Weekly groceries", null),
            Payment_Entity(5, 5, 2, null, "Movie Tickets", currentTime - (5 * oneDay), 120.0, TransactionType.Expense, false, RepeatType.None, "Cinema tickets", null)
        )
        
        transactions.forEach { transaction ->
            transactionRepository.insertTransaction(transaction)
        }
    }
}
