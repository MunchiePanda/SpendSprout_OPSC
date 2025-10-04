package com.example.spendsprout_opsc.database

import com.example.spendsprout_opsc.roomdb.BudgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSetup @Inject constructor(
    private val database: BudgetDatabase
) {
    
    fun setupDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // Ensure database is properly initialized
            database.openHelper.writableDatabase
        }
    }
    
    fun isDatabaseReady(): Boolean {
        return database.isOpen
    }
}
