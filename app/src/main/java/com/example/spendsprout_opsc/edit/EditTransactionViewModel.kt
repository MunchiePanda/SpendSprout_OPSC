
package com.example.spendsprout_opsc.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spendsprout_opsc.accounts.Account
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.transactions.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditTransactionViewModel : ViewModel() {

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadSpinnerData() {
        if (userId == null) return

        val accountsRef = database.getReference("users/$userId/accounts")
        accountsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accountList = snapshot.children.mapNotNull { it.getValue(Account::class.java) }
                _accounts.postValue(accountList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        val categoriesRef = database.getReference("users/$userId/categories")
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

    fun loadTransaction(transactionId: String) {
        if (userId == null) return

        val transactionRef = database.getReference("users/$userId/expenses/$transactionId")
        transactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _transaction.postValue(snapshot.getValue(Transaction::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                _transaction.postValue(null)
            }
        })
    }

    fun saveTransaction(transaction: Transaction) {
        if (userId == null) return

        val transactionsRef = database.getReference("users/$userId/expenses")
        val transactionId = transaction.id ?: transactionsRef.push().key!!
        transactionsRef.child(transactionId).setValue(transaction.copy(id = transactionId))
    }
}
