package com.example.spendsprout_opsc.accounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.SBMH.SpendSprout.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountsViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val accountsRef = database.getReference("accounts")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    fun loadAccounts() {
        currentUser?.let { user ->
            val userId = user.uid
            accountsRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val accountList = mutableListOf<Account>()
                    for (accountSnapshot in snapshot.children) {
                        val account = accountSnapshot.getValue(Account::class.java)
                        account?.let { accountList.add(it) }
                    }
                    _accounts.value = accountList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}
