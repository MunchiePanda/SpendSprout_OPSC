package com.SBMH.SpendSprout.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class TransactionsViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _transactions = MutableLiveData<List<Expense>>()
    val transactions: LiveData<List<Expense>> = _transactions

    fun loadTransactions() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val transactionsRef = database.getReference("users/${currentUser.uid}/expenses")
                transactionsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val transactionList = snapshot.children.mapNotNull { it.getValue(Expense::class.java) }
                        _transactions.postValue(transactionList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }
}
