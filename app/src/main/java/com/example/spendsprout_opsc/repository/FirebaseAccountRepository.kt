package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Account
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

class FirebaseAccountRepository @Inject constructor(
    private val database: FirebaseDatabase,
    // This is the key to solving the problem. It ALWAYS has the correct user.
    private val authState: @JvmSuppressWildcards Flow<FirebaseUser?>
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> = authState.flatMapLatest { user ->
        if (user == null) {
            Log.w("FirebaseAccountRepo", "getAllAccounts: No user logged in.")
            callbackFlow { trySend(emptyList()) }
        } else {
            val accountsRef = database.getReference("users").child(user.uid).child("accounts")
            callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // CRITICAL FIX 1: Map the key to the ID to prevent online/offline bug.
                        val accounts = snapshot.children.mapNotNull { dataSnapshot ->
                            dataSnapshot.getValue<Account>()?.apply {
                                accountId = dataSnapshot.key ?: ""
                            }
                        }
                        trySend(accounts)
                        Log.d("FirebaseAccountRepo", "getAllAccounts: Loaded ${accounts.size} accounts for user ${user.uid}")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseAccountRepo", "getAllAccounts: Database error.", error.toException())
                        close(error.toException())
                    }
                }
                accountsRef.addValueEventListener(listener)
                awaitClose { accountsRef.removeEventListener(listener) }
            }
        }
    }

    override suspend fun getAccount(accountId: String): Account? {
        // CRITICAL FIX 2: Get the user from the authState flow first.
        val user = authState.first() ?: return null
        val snapshot = database.getReference("users").child(user.uid).child("accounts").child(accountId).get().await()
        // CRITICAL FIX 3: Map the key to the ID here as well.
        return snapshot.getValue<Account>()?.apply {
            this.accountId = snapshot.key ?: ""
        }
    }

    override suspend fun addAccount(account: Account) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        val accountsRef = database.getReference("users").child(user.uid).child("accounts")
        val newAccountId = accountsRef.push().key ?: throw IllegalStateException("Could not generate account ID")
        // Always save a copy with the correct ID inside the object
        accountsRef.child(newAccountId).setValue(account.copy(accountId = newAccountId)).await()
        Log.d("FirebaseAccountRepo", "Account added successfully under user ${user.uid}")
    }

    override suspend fun updateAccount(account: Account) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (account.accountId.isEmpty()) {
            throw IllegalArgumentException("Cannot update account with empty ID")
        }
        database.getReference("users").child(user.uid).child("accounts").child(account.accountId).setValue(account).await()
        Log.d("FirebaseAccountRepo", "Account updated successfully for user ${user.uid}")
    }

    override suspend fun deleteAccount(accountId: String) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (accountId.isEmpty()) {
            throw IllegalArgumentException("Cannot delete account with empty ID")
        }
        database.getReference("users").child(user.uid).child("accounts").child(accountId).removeValue().await()
        Log.d("FirebaseAccountRepo", "Account deleted successfully for user ${user.uid}")
    }
}
