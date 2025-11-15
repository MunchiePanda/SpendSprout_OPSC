package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseBudgetRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : BudgetRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val budgetsRef = database.getReference("users").child(userId).child("budgets")

    override fun getAllBudgets(): Flow<List<Budget>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val budgets = snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
                trySend(budgets)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        budgetsRef.addValueEventListener(listener)
        awaitClose { budgetsRef.removeEventListener(listener) }
    }

    override suspend fun getBudget(budgetId: String): Budget? {
        val snapshot = budgetsRef.child(budgetId).get().await()
        return snapshot.getValue(Budget::class.java)
    }

    override suspend fun addBudget(budget: Budget) {
        val budgetId = budgetsRef.push().key ?: throw IllegalStateException("Could not generate budget ID")
        budgetsRef.child(budgetId).setValue(budget.copy(budgetId = budgetId)).await()
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetsRef.child(budget.budgetId).setValue(budget).await()
    }

    override suspend fun deleteBudget(budgetId: String) {
        budgetsRef.child(budgetId).removeValue().await()
    }
}
