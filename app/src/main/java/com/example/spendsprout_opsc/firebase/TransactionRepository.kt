package com.example.spendsprout_opsc.firebase

import android.util.Log
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.roomdb.CategoryTotal
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TransactionRepository {

    private val TAG = "TransactionRepository"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val useFirebase = FirebaseMigrationConfig.useTransactions

    fun getAllTransactions(): Flow<List<Expense_Entity>> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllTransactionsFromFirebase()
        } else {
            flow { emit(withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().getAll() }) }
        }
    }

    fun getRecentTransactions(limit: Int): Flow<List<Expense_Entity>> {
        return if (useFirebase && isUserAuthenticated()) {
            flow {
                val transactions = getAllTransactionsFromFirebaseOnce()
                    .sortedByDescending { it.expenseDate }
                    .take(limit)
                emit(transactions)
            }
        } else {
            flow {
                val all = withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().getAll() }
                emit(all.sortedByDescending { it.expenseDate }.take(limit))
            }
        }
    }

    suspend fun insertTransaction(transaction: Expense_Entity) {
        val resolved = if (transaction.id == 0L) {
            transaction.copy(id = generateTransactionId())
        } else transaction

        if (useFirebase && isUserAuthenticated()) {
            insertTransactionToFirebase(resolved)
            syncToRoom(resolved)
        } else {
            insertTransactionToRoom(resolved)
            if (isUserAuthenticated()) {
                syncToFirebase(resolved)
            }
        }
    }

    suspend fun updateTransaction(transaction: Expense_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            updateTransactionInFirebase(transaction)
            syncToRoom(transaction)
        } else {
            updateTransactionInRoom(transaction)
            if (isUserAuthenticated()) {
                syncToFirebase(transaction)
            }
        }
    }

    suspend fun deleteTransaction(transaction: Expense_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            deleteTransactionFromFirebase(transaction.id)
            deleteTransactionFromRoom(transaction)
        } else {
            deleteTransactionFromRoom(transaction)
            if (isUserAuthenticated()) {
                deleteTransactionFromFirebase(transaction.id)
            }
        }
    }

    suspend fun getTransactionById(id: Long): Expense_Entity? {
        return if (useFirebase && isUserAuthenticated()) {
            getTransactionByIdFromFirebase(id)
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().getById(id) }
        }
    }

    suspend fun getTotalIncome(): Double {
        return if (useFirebase && isUserAuthenticated()) {
            getAllTransactionsFromFirebaseOnce()
                .filter { it.expenseType == ExpenseType.Income }
                .sumOf { it.expenseAmount }
        } else {
            val all = withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().getAll() }
            all.filter { it.expenseType == ExpenseType.Income }.sumOf { it.expenseAmount }
        }
    }

    suspend fun getTotalExpenses(): Double {
        return if (useFirebase && isUserAuthenticated()) {
            getAllTransactionsFromFirebaseOnce()
                .filter { it.expenseType == ExpenseType.Expense }
                .sumOf { it.expenseAmount }
        } else {
            val all = withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().getAll() }
            all.filter { it.expenseType == ExpenseType.Expense }.sumOf { it.expenseAmount }
        }
    }

    suspend fun getTransactionsBetweenDates(startDate: Long, endDate: Long): List<Expense_Entity> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllTransactionsFromFirebaseOnce().filter {
                it.expenseDate in startDate..endDate
            }
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().loadAllBetweenDates(startDate, endDate) }
        }
    }

    suspend fun getTransactionsBetweenAmounts(startAmount: Double, endAmount: Double): List<Expense_Entity> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllTransactionsFromFirebaseOnce().filter {
                it.expenseAmount in startAmount..endAmount
            }
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().loadAllBetweenAmounts(startAmount, endAmount) }
        }
    }

    suspend fun getAllTransactionsSnapshot(): List<Expense_Entity> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllTransactionsFromFirebaseOnce()
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().getAll() }
        }
    }

    suspend fun getCategoryTotals(start: Long, end: Long): List<CategoryTotal> {
        return if (useFirebase && isUserAuthenticated()) {
            val expenses = getTransactionsBetweenDates(start, end)
            expenses.groupBy { it.expenseCategory }
                .map { CategoryTotal(it.key, it.value.sumOf { exp -> exp.expenseAmount }) }
        } else {
            BudgetApp.db.expenseDao().totalsByCategory(start, end)
        }
    }

    private fun isUserAuthenticated(): Boolean = auth.currentUser != null

    private fun getUserId(): String = auth.currentUser?.uid ?: "anonymous"

    private fun getTransactionsReference(): DatabaseReference {
        val userId = getUserId()
        val path = "users/$userId/transactions"
        Log.d(TAG, "getTransactionsReference: userId=$userId, path=$path")
        return database.reference.child("users").child(userId).child("transactions")
    }

    private fun getTransactionReference(id: Long): DatabaseReference =
        getTransactionsReference().child(id.toString())

    private fun getAllTransactionsFromFirebase(): Flow<List<Expense_Entity>> = callbackFlow {
        if (!isUserAuthenticated()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = getTransactionsReference()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { it.toExpenseEntity() }
                trySend(transactions.sortedByDescending { it.expenseDate })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to transactions: ${error.message}", error.toException())
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private suspend fun getAllTransactionsFromFirebaseOnce(): List<Expense_Entity> {
        return suspendCancellableCoroutine { continuation ->
            val ref = getTransactionsReference()
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.children.mapNotNull { it.toExpenseEntity() })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching transactions once: ${error.message}", error.toException())
                    continuation.resume(emptyList())
                }
            })
        }
    }

    private suspend fun getTransactionByIdFromFirebase(id: Long): Expense_Entity? {
        return suspendCancellableCoroutine { continuation ->
            val ref = getTransactionReference(id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.toExpenseEntity())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching transaction $id: ${error.message}", error.toException())
                    continuation.resume(null)
                }
            })
        }
    }

    private suspend fun insertTransactionToFirebase(transaction: Expense_Entity) {
        getTransactionReference(transaction.id).setValue(transaction.toFirebaseMap()).await()
        Log.d(TAG, "Transaction inserted to Firebase: ${transaction.id}")
    }

    private suspend fun updateTransactionInFirebase(transaction: Expense_Entity) {
        getTransactionReference(transaction.id).updateChildren(transaction.toFirebaseMap()).await()
        Log.d(TAG, "Transaction updated in Firebase: ${transaction.id}")
    }

    private suspend fun deleteTransactionFromFirebase(id: Long) {
        getTransactionReference(id).removeValue().await()
        Log.d(TAG, "Transaction deleted from Firebase: $id")
    }

    private suspend fun insertTransactionToRoom(transaction: Expense_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().insert(transaction) }
    }

    private suspend fun updateTransactionInRoom(transaction: Expense_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().update(transaction) }
    }

    private suspend fun deleteTransactionFromRoom(transaction: Expense_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.expenseDao().delete(transaction) }
    }

    private suspend fun syncToFirebase(transaction: Expense_Entity) {
        if (!isUserAuthenticated()) return
        try {
            insertTransactionToFirebase(transaction)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync transaction to Firebase (non-critical)", e)
        }
    }

    private suspend fun syncToRoom(transaction: Expense_Entity) {
        try {
            withContext(Dispatchers.IO) {
                val dao = BudgetApp.db.expenseDao()
                val existing = dao.getById(transaction.id)
                if (existing == null) {
                    dao.insert(transaction)
                } else {
                    dao.update(transaction)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync transaction to Room (non-critical)", e)
        }
    }

    private fun DataSnapshot.toExpenseEntity(): Expense_Entity? {
        return try {
            val id = child("id").getValue(Long::class.java)
                ?: key?.toLongOrNull()
                ?: return null.also {
                    Log.w(TAG, "Transaction snapshot missing ID. key=$key")
                }

            val name = child("expenseName").getValue(String::class.java) ?: ""
            val date = child("expenseDate").getValue(Long::class.java) ?: 0L
            val amount = child("expenseAmount").getValue(Double::class.java)
                ?: child("expenseAmount").getValue(Long::class.java)?.toDouble()
                ?: 0.0
            val typeValue = child("expenseType").getValue(String::class.java) ?: ExpenseType.Expense.name
            val type = runCatching { ExpenseType.valueOf(typeValue) }.getOrDefault(ExpenseType.Expense)
            val isOwed = child("expenseIsOwed").getValue(Boolean::class.java) ?: false
            val repeatValue = child("expenseRepeat").getValue(String::class.java) ?: RepeatType.None.name
            val repeatType = runCatching { RepeatType.valueOf(repeatValue) }.getOrDefault(RepeatType.None)
            val notes = child("expenseNotes").getValue(String::class.java)
            val image = child("expenseImage").getValue(String::class.java)
            val category = child("expenseCategory").getValue(String::class.java) ?: ""
            val start = child("expenseStart").getValue(Long::class.java)
            val end = child("expenseEnd").getValue(Long::class.java)

            Expense_Entity(
                id = id,
                expenseName = name,
                expenseDate = date,
                expenseAmount = amount,
                expenseType = type,
                expenseIsOwed = isOwed,
                expenseRepeat = repeatType,
                expenseNotes = notes,
                expenseImage = image,
                expenseCategory = category,
                expenseStart = start,
                expenseEnd = end
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting snapshot to Expense_Entity. Key=$key", e)
            null
        }
    }

    private fun Expense_Entity.toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "expenseName" to expenseName,
            "expenseDate" to expenseDate,
            "expenseAmount" to expenseAmount,
            "expenseType" to expenseType.name,
            "expenseIsOwed" to expenseIsOwed,
            "expenseRepeat" to expenseRepeat.name,
            "expenseNotes" to expenseNotes,
            "expenseImage" to expenseImage,
            "expenseCategory" to expenseCategory,
            "expenseStart" to expenseStart,
            "expenseEnd" to expenseEnd
        )
    }

    private fun generateTransactionId(): Long {
        return System.currentTimeMillis()
    }
}

