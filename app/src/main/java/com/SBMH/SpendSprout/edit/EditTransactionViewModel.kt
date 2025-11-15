
package com.SBMH.SpendSprout.edit

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

class EditTransactionViewModel : ViewModel() {

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadAccounts() {
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

    fun loadCategories() {
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

    fun saveTransaction(amount: Double, categoryId: String, accountId: String, date: Long, description: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val transactionsRef = database.getReference("users/${currentUser.uid}/expenses")
                val transactionId = transactionsRef.push().key!!
                val expense = Expense(transactionId, amount, date, categoryId, accountId, "", description, false, currentUser.uid)
                transactionsRef.child(transactionId).setValue(expense)
            }
        }
    }

    fun updateTransaction(transactionId: String, amount: Double, categoryId: String, accountId: String, date: Long, description: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val transactionRef = database.getReference("users/${currentUser.uid}/expenses/$transactionId")
                val expense = Expense(transactionId, amount, date, categoryId, accountId, "", description, false, currentUser.uid)
                transactionRef.setValue(expense)
            }
        }
    }

    fun getTransaction(transactionId: String): LiveData<Expense> {
        val transaction = MutableLiveData<Expense>()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val transactionRef = database.getReference("users/${currentUser.uid}/expenses/$transactionId")
            transactionRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactionData = snapshot.getValue(Expense::class.java)
                    if (transactionData != null) {
                        transaction.value = transactionData!!
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
        return transaction
    }
}
