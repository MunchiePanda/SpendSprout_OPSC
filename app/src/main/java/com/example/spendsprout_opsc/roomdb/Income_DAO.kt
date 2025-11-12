package com.example.spendsprout_opsc.roomdb

interface Income_DAO {
    fun insertAll(vararg income: Income_Entity)
    fun delete(income: Income_Entity)
    fun getAll(): List<Income_Entity>
    fun loadAllByIds(incomeIds: List<Int>): List<Income_Entity>
    fun loadAllByNames(incomeNames: List<String>): List<Income_Entity>
    fun loadAllBetweenAmounts(startAmount: Double, endAmount: Double): List<Income_Entity>
    fun loadAllBetweenDates(startDate: Long, endDate: Long): List<Income_Entity>
}
