package com.example.spendsprout_opsc.database

import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.roomdb.Account_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import com.example.spendsprout_opsc.roomdb.Expense_Entity
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
                
                // Initialize transactions (expenses)
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
            Expense_Entity(
                expenseName = "Petrol",
                expenseDate = currentTime - oneDay,
                expenseAmount = 1500.0,
                expenseType = com.example.spendsprout_opsc.ExpenseType.Expense,
                expenseIsOwed = false,
                expenseRepeat = RepeatType.None,
                expenseNotes = "Fuel for car",
                expenseImage = null,
                expenseCategory = "Transport",
                expenseStart = null,
                expenseEnd = null
            ),
            Expense_Entity(
                expenseName = "Mug 'n Bean",
                expenseDate = currentTime - (3 * oneDay),
                expenseAmount = 360.0,
                expenseType = com.example.spendsprout_opsc.ExpenseType.Expense,
                expenseIsOwed = false,
                expenseRepeat = RepeatType.None,
                expenseNotes = "Coffee and lunch",
                expenseImage = null,
                expenseCategory = "Dining Out",
                expenseStart = null,
                expenseEnd = null
            ),
            Expense_Entity(
                expenseName = "Salary",
                expenseDate = currentTime - (7 * oneDay),
                expenseAmount = 20000.0,
                expenseType = com.example.spendsprout_opsc.ExpenseType.Income,
                expenseIsOwed = false,
                expenseRepeat = RepeatType.Monthly,
                expenseNotes = "Monthly salary",
                expenseImage = null,
                expenseCategory = "Income",
                expenseStart = null,
                expenseEnd = null
            ),
            Expense_Entity(
                expenseName = "Groceries",
                expenseDate = currentTime - (2 * oneDay),
                expenseAmount = 800.0,
                expenseType = com.example.spendsprout_opsc.ExpenseType.Expense,
                expenseIsOwed = false,
                expenseRepeat = RepeatType.None,
                expenseNotes = "Weekly groceries",
                expenseImage = null,
                expenseCategory = "Groceries",
                expenseStart = null,
                expenseEnd = null
            ),
            Expense_Entity(
                expenseName = "Movie Tickets",
                expenseDate = currentTime - (5 * oneDay),
                expenseAmount = 120.0,
                expenseType = com.example.spendsprout_opsc.ExpenseType.Expense,
                expenseIsOwed = false,
                expenseRepeat = RepeatType.None,
                expenseNotes = "Cinema tickets",
                expenseImage = null,
                expenseCategory = "Entertainment",
                expenseStart = null,
                expenseEnd = null
            )
        )
        
        transactions.forEach { transaction ->
            transactionRepository.insertTransaction(transaction)
        }
    }
}
