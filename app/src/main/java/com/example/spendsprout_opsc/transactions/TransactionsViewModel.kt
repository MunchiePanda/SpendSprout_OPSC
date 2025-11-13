package com.example.spendsprout_opsc.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.transactions.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TransactionsViewModel : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun loadAllTransactions() {
        viewModelScope.launch {
            currentUser?.let { user ->
                val transactionsRef = database.child("users").child(user.uid).child("transactions")
                transactionsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val transactionList = mutableListOf<Transaction>()
                        snapshot.children.forEach { transactionSnapshot ->
                            val transactionData = transactionSnapshot.getValue<HashMap<String, Any>>()
                            if (transactionData != null) {
                                val transaction = Transaction(
                                    id = transactionSnapshot.key ?: "",
                                    date = transactionData["date"] as? String ?: "",
                                    description = transactionData["description"] as? String ?: "",
                                    amount = transactionData["amount"] as? String ?: "",
                                    color = transactionData["color"] as? String ?: "",
                                    imagePath = transactionData["imagePath"] as? String
                                )
                                transactionList.add(transaction)
                            }
                        }
                        _transactions.postValue(transactionList.sortedByDescending { it.date })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TransactionsViewModel", "Failed to load transactions", error.toException())
                    }
                })
            }
        }
    }

    fun loadTransactionsByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            currentUser?.let { user ->
                // Assuming date is stored in "yyyy-MM-dd" format
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDateString = dateFormat.format(startDate)
                val endDateString = dateFormat.format(endDate)

                val transactionsRef = database.child("users").child(user.uid).child("transactions")
                val query = transactionsRef.orderByChild("date").startAt(startDateString).endAt(endDateString)

                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val transactionList = mutableListOf<Transaction>()
                        snapshot.children.forEach { transactionSnapshot ->
                            val transactionData = transactionSnapshot.getValue<HashMap<String, Any>>()
                            if (transactionData != null) {
                                val transaction = Transaction(
                                    id = transactionSnapshot.key ?: "",
                                    date = transactionData["date"] as? String ?: "",
                                    description = transactionData["description"] as? String ?: "",
                                    amount = transactionData["amount"] as? String ?: "",
                                    color = transactionData["color"] as? String ?: "",
                                    imagePath = transactionData["imagePath"] as? String
                                )
                                transactionList.add(transaction)
                            }
                        }
                        _transactions.postValue(transactionList.sortedByDescending { it.date })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TransactionsViewModel", "Failed to load transactions by date range", error.toException())
                    }
                })
            }
        }
    }
}
