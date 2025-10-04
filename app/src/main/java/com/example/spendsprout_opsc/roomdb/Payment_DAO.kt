package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Payment_DAO {
    //Insert one or more payments into the database
    @Insert
    suspend fun insertAll(vararg payments: Payment_Entity)

    //Insert single payment
    @Insert
    suspend fun insert(payment: Payment_Entity)

    //Update payment
    @Update
    suspend fun update(payment: Payment_Entity)

    //Delete a payment from the database
    @Delete
    suspend fun delete(payment: Payment_Entity)

    //Get all
    @Query("SELECT * FROM Payment ORDER BY payment_date DESC")
    fun getAll(): Flow<List<Payment_Entity>>

    //Get recent transactions (last N)
    @Query("SELECT * FROM Payment ORDER BY payment_date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<Payment_Entity>>

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Payment WHERE id IN (:paymentIds)")
    suspend fun loadAllByIds(paymentIds: List<Int>): List<Payment_Entity>

    //Get payment based on the names that are passed in
    @Query("SELECT * FROM Payment WHERE payment_name IN (:paymentNames)")
    suspend fun loadAllByNames(paymentNames: List<String>): List<Payment_Entity>

    //Get payment by ID
    @Query("SELECT * FROM Payment WHERE id = :paymentId")
    suspend fun getById(paymentId: Int): Payment_Entity?

    //Get payments between two amounts (inclusive)
    @Query("SELECT * FROM Payment WHERE payment_amount BETWEEN :startAmount AND :endAmount")
    suspend fun loadAllBetweenAmounts(startAmount: Double, endAmount: Double): List<Payment_Entity>

    //Get payments between two dates (inclusive)
    @Query("SELECT * FROM Payment WHERE payment_date BETWEEN :startDate AND :endDate")
    suspend fun loadAllBetweenDates(startDate: Long, endDate: Long): List<Payment_Entity>

    //Get payments by type (Income/Expense)
    @Query("SELECT * FROM Payment WHERE payment_type = :type ORDER BY payment_date DESC")
    fun getByType(type: String): Flow<List<Payment_Entity>>

    //Get total income
    @Query("SELECT SUM(payment_amount) FROM Payment WHERE payment_type = 'Income'")
    suspend fun getTotalIncome(): Double?

    //Get total expenses
    @Query("SELECT SUM(payment_amount) FROM Payment WHERE payment_type = 'Expense'")
    suspend fun getTotalExpenses(): Double?
}