package com.example.spendsprout_opsc.roomdb

import kotlinx.coroutines.flow.Flow

interface Category_DAO {
    suspend fun insertAll(vararg categories: Category_Entity)
    suspend fun insert(category: Category_Entity)
    suspend fun update(category: Category_Entity)
    suspend fun delete(category: Category_Entity)
    fun getAll(): Flow<List<Category_Entity>>
    suspend fun loadAllByIds(categoryIds: List<Int>): List<Category_Entity>
    suspend fun loadAllByNames(categoryNames: List<String>): List<Category_Entity>
    suspend fun getById(categoryId: Int): Category_Entity?
    suspend fun getTotalAllocation(): Double?
    suspend fun getTotalSpent(): Double?
}
