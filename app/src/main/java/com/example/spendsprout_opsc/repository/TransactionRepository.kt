package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.roomdb.Payment_DAO
import com.example.spendsprout_opsc.roomdb.Payment_Entity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val paymentDao: Payment_DAO
) {
    fun getAllTransactions(): Flow<List<Payment_Entity>> = paymentDao.getAll()
    
    fun getRecentTransactions(limit: Int): Flow<List<Payment_Entity>> = paymentDao.getRecent(limit)
    
    fun getTransactionsByType(type: String): Flow<List<Payment_Entity>> = paymentDao.getByType(type)
    
    suspend fun insertTransaction(transaction: Payment_Entity) = paymentDao.insert(transaction)
    
    suspend fun updateTransaction(transaction: Payment_Entity) = paymentDao.update(transaction)
    
    suspend fun deleteTransaction(transaction: Payment_Entity) = paymentDao.delete(transaction)
    
    suspend fun getTransactionById(id: Int) = paymentDao.getById(id)
    
    suspend fun getTotalIncome() = paymentDao.getTotalIncome() ?: 0.0
    
    suspend fun getTotalExpenses() = paymentDao.getTotalExpenses() ?: 0.0
    
    suspend fun getTransactionsBetweenDates(startDate: Long, endDate: Long) = 
        paymentDao.loadAllBetweenDates(startDate, endDate)
    
    suspend fun getTransactionsBetweenAmounts(startAmount: Double, endAmount: Double) = 
        paymentDao.loadAllBetweenAmounts(startAmount, endAmount)
}
