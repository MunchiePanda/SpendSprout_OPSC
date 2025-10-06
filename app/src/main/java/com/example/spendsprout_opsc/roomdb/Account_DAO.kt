package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Account_DAO {
    //Insert one or more accounts into the database
    @Insert
    suspend fun insertAll(vararg accounts: Account_Entity)

    //Insert single account
    @Insert
    suspend fun insert(account: Account_Entity)

    //Update account
    @Update
    suspend fun update(account: Account_Entity)

    //Delete an account from the database
    @Delete
    suspend fun delete(account: Account_Entity)

    //Get all
    @Query("SELECT * FROM Account ORDER BY account_name ASC")
    fun getAll(): Flow<List<Account_Entity>>

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Account WHERE id IN (:accountIds)")
    suspend fun loadAllByIds(accountIds: List<Int>): List<Account_Entity>

    //Get account based on the names that are passed in
    @Query("SELECT * FROM Account WHERE account_name IN (:accountNames)")
    suspend fun loadAllByNames(accountNames: List<String>): List<Account_Entity>

    //Get account by ID
    @Query("SELECT * FROM Account WHERE id = :accountId")
    suspend fun getById(accountId: Int): Account_Entity?

    //Get total balance across all accounts
    @Query("SELECT SUM(account_balance) FROM Account")
    suspend fun getTotalBalance(): Double?
    
    //Get count of accounts
    @Query("SELECT COUNT(*) FROM Account")
    suspend fun getCount(): Int
}