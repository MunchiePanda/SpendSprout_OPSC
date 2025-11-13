package com.example.spendsprout_opsc.roomdb

import kotlinx.coroutines.flow.Flow

interface Category_DAO {
    suspend fun insertAll(vararg categories: Category_Entity): Nothing = legacyRoomRemoved()
    suspend fun insert(category: Category_Entity): Nothing = legacyRoomRemoved()
    suspend fun update(category: Category_Entity): Nothing = legacyRoomRemoved()
    suspend fun delete(category: Category_Entity): Nothing = legacyRoomRemoved()
    fun getAll(): Flow<List<Category_Entity>> = legacyRoomRemoved()
    suspend fun loadAllByIds(categoryIds: List<Int>): List<Category_Entity> = legacyRoomRemoved()
    suspend fun loadAllByNames(categoryNames: List<String>): List<Category_Entity> = legacyRoomRemoved()
    suspend fun getById(categoryId: Int): Category_Entity? = legacyRoomRemoved()
    suspend fun getTotalAllocation(): Double? = legacyRoomRemoved()
    suspend fun getTotalSpent(): Double? = legacyRoomRemoved()
}

private fun <T> legacyRoomRemoved(): T =
    throw UnsupportedOperationException("Room database has been removed; use Firebase repositories instead.")