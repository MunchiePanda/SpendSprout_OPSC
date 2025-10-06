package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Budget_DAO {
    //Insert one or more budgets into the database
    @Insert
    suspend fun insertAll(vararg budgets: Budget_Entity)

    //Insert single budget
    @Insert
    suspend fun insert(budget: Budget_Entity)

    //Update budget
    @Update
    suspend fun update(budget: Budget_Entity)

    //Delete a budget from the database
    @Delete
    suspend fun delete(budget: Budget_Entity)

    //Get all budgets
    @Query("SELECT * FROM Budget ORDER BY budget_name ASC")
    fun getAll(): Flow<List<Budget_Entity>>

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Budget WHERE id IN (:budgetIds)")
    suspend fun loadAllByIds(budgetIds: List<Int>): List<Budget_Entity>

    //Get budget by ID
    @Query("SELECT * FROM Budget WHERE id = :budgetId")
    suspend fun getById(budgetId: Int): Budget_Entity?

    //Get budget by name
    @Query("SELECT * FROM Budget WHERE budget_name = :budgetName")
    suspend fun getByName(budgetName: String): Budget_Entity?

    //Get count of budgets
    @Query("SELECT COUNT(*) FROM Budget")
    suspend fun getCount(): Int
}
