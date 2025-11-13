package com.example.spendsprout_opsc.roomdb

/**
 * Legacy stub preserved to satisfy references while the app transitions off Room.
 */
class BudgetDatabase {
    fun categoryDao(): Category_DAO = legacyRoomRemoved()
    fun accountDao(): Account_DAO = legacyRoomRemoved()
}

private fun legacyRoomRemoved(): Nothing =
    throw UnsupportedOperationException("Room database has been removed; use Firebase repositories instead.")