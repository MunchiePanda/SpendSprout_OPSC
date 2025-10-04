package com.example.spendsprout_opsc.database

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseErrorHandler {
    
    private const val TAG = "DatabaseErrorHandler"
    
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Database operation failed", throwable)
        // Handle database errors gracefully
        // Could show user-friendly error messages
        // Could retry operations
        // Could log to crash reporting service
    }
    
    fun handleDatabaseError(error: Throwable, operation: String) {
        Log.e(TAG, "Database error in $operation", error)

        when (error) {
            //is androidx.room.RoomDatabase.CannotCreateRoomDatabaseException -> {
                //Log.e(TAG, "Cannot create database: ${error.message}")
            //}
            //is androidx.room.RoomDatabase.MigrationException -> {
               // Log.e(TAG, "Database migration failed: ${error.message}")
            //}
            //else -> {
               // Log.e(TAG, "Unknown database error: ${error.message}")
            //}
        }
    }
    
    fun safeDatabaseOperation(
        scope: CoroutineScope,
        operation: suspend () -> Unit
    ) {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                operation()
            } catch (e: Exception) {
                handleDatabaseError(e, "Database operation")
            }
        }
    }
}
