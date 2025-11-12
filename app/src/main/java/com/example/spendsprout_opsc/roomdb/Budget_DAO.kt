package com.example.spendsprout_opsc.roomdb

import kotlinx.coroutines.flow.Flow

interface Budget_DAO {
    suspend fun insertAll(vararg budgets: Budget_Entity)
    suspend fun insert(budget: Budget_Entity)
    suspend fun update(budget: Budget_Entity)
    suspend fun delete(budget: Budget_Entity)
    fun getAll(): Flow<List<Budget_Entity>>
    suspend fun loadAllByIds(budgetIds: List<Int>): List<Budget_Entity>
    suspend fun getById(budgetId: Int): Budget_Entity?
    suspend fun getByName(budgetName: String): Budget_Entity?
    suspend fun getCount(): Int
}
