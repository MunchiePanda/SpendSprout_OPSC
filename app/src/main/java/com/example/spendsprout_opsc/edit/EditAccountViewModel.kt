package com.example.spendsprout_opsc.edit

import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.roomdb.Account_Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditAccountViewModel {
    
    fun saveAccount(name: String, type: String, balance: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Account name is required" }

        // Map UI type to enum
        val accountType = mapToAccountType(type)

        // Write to DB on IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entity = Account_Entity(
                    id = getNextAccountId(),
                    accountName = name,
                    accountType = accountType,
                    accountBalance = balance,
                    accountNotes = notes.ifBlank { null }
                )
                BudgetApp.db.accountDao().insertAll(entity)
                android.util.Log.d("EditAccountViewModel", "Account saved: $name ($accountType) balance=$balance")
            } catch (e: Exception) {
                android.util.Log.e("EditAccountViewModel", "Error saving account: ${e.message}", e)
            }
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

    private fun getNextAccountId(): Int {
        return try {
            val existing = BudgetApp.db.accountDao().getAll()
            (existing.maxOfOrNull { it.id } ?: 0) + 1
        } catch (e: Exception) {
            1
        }
    }
}

