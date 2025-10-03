package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Category_DAO {
    //Insert one or more categories into the database
    @Insert
    fun insertAll(vararg categories: Category_Entity)

    //Delete a category from the database
    @Delete
    fun delete(category: Category_Entity)

    //Get all
    @Query("SELECT * FROM Category")   //* is select all
    fun getAll(): List<Category_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Category WHERE id IN (:categoryIds)")   //selecting all where id is in the dbExampleIds array
    fun loadAllByIds(categoryIds: List<Int>): List<Category_Entity>
        //Input: dao.loadAllByIds(listOf(1,2))
    //Output: [ Category_Entity(id = 1, categoryName = "Needs", categoryColor = 0, categoryBalance = 0.0, categoryAllocation = 0.0, categoryNotes = null),
    //          Category_Entity(id = 2, categoryName = "Wants", categoryColor = 3, categoryBalance = 0.0, categoryAllocation = 0.0, categoryNotes = null) ]

    //Get category based on the names that are passed in
    @Query("SELECT * FROM Category WHERE category_name IN (:categoryNames)")
    fun loadAllByNames(categoryNames: List<String>): List<Category_Entity>
        //Input: dao.loadAllByNames(listOf("Needs", "Wants"))
        //Output: [ Category_Entity(id = 1, categoryName = "Needs", categoryColor = 0, categoryBalance = 0.0, categoryAllocation = 0.0, categoryNotes = null),
        //          Category_Entity(id = 2, categoryName = "Wants", categoryColor = 3, categoryBalance = 0.0, categoryAllocation = 0.0, categoryNotes = null) ]
}