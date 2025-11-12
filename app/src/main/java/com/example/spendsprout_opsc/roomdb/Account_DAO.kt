package com.example.spendsprout_opsc.roomdb

import kotlinx.coroutines.flow.Flow

/**
 * Legacy stub for the old Room-based DAO. All functions now throw to indicate
 * that Room has been removed during the Firebase migration.
 */
interface Account_DAO {
    suspend fun insertAll(vararg accounts: Account_Entity)
    suspend fun insert(account: Account_Entity)
    suspend fun update(account: Account_Entity)
    suspend fun delete(account: Account_Entity)
    fun getAll(): Flow<List<Account_Entity>>
    suspend fun loadAllByIds(accountIds: List<Int>): List<Account_Entity>
    suspend fun loadAllByNames(accountNames: List<String>): List<Account_Entity>
    suspend fun getById(accountId: Int): Account_Entity?
    suspend fun getTotalBalance(): Double?
    suspend fun getCount(): Int
}
