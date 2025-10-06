package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Subcategory_DAO {
    //Insert one or more subcategories into the database
    @Insert
    fun insertAll(vararg subcategories: Subcategory_Entity)

    //Insert single subcategory
    @Insert
    suspend fun insert(subcategory: Subcategory_Entity)

    //Update subcategory
    @Update
    suspend fun update(subcategory: Subcategory_Entity)

    //Delete a subcategory from the database
    @Delete
    fun delete(subcategory: Subcategory_Entity)

    //Get all
    @Query("SELECT * FROM Subcategory")   //* is select all
    fun getAll(): List<Subcategory_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Subcategory WHERE id IN (:subcategoryIds)")   //selecting all where id is in the subcategoryIds array
    fun loadAllByIds(subcategoryIds: List<Int>): List<Subcategory_Entity>

    //Get subcategory based on the names that are passed in
    @Query("SELECT * FROM Subcategory WHERE subcategory_name IN (:subcategoryNames)")
    fun loadAllByNames(subcategoryNames: List<String>): List<Subcategory_Entity>
    
    //Get subcategory by ID
    @Query("SELECT * FROM Subcategory WHERE id = :subcategoryId")
    suspend fun getById(subcategoryId: Int): Subcategory_Entity?

    //Get subcategories for a specific category (assuming there's a category_id field)
    @Query("SELECT * FROM Subcategory WHERE category_id = :categoryId")
    suspend fun getByCategoryId(categoryId: Int): List<Subcategory_Entity>
    
    //Get count of subcategories
    @Query("SELECT COUNT(*) FROM Subcategory")
    suspend fun getCount(): Int
}