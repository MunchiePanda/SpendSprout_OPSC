package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Transaction
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

class FirebaseTransactionRepository @Inject constructor(
    private val database: FirebaseDatabase
) : TransactionRepository {

    private val tag = "FirebaseTransactionRepo"
    private val transactionsRef = database.getReference("transactions")

    override fun getAllTransactions(): Flow<List<Transaction>> = callbackFlow {
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

    override suspend fun getTransaction(transactionId: String): Transaction? {
        return try {
            val snapshot = transactionsRef.child(transactionId).get().await()
            snapshot.getValue<Transaction>()?.copy(id = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e(tag, "getTransaction failed for id: $transactionId", e)
            null
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val transactionId = transactionsRef.push().key ?: throw IllegalStateException("Could not generate transaction ID")
        transactionsRef.child(transactionId).setValue(transaction.copy(id = transactionId)).await()
        Log.d(tag, "addTransaction successful for id: $transactionId")
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        if (transaction.id.isEmpty()) throw IllegalArgumentException("Transaction ID cannot be empty")
        transactionsRef.child(transaction.id).setValue(transaction).await()
        Log.d(tag, "updateTransaction successful for id: ${transaction.id}")
    }

    override suspend fun deleteTransaction(transactionId: String) {
        if (transactionId.isEmpty()) throw IllegalArgumentException("Transaction ID cannot be empty")
        transactionsRef.child(transactionId).removeValue().await()
        Log.d(tag, "deleteTransaction successful for id: $transactionId")
    }
}
