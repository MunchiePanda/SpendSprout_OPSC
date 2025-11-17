package com.example.spendsprout_opsc.firebase

import android.util.Log
import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Account_Entity
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class AccountRepository {

    private val TAG = "AccountRepository"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val useFirebase = FirebaseMigrationConfig.useAccounts

    fun getAllAccounts(): Flow<List<Account_Entity>> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllAccountsFromFirebase()
        } else {
            getAllAccountsFromRoom()
        }
    }

    suspend fun insertAccount(account: Account_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            insertAccountToFirebase(account)
            syncToRoom(account)
        } else {
            insertAccountToRoom(account)
            if (isUserAuthenticated()) {
                syncToFirebase(account)
            }
        }
    }

    suspend fun updateAccount(account: Account_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            updateAccountInFirebase(account)
            syncToRoom(account)
        } else {
            updateAccountInRoom(account)
            if (isUserAuthenticated()) {
                syncToFirebase(account)
            }
        }
    }

    suspend fun deleteAccount(account: Account_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            deleteAccountFromFirebase(account.id)
            deleteAccountFromRoom(account)
        } else {
            deleteAccountFromRoom(account)
            if (isUserAuthenticated()) {
                deleteAccountFromFirebase(account.id)
            }
        }
    }

    suspend fun getAccountById(id: Int): Account_Entity? {
        return if (useFirebase && isUserAuthenticated()) {
            getAccountByIdFromFirebase(id)
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.accountDao().getById(id) }
        }
    }

    suspend fun getTotalBalance(): Double {
        return if (useFirebase && isUserAuthenticated()) {
            val accounts = getAllAccountsFromFirebaseSingle()
            accounts.sumOf { it.accountBalance }
        } else {
            BudgetApp.db.accountDao().getTotalBalance() ?: 0.0
        }
    }

    private fun isUserAuthenticated(): Boolean = auth.currentUser != null

    private fun getUserId(): String = auth.currentUser?.uid ?: "anonymous"

    private fun getAccountsReference(): DatabaseReference {
        val userId = getUserId()
        val path = "users/$userId/accounts"
        Log.d(TAG, "getAccountsReference: userId=$userId, path=$path")
        return database.reference.child("users").child(userId).child("accounts")
    }

    private fun getAccountReference(id: Int): DatabaseReference {
        return getAccountsReference().child(id.toString())
    }

    private fun getAllAccountsFromFirebase(): Flow<List<Account_Entity>> = callbackFlow {
        val isAuthenticated = isUserAuthenticated()
        val userId = getUserId()
        Log.d(TAG, "getAllAccountsFromFirebase: authenticated=$isAuthenticated, userId=$userId")

        if (!isAuthenticated) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = getAccountsReference()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accounts = snapshot.children.mapNotNull { it.toAccountEntity() }
                Log.d(TAG, "Accounts snapshot parsed: ${accounts.size} records")
                trySend(accounts.sortedBy { it.accountName })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to accounts: ${error.message}", error.toException())
                trySend(emptyList())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private suspend fun getAllAccountsFromFirebaseSingle(): List<Account_Entity> {
        return suspendCancellableCoroutine { continuation ->
            val ref = getAccountsReference()
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.children.mapNotNull { it.toAccountEntity() })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching accounts once: ${error.message}", error.toException())
                    continuation.resume(emptyList())
                }
            })
        }
    }

    private suspend fun getAccountByIdFromFirebase(id: Int): Account_Entity? {
        return suspendCancellableCoroutine { continuation ->
            val ref = getAccountReference(id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.toAccountEntity())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching account $id: ${error.message}", error.toException())
                    continuation.resume(null)
                }
            })
        }
    }

    private suspend fun insertAccountToFirebase(account: Account_Entity) {
        val ref = getAccountReference(account.id)
        ref.setValue(account.toFirebaseMap()).await()
        Log.d(TAG, "Account inserted to Firebase: ${account.accountName}")
    }

    private suspend fun updateAccountInFirebase(account: Account_Entity) {
        val ref = getAccountReference(account.id)
        ref.updateChildren(account.toFirebaseMap()).await()
        Log.d(TAG, "Account updated in Firebase: ${account.accountName}")
    }

    private suspend fun deleteAccountFromFirebase(id: Int) {
        val ref = getAccountReference(id)
        ref.removeValue().await()
        Log.d(TAG, "Account deleted from Firebase: $id")
    }

    private fun getAllAccountsFromRoom(): Flow<List<Account_Entity>> {
        return BudgetApp.db.accountDao().getAll()
    }

    private suspend fun insertAccountToRoom(account: Account_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.accountDao().insert(account) }
    }

    private suspend fun updateAccountInRoom(account: Account_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.accountDao().update(account) }
    }

    private suspend fun deleteAccountFromRoom(account: Account_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.accountDao().delete(account) }
    }

    private suspend fun syncToFirebase(account: Account_Entity) {
        if (!isUserAuthenticated()) return
        try {
            insertAccountToFirebase(account)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync account to Firebase (non-critical)", e)
        }
    }

    private suspend fun syncToRoom(account: Account_Entity) {
        try {
            withContext(Dispatchers.IO) {
                val dao = BudgetApp.db.accountDao()
                val existing = dao.getById(account.id)
                if (existing == null) {
                    dao.insert(account)
                } else {
                    dao.update(account)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync account to Room (non-critical)", e)
        }
    }

    private fun DataSnapshot.toAccountEntity(): Account_Entity? {
        return try {
            val id = child("id").getValue(Int::class.java)
                ?: key?.toIntOrNull()
                ?: return null.also {
                    Log.w(TAG, "Account snapshot missing ID. key=$key")
                }

            val name = child("accountName").getValue(String::class.java) ?: ""
            val balance = child("accountBalance").getValue(Double::class.java)
                ?: child("accountBalance").getValue(Long::class.java)?.toDouble()
                ?: 0.0
            val notes = child("accountNotes").getValue(String::class.java)
            val typeValue = child("accountType").getValue(String::class.java) ?: AccountType.Cash.name
            val type = runCatching { AccountType.valueOf(typeValue) }.getOrDefault(AccountType.Cash)

            Account_Entity(
                id = id,
                accountName = name,
                accountType = type,
                accountBalance = balance,
                accountNotes = notes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting snapshot to Account_Entity. Key=$key", e)
            null
        }
    }

    private fun Account_Entity.toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "accountName" to accountName,
            "accountType" to accountType.name,
            "accountBalance" to accountBalance,
            "accountNotes" to accountNotes
        )
    }
}

