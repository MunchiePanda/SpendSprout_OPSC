package com.example.spendsprout_opsc.roomdb

import kotlinx.coroutines.flow.Flow

interface Contact_DAO {
    suspend fun insertAll(vararg contacts: Contact_Entity)
    suspend fun insert(contact: Contact_Entity)
    suspend fun update(contact: Contact_Entity)
    suspend fun delete(contact: Contact_Entity)
    fun getAll(): Flow<List<Contact_Entity>>
    suspend fun loadAllByIds(contactIds: List<Int>): List<Contact_Entity>
    suspend fun loadAllByNames(contactNames: List<String>): List<Contact_Entity>
    suspend fun getById(contactId: Int): Contact_Entity?
}
