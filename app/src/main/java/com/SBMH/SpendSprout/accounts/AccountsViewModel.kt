
package com.SBMH.SpendSprout.accounts

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

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    fun loadAccounts() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val accountsRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/accounts")
            accountsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val accountList = mutableListOf<Account>()
                    for (accountSnapshot in snapshot.children) {
                        val account = accountSnapshot.getValue(Account::class.java)
                        if (account != null) {
                            var accountWithId = account.copy(id = accountSnapshot.key!!)
                            accountList.add(accountWithId)
                        }
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
