package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Budget
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBudgetRepository @Inject constructor(
    private val database: FirebaseDatabase
) : BudgetRepository {

    private val tag = "FirebaseBudgetRepo"
    private val budgetsRef = database.getReference("budgets")

    override fun getAllBudgets(): Flow<List<Budget>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val budgetsList = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue<Budget>()?.copy(budgetId = dataSnapshot.key ?: "")
                }
                trySend(budgetsList)
                Log.d(tag, "Loaded ${budgetsList.size} budgets.")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "getAllBudgets: Database error.", error.toException())
                close(error.toException())
            }
        }
        budgetsRef.addValueEventListener(listener)
        awaitClose { budgetsRef.removeEventListener(listener) }
    }

    override suspend fun getBudget(budgetId: String): Budget? {
        if (budgetId.isEmpty()) return null
        return try {
            val snapshot = budgetsRef.child(budgetId).get().await()
            snapshot.getValue<Budget>()?.copy(budgetId = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e(tag, "getBudget failed for budgetId: $budgetId", e)
            null
        }
    }

    override suspend fun addBudget(budget: Budget) {
        val newBudgetId = budgetsRef.push().key ?: throw IllegalStateException("Could not generate budget ID")
        budgetsRef.child(newBudgetId).setValue(budget.copy(budgetId = newBudgetId)).await()
        Log.d(tag, "Budget added successfully with ID: $newBudgetId")
    }

    override suspend fun updateBudget(budget: Budget) {
        if (budget.budgetId.isEmpty()) {
            throw IllegalArgumentException("Cannot update budget with empty ID")
        }
        budgetsRef.child(budget.budgetId).setValue(budget).await()
        Log.d(tag, "Budget updated successfully for budgetId: ${budget.budgetId}")
    }

    override suspend fun deleteBudget(budgetId: String) {
        if (budgetId.isEmpty()) {
            throw IllegalArgumentException("Cannot delete budget with empty ID")
        }
        budgetsRef.child(budgetId).removeValue().await()
        Log.d(tag, "Budget deleted successfully: $budgetId")
    }
}
