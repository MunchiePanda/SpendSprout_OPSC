package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.roomdb.Account_DAO
import com.example.spendsprout_opsc.roomdb.Account_Entity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: Account_DAO
) {
    fun getAllAccounts(): Flow<List<Account_Entity>> = accountDao.getAll()
    
    suspend fun insertAccount(account: Account_Entity) = accountDao.insert(account)
    
    suspend fun updateAccount(account: Account_Entity) = accountDao.update(account)
    
    suspend fun deleteAccount(account: Account_Entity) = accountDao.delete(account)
    
    suspend fun getAccountById(id: Int) = accountDao.getById(id)
    
    suspend fun getTotalBalance() = accountDao.getTotalBalance() ?: 0.0
}
