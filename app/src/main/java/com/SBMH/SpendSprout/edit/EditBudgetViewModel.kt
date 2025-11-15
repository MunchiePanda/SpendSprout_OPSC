package com.SBMH.SpendSprout.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
}
