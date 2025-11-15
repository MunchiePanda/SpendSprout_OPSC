package com.example.spendsprout_opsc.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditBudgetViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val budgetsRef = database.getReference("budgets")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun saveBudget(name: String, openingBalance: Double, minGoal: Double, maxGoal: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Budget name is required" }
        require(minGoal < maxGoal) { "Minimum goal must be less than maximum goal" }
        require(minGoal <= openingBalance && maxGoal <= openingBalance) { "Goals cannot exceed opening balance" }

        currentUser?.let { user ->
            val userId = user.uid
            val budgetId = budgetsRef.child(userId).push().key
            budgetId?.let {
                val budget = Budget(it, name, openingBalance, minGoal, maxGoal, openingBalance, notes.ifBlank { null })
                budgetsRef.child(userId).child(it).setValue(budget)
                    .addOnSuccessListener {
                        Log.d("EditBudgetViewModel", "Budget saved: $name opening=$openingBalance min=$minGoal max=$maxGoal")
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditBudgetViewModel", "Error saving budget: ${e.message}", e)
                    }
            }
        }
    }

    fun updateBudget(id: String, name: String, openingBalance: Double, minGoal: Double, maxGoal: Double, notes: String) {
        // Validate
        require(name.isNotBlank()) { "Budget name is required" }
        require(minGoal < maxGoal) { "Minimum goal must be less than maximum goal" }
        require(minGoal <= openingBalance && maxGoal <= openingBalance) { "Goals cannot exceed opening balance" }

        currentUser?.let { user ->
            val userId = user.uid
            val budget = Budget(id, name, openingBalance, minGoal, maxGoal, openingBalance, notes.ifBlank { null })
            budgetsRef.child(userId).child(id).setValue(budget)
                .addOnSuccessListener {
                    Log.d("EditBudgetViewModel", "Budget updated successfully: $name opening=$openingBalance min=$minGoal max=$maxGoal")
                }
                .addOnFailureListener { e ->
                    Log.e("EditBudgetViewModel", "Error updating budget: ${e.message}", e)
                }
        }
    }
}

data class Budget(
    val id: String = "",
    val budgetName: String = "",
    val openingBalance: Double = 0.0,
    val budgetMinGoal: Double = 0.0,
    val budgetMaxGoal: Double = 0.0,
    val budgetBalance: Double = 0.0,
    val budgetNotes: String? = null
)
