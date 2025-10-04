package com.example.spendsprout_opsc.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Contact_DAO {
    //Insert one or more contacts into the database
    @Insert
    suspend fun insertAll(vararg contacts: Contact_Entity)

    //Insert single contact
    @Insert
    suspend fun insert(contact: Contact_Entity)

    //Update contact
    @Update
    suspend fun update(contact: Contact_Entity)

    //Delete a contact from the database
    @Delete
    suspend fun delete(contact: Contact_Entity)

    //Get all
    @Query("SELECT * FROM Contact ORDER BY contact_name ASC")
    fun getAll(): Flow<List<Contact_Entity>>

    //Get all based on the IDs that are passed in
    @Query("SELECT * FROM Contact WHERE id IN (:contactIds)")
    suspend fun loadAllByIds(contactIds: List<Int>): List<Contact_Entity>

    //Get contact based on the names that are passed in
    @Query("SELECT * FROM Contact WHERE contact_name IN (:contactNames)")
    suspend fun loadAllByNames(contactNames: List<String>): List<Contact_Entity>

    //Get contact by ID
    @Query("SELECT * FROM Contact WHERE id = :contactId")
    suspend fun getById(contactId: Int): Contact_Entity?
}