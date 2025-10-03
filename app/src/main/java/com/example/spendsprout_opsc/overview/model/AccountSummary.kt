package com.example.spendsprout_opsc.overview.model

/**
 * AccountSummary - Data Model for Account Summary Display
 * 
 * This is like Unity's ScriptableObject or data structure for account information.
 * Similar to Unity's data classes used for UI display.
 * 
 * Responsibilities:
 * - Store account summary data (like Unity's data containers)
 * - Provide account information for UI display (like Unity's UI data binding)
 * - Handle account balances and limits (like Unity's financial data)
 */
data class AccountSummary(
    val name: String,
    val balance: String,
    val limit: String
)
