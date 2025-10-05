package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Expense_DAO {
    //Insert one or more expenses into the database
    @Insert
    fun insertAll(vararg expense: Expense_Entity)
    
    //Insert single expense (for submission requirements)
    @Insert
    suspend fun insert(expense: Expense_Entity)

    //Delete an expense from the database
    @Delete
    fun delete(expense: Expense_Entity)

    //Get all
    @Query("SELECT * FROM Expense")   //* is select all
    fun getAll(): List<Expense_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Expense WHERE id IN (:expenseIds)")   //selecting all where id is in the expenseIds array
    fun loadAllByIds(expenseIds: List<Int>): List<Expense_Entity>

    //Get expense based on the names that are passed in
    @Query("SELECT * FROM Expense WHERE expense_name IN (:expenseNames)")
    fun loadAllByNames(expenseNames: List<String>): List<Expense_Entity>

    //Get expenses between two amounts (inclusive)
    @Query("SELECT * FROM Expense WHERE expense_amount BETWEEN :startAmount AND :endAmount")
    fun loadAllBetweenAmounts(startAmount: Double, endAmount: Double): List<Expense_Entity>

    //Get expenses between two dates (inclusive)
    @Query("SELECT * FROM Expense WHERE expense_date BETWEEN :startDate AND :endDate")
    fun loadAllBetweenDates(startDate: Long, endDate: Long): List<Expense_Entity>
    
    // New queries for submission requirements
    @Query("SELECT * FROM Expense WHERE expense_date BETWEEN :start AND :end ORDER BY expense_date DESC")
    suspend fun getBetweenDates(start: Long, end: Long): List<Expense_Entity>

    @Query("SELECT * FROM Expense WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Expense_Entity?

    @Query("""
        SELECT expense_category AS category, SUM(expense_amount) AS total
        FROM Expense
        WHERE expense_date BETWEEN :start AND :end
        GROUP BY expense_category
        ORDER BY total DESC
    """)
    suspend fun totalsByCategory(start: Long, end: Long): List<CategoryTotal>
}

data class CategoryTotal(val category: String, val total: Double)