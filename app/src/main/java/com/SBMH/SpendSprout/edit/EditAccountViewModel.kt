package com.SBMH.SpendSprout.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class EditAccountViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveAccount(accountName: String, accountBalance: Double) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val accountsRef = database.getReference("users/${currentUser.uid}/accounts")
                val accountId = accountsRef.push().key!!
                val account = Account(accountId, accountName, accountBalance)
                accountsRef.child(accountId).setValue(account)
            }
        }
    }

    fun updateAccount(accountId: String, accountName: String, accountBalance: Double) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val accountRef = database.getReference("users/${currentUser.uid}/accounts/$accountId")
                val account = Account(accountId, accountName, accountBalance)
                accountRef.setValue(account)
            }
        }
    }
}
