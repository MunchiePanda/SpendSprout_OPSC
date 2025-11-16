package com.example.spendsprout_opsc.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigration {
    
    val MIGRATION_0_1 = object : Migration(0, 1) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Initial database creation with all tables
            // This migration handles the initial setup
        }
    }
    
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Future migration for schema changes
            // Add new columns, tables, or modify existing ones
        }
    }
    
    val ALL_MIGRATIONS = arrayOf(MIGRATION_0_1, MIGRATION_1_2)
}