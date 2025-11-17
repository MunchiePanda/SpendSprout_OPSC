package com.example.spendsprout_opsc.firebase

/**
 * Centralized switches for the gradual Firebase migration.
 * Keep feature-specific flags here so we can enable/disable remote sources
 * without editing every repository.
 */
object FirebaseMigrationConfig {
    const val useBudgets = true
    const val useAccounts = true
    const val useCategories = true
    const val useSubcategories = true
    const val useTransactions = true
}

