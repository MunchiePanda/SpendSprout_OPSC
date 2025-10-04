package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Subcategory_DAO {
    //Insert one or more subcategories into the database
    @Insert
    suspend fun insertAll(vararg subcategories: Subcategory_Entity)

    //Insert single subcategory
    @Insert
    suspend fun insert(subcategory: Subcategory_Entity)

    //Update subcategory
    @Update
    suspend fun update(subcategory: Subcategory_Entity)

    //Delete a subcategory from the database
    @Delete
    suspend fun delete(subcategory: Subcategory_Entity)

    //Get all
    @Query("SELECT * FROM Subcategory ORDER BY subcategory_name ASC")
    fun getAll(): Flow<List<Subcategory_Entity>>

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Subcategory WHERE id IN (:subcategoryIds)")
    suspend fun loadAllByIds(subcategoryIds: List<Int>): List<Subcategory_Entity>

    //Get subcategory based on the names that are passed in
    @Query("SELECT * FROM Subcategory WHERE subcategory_name IN (:subcategoryNames)")
    suspend fun loadAllByNames(subcategoryNames: List<String>): List<Subcategory_Entity>

    //Get subcategory by ID
    @Query("SELECT * FROM Subcategory WHERE id = :subcategoryId")
    suspend fun getById(subcategoryId: Int): Subcategory_Entity?
}