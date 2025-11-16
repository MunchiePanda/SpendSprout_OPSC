package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Transaction
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

class FirebaseTransactionRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val authState: @JvmSuppressWildcards Flow<FirebaseUser?>
) : TransactionRepository {

    private val tag = "FirebaseTransactionRepo"

    override fun getAllTransactions(): Flow<List<Transaction>> = authState.flatMapLatest { user ->
        if (user == null) {
            Log.w(tag, "getAllTransactions: No user logged in.")
            callbackFlow { trySend(emptyList()) }
        } else {
            val transactionsRef = database.getReference("users").child(user.uid).child("transactions")
            callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val transactions = snapshot.children.mapNotNull { txSnapshot ->
                            txSnapshot.getValue<Transaction>()?.copy(id = txSnapshot.key ?: "")
                        }
                        trySend(transactions)
                        Log.d(tag, "Loaded ${transactions.size} transactions.")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(tag, "getAllTransactions failed", error.toException())
                        close(error.toException())
                    }
                }
                transactionsRef.addValueEventListener(listener)
                awaitClose { transactionsRef.removeEventListener(listener) }
            }
        }
    }

    override suspend fun getTransaction(transactionId: String): Transaction? {
        val user = authState.first() ?: return null
        return try {
            val snapshot = database.getReference("users").child(user.uid).child("transactions").child(transactionId).get().await()
            snapshot.getValue<Transaction>()?.copy(id = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e(tag, "getTransaction failed for id: $transactionId", e)
            null
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        val transactionsRef = database.getReference("users").child(user.uid).child("transactions")
        val transactionId = transactionsRef.push().key ?: throw IllegalStateException("Could not generate transaction ID")
        transactionsRef.child(transactionId).setValue(transaction.copy(id = transactionId)).await()
        Log.d(tag, "addTransaction successful for id: $transactionId")
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (transaction.id.isEmpty()) throw IllegalArgumentException("Transaction ID cannot be empty")
        database.getReference("users").child(user.uid).child("transactions").child(transaction.id).setValue(transaction).await()
        Log.d(tag, "updateTransaction successful for id: ${transaction.id}")
    }

    override suspend fun deleteTransaction(transactionId: String) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (transactionId.isEmpty()) throw IllegalArgumentException("Transaction ID cannot be empty")
        database.getReference("users").child(user.uid).child("transactions").child(transactionId).removeValue().await()
        Log.d(tag, "deleteTransaction successful for id: $transactionId")
    }
}
