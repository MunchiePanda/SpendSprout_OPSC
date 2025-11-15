
package com.example.spendsprout_opsc.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.SBMH.SpendSprout.model.Account
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OverviewViewModel : ViewModel() {

    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance

    private val _recentTransactions = MutableLiveData<List<Expense>>()
    val recentTransactions: LiveData<List<Expense>> = _recentTransactions

    private val _categorySummary = MutableLiveData<List<Category>>()
    val categorySummary: LiveData<List<Category>> = _categorySummary

    private val _accountSummary = MutableLiveData<List<Account>>()
    val accountSummary: LiveData<List<Account>> = _accountSummary

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadData() {
        if (userId == null) return

        loadTotalBalance()
        loadRecentTransactions()
        loadCategorySummary()
        loadAccountSummary()
    }

    private fun loadTotalBalance() {
        val accountsRef = database.getReference("users/$userId/accounts")
        accountsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val total = snapshot.children.sumOf { it.child("accountBalance").getValue(Double::class.java) ?: 0.0 }
                _totalBalance.postValue(total)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadRecentTransactions() {
        val transactionsRef = database.getReference("users/$userId/expenses")
        transactionsRef.orderByChild("date").limitToLast(5).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { it.getValue(Expense::class.java) }.reversed()
                _recentTransactions.postValue(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadCategorySummary() {
        val categoriesRef = database.getReference("users/$userId/categories")
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                _categorySummary.postValue(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadAccountSummary() {
        val accountsRef = database.getReference("users/$userId/accounts")
        accountsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accounts = snapshot.children.mapNotNull { it.getValue(Account::class.java) }
                _accountSummary.postValue(accounts)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
