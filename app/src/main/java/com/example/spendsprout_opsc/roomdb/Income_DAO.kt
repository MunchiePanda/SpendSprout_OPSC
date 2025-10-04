package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Income_DAO {
    //Insert one or more incomes into the database
    @Insert
    fun insertAll(vararg income: Income_Entity)

    //Delete an income from the database
    @Delete
    fun delete(income: Income_Entity)

    //Get all
    @Query("SELECT * FROM Income")   //* is select all
    fun getAll(): List<Income_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Income WHERE id IN (:incomeIds)")   //selecting all where id is in the incomeIds array
    fun loadAllByIds(incomeIds: List<Int>): List<Income_Entity>

    //Get income based on the names that are passed in
    @Query("SELECT * FROM Income WHERE income_name IN (:incomeNames)")
    fun loadAllByNames(incomeNames: List<String>): List<Income_Entity>

    //Get incomes between two amounts (inclusive)
    @Query("SELECT * FROM Income WHERE income_amount BETWEEN :startAmount AND :endAmount")
    fun loadAllBetweenAmounts(startAmount: Double, endAmount: Double): List<Income_Entity>

    //Get incomes between two dates (inclusive)
    @Query("SELECT * FROM Income WHERE income_date BETWEEN :startDate AND :endDate")
    fun loadAllBetweenDates(startDate: Long, endDate: Long): List<Income_Entity>
}