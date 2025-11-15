package com.SBMH.SpendSprout.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Account
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _transactions = MutableLiveData<List<Expense>>()
    val transactions: LiveData<List<Expense>> = _transactions

    fun loadData() {
        loadAccounts()
        loadCategories()
        loadTransactions()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val accountsRef = database.getReference("users/${currentUser.uid}/accounts")
                accountsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val accountList = snapshot.children.mapNotNull { it.getValue(Account::class.java) }
                        _accounts.postValue(accountList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val categoriesRef = database.getReference("users/${currentUser.uid}/categories")
                categoriesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categoryList = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                        _categories.postValue(categoryList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }

    private fun loadTransactions() {
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
