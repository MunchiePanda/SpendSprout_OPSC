package com.SBMH.SpendSprout.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class EditBudgetViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveBudget(budgetName: String, budgetAmount: Double, budgetCategory: String, budgetStartDate: Long, budgetEndDate: Long) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val budgetsRef = database.getReference("users/${currentUser.uid}/budgets")
                val budgetId = budgetsRef.push().key!!
                val budget = Budget(budgetId, budgetName, budgetAmount, budgetCategory, budgetStartDate, budgetEndDate)
                budgetsRef.child(budgetId).setValue(budget)
            }
        }
    }

    fun updateBudget(budgetId: String, budgetName: String, budgetAmount: Double, budgetCategory: String, budgetStartDate: Long, budgetEndDate: Long) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val budgetRef = database.getReference("users/${currentUser.uid}/budgets/$budgetId")
                val budget = Budget(budgetId, budgetName, budgetAmount, budgetCategory, budgetStartDate, budgetEndDate)
                budgetRef.setValue(budget)
            }
        }
    }

    fun getBudget(budgetId: String): LiveData<Budget> {
        val budget = MutableLiveData<Budget>()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val budgetRef = database.getReference("users/${currentUser.uid}/budgets/$budgetId")
            budgetRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val budgetData = snapshot.getValue(Budget::class.java)
                    if (budgetData != null) {
                        budget.value = budgetData!!
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
        return budget
    }
}
