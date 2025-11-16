package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Account
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

class FirebaseAccountRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : AccountRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val accountsRef = database.getReference("users").child(userId).child("accounts")

    override fun getAllAccounts(): Flow<List<Account>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accounts = snapshot.children.mapNotNull { it.getValue(Account::class.java) }
                trySend(accounts)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        accountsRef.addValueEventListener(listener)
        awaitClose { accountsRef.removeEventListener(listener) }
    }

    override suspend fun getAccount(accountId: String): Account? {
        val snapshot = accountsRef.child(accountId).get().await()
        return snapshot.getValue(Account::class.java)
    }

    override suspend fun addAccount(account: Account) {
        val accountId = accountsRef.push().key ?: throw IllegalStateException("Could not generate account ID")
        accountsRef.child(accountId).setValue(account.copy(accountId = accountId))
            .addOnSuccessListener {
                Log.d("FirebaseAccountRepo", "Account added successfully: ${account.accountName}")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAccountRepo", "Error adding account", e)
            }
            .await()
    }

    override suspend fun updateAccount(account: Account) {
        accountsRef.child(account.accountId).setValue(account).await()
    }

    override suspend fun deleteAccount(accountId: String) {
        accountsRef.child(accountId).removeValue().await()
    }
}
