package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.firebase.FirebaseConstants
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@Singleton
class TransactionRepository @Inject constructor(
    @Named("rootDatabaseReference") rootReference: DatabaseReference,
) {

    private val transactionsReference: DatabaseReference =
        rootReference
            .child(FirebaseConstants.USERS_NODE)
            .child(FirebaseConstants.DEFAULT_USER_ID)
            .child(FirebaseConstants.TRANSACTIONS_NODE)

    fun getAllTransactions(): Flow<List<Expense_Entity>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { child ->
                    child.getValue(Expense_Entity::class.java)?.apply {
                        if (id == 0L) {
                            id = child.key?.toLongOrNull() ?: id
                        }
                    }
                }
                trySend(transactions).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        transactionsReference.addValueEventListener(listener)
        awaitClose { transactionsReference.removeEventListener(listener) }
    }

    fun getRecentTransactions(limit: Int): Flow<List<Expense_Entity>> =
        getAllTransactions().map { transactions ->
            transactions.sortedByDescending { it.expenseDate }.take(limit)
        }

    fun getTransactionsByType(type: String): Flow<List<Expense_Entity>> =
        getAllTransactions().map { transactions ->
            val normalized = type.trim().lowercase()
            transactions.filter { it.expenseType.name.lowercase() == normalized }
        }

    suspend fun insertTransaction(transaction: Expense_Entity) {
        val transactionId = if (transaction.id != 0L) transaction.id else generateNextTransactionId()
        transactionsReference
            .child(transactionId.toString())
            .setValue(transaction.copy(id = transactionId))
            .await()
    }

    suspend fun updateTransaction(transaction: Expense_Entity) {
        if (transaction.id == 0L) return
        transactionsReference
            .child(transaction.id.toString())
            .setValue(transaction)
            .await()
    }

    suspend fun deleteTransaction(transaction: Expense_Entity) {
        if (transaction.id == 0L) return
        transactionsReference
            .child(transaction.id.toString())
            .removeValue()
            .await()
    }

    suspend fun getTransactionById(id: Long): Expense_Entity? {
        val snapshot = transactionsReference.child(id.toString()).get().await()
        return snapshot.getValue(Expense_Entity::class.java)?.apply { this.id = id }
    }

    suspend fun getTotalIncome(): Double {
        val snapshot = transactionsReference.get().await()
        return snapshot.children
            .mapNotNull { it.getValue(Expense_Entity::class.java) }
            .filter { it.expenseType == ExpenseType.Income }
            .sumOf { it.expenseAmount }
    }

    suspend fun getTotalExpenses(): Double {
        val snapshot = transactionsReference.get().await()
        return snapshot.children
            .mapNotNull { it.getValue(Expense_Entity::class.java) }
            .filter { it.expenseType == ExpenseType.Expense }
            .sumOf { it.expenseAmount }
    }

    suspend fun getTransactionsBetweenDates(startDate: Long, endDate: Long): List<Expense_Entity> {
        val snapshot = transactionsReference.get().await()
        return snapshot.children
            .mapNotNull { it.getValue(Expense_Entity::class.java) }
            .filter { it.expenseDate in startDate..endDate }
    }

    suspend fun getTransactionsBetweenAmounts(startAmount: Double, endAmount: Double): List<Expense_Entity> {
        val snapshot = transactionsReference.get().await()
        return snapshot.children
            .mapNotNull { it.getValue(Expense_Entity::class.java) }
            .filter { it.expenseAmount in startAmount..endAmount }
    }

    private suspend fun generateNextTransactionId(): Long {
        val snapshot = transactionsReference.get().await()
        val maxId = snapshot.children
            .mapNotNull { it.key?.toLongOrNull() }
            .maxOrNull() ?: 0L
        return maxId + 1
    }
}
