package com.example.spendsprout_opsc.edit

import android.util.Log
import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.firebase.AccountRepository
import com.example.spendsprout_opsc.roomdb.Account_Entity
import kotlinx.coroutines.flow.first

class EditAccountViewModel {
    
    private val accountRepository = AccountRepository()
    
    suspend fun saveAccount(name: String, type: String, balance: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Account name is required" }

        // Map UI type to enum
        val accountType = mapToAccountType(type)

        // Write to DB synchronously
        try {
            val entity = Account_Entity(
                id = getNextAccountId(),
                accountName = name,
                accountType = accountType,
                accountBalance = balance,
                accountNotes = notes.ifBlank { null }
            )
            accountRepository.insertAccount(entity)
            Log.d("EditAccountViewModel", "Account saved: $name ($accountType) balance=$balance")
        } catch (e: Exception) {
            Log.e("EditAccountViewModel", "Error saving account: ${e.message}", e)
            throw e // Re-throw to handle in Activity
        }
    }

    private fun mapToAccountType(type: String): AccountType {
        return when (type.trim().lowercase()) {
            "cash" -> AccountType.Cash
            "card" -> AccountType.Credit
            "bank" -> AccountType.Debit
            else -> AccountType.Cash
        }
    }

    suspend fun updateAccount(id: Int, name: String, type: String, balance: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Account name is required" }

        // Map UI type to enum
        val accountType = mapToAccountType(type)

        Log.d("EditAccountViewModel", "Starting account update for ID: $id, name: $name")

        // Write to DB synchronously
        try {
            val entity = Account_Entity(
                id = id,
                accountName = name,
                accountType = accountType,
                accountBalance = balance,
                accountNotes = notes.ifBlank { null }
            )
            
            Log.d("EditAccountViewModel", "Updating with entity: $entity")
            accountRepository.updateAccount(entity)
            Log.d("EditAccountViewModel", "Account updated successfully: $name ($accountType) balance=$balance")
        } catch (e: Exception) {
            Log.e("EditAccountViewModel", "Error updating account: ${e.message}", e)
            throw e // Re-throw to handle in Activity
        }
    }

    private suspend fun getNextAccountId(): Int {
        return try {
            val accounts = accountRepository.getAllAccounts().first()
            (accounts.maxOfOrNull { it.id } ?: 0) + 1
        } catch (e: Exception) {
            1
        }
    }
}

