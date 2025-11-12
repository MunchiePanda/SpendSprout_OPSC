package com.example.spendsprout_opsc.database

import com.example.spendsprout_opsc.roomdb.BudgetDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSetup @Inject constructor(
    private val database: BudgetDatabase
) {
    
    fun setupDatabase() {
        throw UnsupportedOperationException("Room database setup is no longer supported.")
    }
    
    fun isDatabaseReady(): Boolean {
        return false
    }
}
