package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTransactionRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : TransactionRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val transactionsRef = database.getReference("users").child(userId).child("transactions")

    override fun getAllTransactions(): Flow<List<Transaction>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { it.getValue(Transaction::class.java) }
                trySend(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        transactionsRef.addValueEventListener(listener)
        awaitClose { transactionsRef.removeEventListener(listener) }
    }

    override suspend fun getTransaction(transactionId: String): Transaction? {
        val snapshot = transactionsRef.child(transactionId).get().await()
        return snapshot.getValue(Transaction::class.java)
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val transactionId = transactionsRef.push().key ?: throw IllegalStateException("Could not generate transaction ID")
        transactionsRef.child(transactionId).setValue(transaction.copy(transactionId = transactionId)).await()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionsRef.child(transaction.transactionId).setValue(transaction).await()
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionsRef.child(transactionId).removeValue().await()
    }
}
