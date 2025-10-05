package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.roomdb.Expense_DAO
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val expenseDao: Expense_DAO
) {
    fun getAllTransactions(): Flow<List<Expense_Entity>> = flow { emit(expenseDao.getAll()) }
    
    fun getRecentTransactions(limit: Int): Flow<List<Expense_Entity>> = flow {
        val all = expenseDao.getAll().sortedByDescending { it.expenseDate }
        emit(all.take(limit))
    }
    
    fun getTransactionsByType(type: String): Flow<List<Expense_Entity>> = flow {
        val normalized = type.trim().lowercase()
        val all = expenseDao.getAll()
        emit(all.filter { it.expenseType.name.lowercase() == normalized })
    }
    
    suspend fun insertTransaction(transaction: Expense_Entity) = expenseDao.insert(transaction)
    
    suspend fun updateTransaction(transaction: Expense_Entity) {
        // No explicit update in DAO; for prototype, delete+insert can be implemented if needed
        // Left as a no-op to satisfy current usage
    }
    
    suspend fun deleteTransaction(transaction: Expense_Entity) = expenseDao.delete(transaction)
    
    suspend fun getTransactionById(id: Long) = expenseDao.getById(id)
    
    suspend fun getTotalIncome(): Double {
        val all = expenseDao.getAll()
        return all.filter { it.expenseType.name == "Income" }.sumOf { it.expenseAmount }
    }
    
    suspend fun getTotalExpenses(): Double {
        val all = expenseDao.getAll()
        return all.filter { it.expenseType.name == "Expense" }.sumOf { it.expenseAmount }
    }
    
    suspend fun getTransactionsBetweenDates(startDate: Long, endDate: Long): List<Expense_Entity> = 
        expenseDao.loadAllBetweenDates(startDate, endDate)
    
    suspend fun getTransactionsBetweenAmounts(startAmount: Double, endAmount: Double): List<Expense_Entity> = 
        expenseDao.loadAllBetweenAmounts(startAmount, endAmount)
}
