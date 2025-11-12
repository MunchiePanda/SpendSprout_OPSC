package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.firebase.FirebaseConstants
import com.example.spendsprout_opsc.roomdb.Account_Entity
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
import kotlinx.coroutines.tasks.await

@Singleton
class AccountRepository @Inject constructor(
    @Named("rootDatabaseReference") rootReference: DatabaseReference,
) {

    private val accountsReference: DatabaseReference =
        rootReference
            .child(FirebaseConstants.USERS_NODE)
            .child(FirebaseConstants.DEFAULT_USER_ID)
            .child(FirebaseConstants.ACCOUNTS_NODE)

    fun getAllAccounts(): Flow<List<Account_Entity>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accounts = snapshot.children.mapNotNull { child ->
                    child.getValue(Account_Entity::class.java)?.apply {
                        if (id == 0) {
                            id = child.key?.toIntOrNull() ?: id
                        }
                    }
                }
                trySend(accounts).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        accountsReference.addValueEventListener(listener)
        awaitClose { accountsReference.removeEventListener(listener) }
    }

    suspend fun getAccountById(id: Int): Account_Entity? {
        val snapshot = accountsReference.child(id.toString()).get().await()
        return snapshot.getValue(Account_Entity::class.java)?.apply { this.id = id }
    }

    suspend fun insertAccount(account: Account_Entity) {
        val accountId = if (account.id != 0) account.id else generateNextAccountId()
        accountsReference
            .child(accountId.toString())
            .setValue(account.copy(id = accountId))
            .await()
    }

    suspend fun updateAccount(account: Account_Entity) {
        if (account.id == 0) return
        accountsReference
            .child(account.id.toString())
            .setValue(account)
            .await()
    }

    suspend fun deleteAccount(account: Account_Entity) {
        if (account.id == 0) return
        accountsReference
            .child(account.id.toString())
            .removeValue()
            .await()
    }

    suspend fun getTotalBalance(): Double {
        val snapshot = accountsReference.get().await()
        return snapshot.children.sumOf { child ->
            child.child("accountBalance").getValue(Double::class.java) ?: 0.0
        }
    }

    private suspend fun generateNextAccountId(): Int {
        val snapshot = accountsReference.get().await()
        val maxId = snapshot.children
            .mapNotNull { child -> child.key?.toIntOrNull() }
            .maxOrNull() ?: 0
        return maxId + 1
    }
}
