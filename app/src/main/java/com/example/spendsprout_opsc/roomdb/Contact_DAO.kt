package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Contact_DAO {
    //Insert one or more contacts into the database
    @Insert
    fun insertAll(vararg contacts: Contact_Entity)

    //Delete a contact from the database
    @Delete
    fun delete(contact: Contact_Entity)

    //Get all
    @Query("SELECT * FROM Contact")   //* is select all
    fun getAll(): List<Contact_Entity>       //function name is getAll, it returns a list

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Contact WHERE id IN (:contactIds)")   //selecting all where id is in the contactIds array
    fun loadAllByIds(contactIds: List<Int>): List<Contact_Entity>

    //Get contact based on the names that are passed in
    @Query("SELECT * FROM Contact WHERE contact_name IN (:contactNames)")
    fun loadAllByNames(contactNames: List<String>): List<Contact_Entity>
}