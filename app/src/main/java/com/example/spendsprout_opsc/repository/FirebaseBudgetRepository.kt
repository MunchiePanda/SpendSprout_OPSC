package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Budget
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBudgetRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val authState: @JvmSuppressWildcards Flow<FirebaseUser?>
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<Budget>> = authState.flatMapLatest { user ->
        if (user == null) {
            Log.w("FirebaseBudgetRepo", "getAllBudgets: No user logged in.")
            callbackFlow { trySend(emptyList()) }
        } else {
            val budgetsRef = database.getReference("users").child(user.uid).child("budgets")
            callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val budgetsList = snapshot.children.mapNotNull { dataSnapshot ->
                            // Correctly deserialize and map the key to the budgetId
                            dataSnapshot.getValue<Budget>()?.copy(budgetId = dataSnapshot.key ?: "")
                        }
                        trySend(budgetsList)
                        Log.d("FirebaseBudgetRepo", "Loaded ${budgetsList.size} budgets for user ${user.uid}")
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseBudgetRepo", "getAllBudgets: Database error.", error.toException())
                        close(error.toException())
                    }
                }
                budgetsRef.addValueEventListener(listener)
                awaitClose { budgetsRef.removeEventListener(listener) }
            }
        }
    }

    override suspend fun getBudget(budgetId: String): Budget? {
        val user = authState.first() ?: return null
        if (budgetId.isEmpty()) return null

        return try {
            val snapshot = database.getReference("users").child(user.uid).child("budgets").child(budgetId).get().await()
            snapshot.getValue<Budget>()?.copy(budgetId = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e("FirebaseBudgetRepo", "getBudget failed for budgetId: $budgetId", e)
            null
        }
    }


    override suspend fun addBudget(budget: Budget) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        val budgetsRef = database.getReference("users").child(user.uid).child("budgets")
        val newBudgetId = budgetsRef.push().key ?: throw IllegalStateException("Could not generate budget ID")
        // Use the passed budget object, but ensure the ID is the one we just generated
        budgetsRef.child(newBudgetId).setValue(budget.copy(budgetId = newBudgetId)).await()
        Log.d("FirebaseBudgetRepo", "Budget added successfully with ID: $newBudgetId for user ${user.uid}")
    }

    override suspend fun updateBudget(budget: Budget) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (budget.budgetId.isEmpty()) {
            throw IllegalArgumentException("Cannot update budget with empty ID")
        }
        database.getReference("users").child(user.uid).child("budgets").child(budget.budgetId).setValue(budget).await()
        Log.d("FirebaseBudgetRepo", "Budget updated successfully for budgetId: ${budget.budgetId}")
    }

    override suspend fun deleteBudget(budgetId: String) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (budgetId.isEmpty()) {
            throw IllegalArgumentException("Cannot delete budget with empty ID")
        }
        database.getReference("users").child(user.uid).child("budgets").child(budgetId).removeValue().await()
        Log.d("FirebaseBudgetRepo", "Budget deleted successfully: $budgetId for user ${user.uid}")
    }
}
