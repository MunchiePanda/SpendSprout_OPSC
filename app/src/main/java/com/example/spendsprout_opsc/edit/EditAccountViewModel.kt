package com.example.spendsprout_opsc.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import com.SBMH.SpendSprout.model.Account
import com.example.spendsprout_opsc.AccountType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditAccountViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val accountsRef = database.getReference("accounts")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun saveAccount(name: String, type: String, balance: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Account name is required" }

        currentUser?.let { user ->
            val userId = user.uid
            val accountId = accountsRef.child(userId).push().key
            accountId?.let {
                val account = Account(it, name, type, balance, notes.ifBlank { null }, userId)
                accountsRef.child(userId).child(it).setValue(account)
                    .addOnSuccessListener {
                        Log.d("EditAccountViewModel", "Account saved: $name ($type) balance=$balance")
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditAccountViewModel", "Error saving account: ${e.message}", e)
                    }
            }
        }
    }

    fun updateAccount(id: String, name: String, type: String, balance: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Account name is required" }

        currentUser?.let { user ->
            val userId = user.uid
            val account = Account(id, name, type, balance, notes.ifBlank { null }, userId)
            accountsRef.child(userId).child(id).setValue(account)
                .addOnSuccessListener {
                    Log.d("EditAccountViewModel", "Account updated successfully: $name ($type) balance=$balance")
                }
                .addOnFailureListener { e ->
                    Log.e("EditAccountViewModel", "Error updating account: ${e.message}", e)
                }
        }
    }
}
