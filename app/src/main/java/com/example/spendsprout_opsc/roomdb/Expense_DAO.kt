package com.example.spendsprout_opsc.roomdb

interface Expense_DAO {
    fun insertAll(vararg expense: Expense_Entity)
    suspend fun insert(expense: Expense_Entity)
    fun delete(expense: Expense_Entity)
    suspend fun update(expense: Expense_Entity)
    fun getAll(): List<Expense_Entity>
    fun loadAllByIds(expenseIds: List<Int>): List<Expense_Entity>
    fun loadAllByNames(expenseNames: List<String>): List<Expense_Entity>
    fun loadAllBetweenAmounts(startAmount: Double, endAmount: Double): List<Expense_Entity>
    fun loadAllBetweenDates(startDate: Long, endDate: Long): List<Expense_Entity>
    suspend fun getBetweenDates(start: Long, end: Long): List<Expense_Entity>
    suspend fun getById(id: Long): Expense_Entity?
    suspend fun totalsByCategory(start: Long, end: Long): List<CategoryTotal>
}

data class CategoryTotal(val category: String, val total: Double)
