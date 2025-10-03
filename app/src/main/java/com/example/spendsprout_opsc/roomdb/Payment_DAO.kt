package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Payment_DAO {
    //Insert one or more payments into the database
    @Insert
    fun insertAll(vararg payments: Payment_Entity)

    //Delete a payment from the database
    @Delete
    fun delete(payment: Payment_Entity)

    //Get all
    @Query("SELECT * FROM Payment")   //* is select all
    fun getAll(): List<Payment_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Payment WHERE id IN (:paymentIds)")   //selecting all where id is in the paymentIds array
    fun loadAllByIds(paymentIds: List<Int>): List<Payment_Entity>

    //Get payment based on the names that are passed in
    @Query("SELECT * FROM Payment WHERE payment_name IN (:paymentNames)")
    fun loadAllByNames(paymentNames: List<String>): List<Payment_Entity>

    //Get payments between two amounts (inclusive)
    @Query("SELECT * FROM Payment WHERE payment_amount BETWEEN :startAmount AND :endAmount")
    fun loadAllBetweenAmounts(startAmount: Double, endAmount: Double): List<Payment_Entity>

    //Get payments between two dates (inclusive)
    @Query("SELECT * FROM Payment WHERE payment_date BETWEEN :startDate AND :endDate")
    fun loadAllBetweenDates(startDate: Long, endDate: Long): List<Payment_Entity>
}