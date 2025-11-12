package com.example.spendsprout_opsc.edit

import android.util.Log
import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.firebase.FirebaseRepositoryProvider
import com.example.spendsprout_opsc.roomdb.Account_Entity

class EditAccountViewModel {

    private val accountRepository = FirebaseRepositoryProvider.accountRepository

    suspend fun saveAccount(name: String, type: String, balance: Double, notes: String) {
        require(name.isNotBlank()) { "Account name is required" }

        val accountType = mapToAccountType(type)

        try {
            val entity = Account_Entity(
                id = 0,
                accountName = name,
                accountType = accountType,
                accountBalance = balance,
                accountNotes = notes.ifBlank { null }
            )
            accountRepository.insertAccount(entity)
            Log.d("EditAccountViewModel", "Account saved: $name ($accountType) balance=$balance")
        } catch (e: Exception) {
            Log.e("EditAccountViewModel", "Error saving account: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateAccount(id: Int, name: String, type: String, balance: Double, notes: String) {
        require(name.isNotBlank()) { "Account name is required" }

        val accountType = mapToAccountType(type)

        try {
            val entity = Account_Entity(
                id = id,
                accountName = name,
                accountType = accountType,
                accountBalance = balance,
                accountNotes = notes.ifBlank { null }
            )
            accountRepository.updateAccount(entity)
            Log.d("EditAccountViewModel", "Account updated successfully: $name ($accountType) balance=$balance")
        } catch (e: Exception) {
            Log.e("EditAccountViewModel", "Error updating account: ${e.message}", e)
            throw e
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
}
