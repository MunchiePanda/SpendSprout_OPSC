package com.example.spendsprout_opsc.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spendsprout_opsc.transactions.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {

    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    private val _transactions = MutableLiveData<List<Expense>>()
    val transactions: LiveData<List<Expense>> = _transactions

    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance().getReference("users/$userId/expenses")

    fun loadAllTransactions() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val expenseList = mutableListOf<Expense>()
                for (expenseSnapshot in snapshot.children) {
                    val expense = expenseSnapshot.getValue(Expense::class.java)
                    expense?.id = expenseSnapshot.key
                    expense?.let { expenseList.add(it) }
                }
                _transactions.value = expenseList.sortedByDescending { it.expenseDate }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadTransactionsByDateRange(startDate: Long, endDate: Long) {
        database.orderByChild("expenseDate").startAt(startDate.toDouble()).endAt(endDate.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expenseList = mutableListOf<Expense>()
                    for (expenseSnapshot in snapshot.children) {
                        val expense = expenseSnapshot.getValue(Expense::class.java)
                        expense?.id = expenseSnapshot.key
                        expense?.let { expenseList.add(it) }
                    }
                    _transactions.value = expenseList.sortedByDescending { it.expenseDate }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}