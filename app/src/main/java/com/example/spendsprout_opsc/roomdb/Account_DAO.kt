package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Account_DAO {
    //Insert one or more accounts into the database
    @Insert
    fun insertAll(vararg accounts: Account_Entity)

    //Delete an account from the database
    @Delete
    fun delete(account: Account_Entity)

    //Get all
    @Query("SELECT * FROM Account")   //* is select all
    fun getAll(): List<Account_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Account WHERE id IN (:accountIds)")   //selecting all where id is in the accountIds array
    fun loadAllByIds(accountIds: List<Int>): List<Account_Entity>

    //Get account based on the names that are passed in
    @Query("SELECT * FROM Account WHERE account_name IN (:accountNames)")
    fun loadAllByNames(accountNames: List<String>): List<Account_Entity>
}