package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Category_DAO {
    //Insert one or more categories into the database
    @Insert
    suspend fun insertAll(vararg categories: Category_Entity)

    //Insert single category
    @Insert
    suspend fun insert(category: Category_Entity)

    //Update category
    @Update
    suspend fun update(category: Category_Entity)

    //Delete a category from the database
    @Delete
    suspend fun delete(category: Category_Entity)

    //Get all
    @Query("SELECT * FROM Category ORDER BY category_name ASC")
    fun getAll(): Flow<List<Category_Entity>>

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Category WHERE id IN (:categoryIds)")
    suspend fun loadAllByIds(categoryIds: List<Int>): List<Category_Entity>

    //Get category based on the names that are passed in
    @Query("SELECT * FROM Category WHERE category_name IN (:categoryNames)")
    suspend fun loadAllByNames(categoryNames: List<String>): List<Category_Entity>

    //Get category by ID
    @Query("SELECT * FROM Category WHERE id = :categoryId")
    suspend fun getById(categoryId: Int): Category_Entity?

    //Get total allocated budget
    @Query("SELECT SUM(category_allocation) FROM Category")
    suspend fun getTotalAllocation(): Double?

    //Get total spent amount
    @Query("SELECT SUM(category_balance) FROM Category")
    suspend fun getTotalSpent(): Double?
}