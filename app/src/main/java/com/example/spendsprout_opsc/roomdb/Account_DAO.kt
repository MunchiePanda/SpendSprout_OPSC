package com.example.spendsprout_opsc.roomdb

import kotlinx.coroutines.flow.Flow

/**
 * Legacy stub for the old Room-based DAO. All functions now throw to indicate
 * that Room has been removed during the Firebase migration.
 */
interface Account_DAO {
    suspend fun insertAll(vararg accounts: Account_Entity): Nothing = legacyRoomRemoved()
    suspend fun insert(account: Account_Entity): Nothing = legacyRoomRemoved()
    suspend fun update(account: Account_Entity): Nothing = legacyRoomRemoved()
    suspend fun delete(account: Account_Entity): Nothing = legacyRoomRemoved()
    fun getAll(): Flow<List<Account_Entity>> = legacyRoomRemoved()
    suspend fun loadAllByIds(accountIds: List<Int>): List<Account_Entity> = legacyRoomRemoved()
    suspend fun loadAllByNames(accountNames: List<String>): List<Account_Entity> = legacyRoomRemoved()
    suspend fun getById(accountId: Int): Account_Entity? = legacyRoomRemoved()
    suspend fun getTotalBalance(): Double? = legacyRoomRemoved()
    suspend fun getCount(): Int = legacyRoomRemoved()
}

private fun <T> legacyRoomRemoved(): T =
    throw UnsupportedOperationException("Room database has been removed; use Firebase repositories instead.")