package com.example.spendsprout_opsc.firebase

import android.util.Log
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SubcategoryRepository {

    private val TAG = "SubcategoryRepository"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val useFirebase = FirebaseMigrationConfig.useSubcategories

    fun getAllSubcategories(): Flow<List<Subcategory_Entity>> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllSubcategoriesFromFirebase()
        } else {
            getAllSubcategoriesFromRoom()
        }
    }

    suspend fun insertSubcategory(subcategory: Subcategory_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            insertSubcategoryToFirebase(subcategory)
            syncToRoom(subcategory)
        } else {
            insertSubcategoryToRoom(subcategory)
            if (isUserAuthenticated()) {
                syncToFirebase(subcategory)
            }
        }
    }

    suspend fun updateSubcategory(subcategory: Subcategory_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            updateSubcategoryInFirebase(subcategory)
            syncToRoom(subcategory)
        } else {
            updateSubcategoryInRoom(subcategory)
            if (isUserAuthenticated()) {
                syncToFirebase(subcategory)
            }
        }
    }

    suspend fun deleteSubcategory(subcategory: Subcategory_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            deleteSubcategoryFromFirebase(subcategory.id)
            deleteSubcategoryFromRoom(subcategory)
        } else {
            deleteSubcategoryFromRoom(subcategory)
            if (isUserAuthenticated()) {
                deleteSubcategoryFromFirebase(subcategory.id)
            }
        }
    }

    suspend fun getSubcategoryById(id: Int): Subcategory_Entity? {
        return if (useFirebase && isUserAuthenticated()) {
            getSubcategoryByIdFromFirebase(id)
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.subcategoryDao().getById(id) }
        }
    }

    suspend fun getByCategoryId(categoryId: Long): List<Subcategory_Entity> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllSubcategoriesFromFirebaseOnce().filter { it.categoryId.toLong() == categoryId }
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.subcategoryDao().getByCategoryId(categoryId) }
        }
    }

    private fun isUserAuthenticated(): Boolean = auth.currentUser != null

    private fun getUserId(): String = auth.currentUser?.uid ?: "anonymous"

    private fun getSubcategoriesReference(): DatabaseReference {
        val userId = getUserId()
        val path = "users/$userId/subcategories"
        Log.d(TAG, "getSubcategoriesReference: userId=$userId, path=$path")
        return database.reference.child("users").child(userId).child("subcategories")
    }

    private fun getSubcategoryReference(id: Int): DatabaseReference =
        getSubcategoriesReference().child(id.toString())

    private fun getAllSubcategoriesFromFirebase(): Flow<List<Subcategory_Entity>> = callbackFlow {
        if (!isUserAuthenticated()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = getSubcategoriesReference()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subcategories = snapshot.children.mapNotNull { it.toSubcategoryEntity() }
                trySend(subcategories.sortedBy { it.subcategoryName })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to subcategories: ${error.message}", error.toException())
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private suspend fun getAllSubcategoriesFromFirebaseOnce(): List<Subcategory_Entity> {
        return suspendCancellableCoroutine { continuation ->
            val ref = getSubcategoriesReference()
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.children.mapNotNull { it.toSubcategoryEntity() })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching subcategories once: ${error.message}", error.toException())
                    continuation.resume(emptyList())
                }
            })
        }
    }

    private suspend fun getSubcategoryByIdFromFirebase(id: Int): Subcategory_Entity? {
        return suspendCancellableCoroutine { continuation ->
            val ref = getSubcategoryReference(id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.toSubcategoryEntity())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching subcategory $id: ${error.message}", error.toException())
                    continuation.resume(null)
                }
            })
        }
    }

    private suspend fun insertSubcategoryToFirebase(subcategory: Subcategory_Entity) {
        getSubcategoryReference(subcategory.id).setValue(subcategory.toFirebaseMap()).await()
        Log.d(TAG, "Subcategory inserted to Firebase: ${subcategory.subcategoryName}")
    }

    private suspend fun updateSubcategoryInFirebase(subcategory: Subcategory_Entity) {
        getSubcategoryReference(subcategory.id).updateChildren(subcategory.toFirebaseMap()).await()
        Log.d(TAG, "Subcategory updated in Firebase: ${subcategory.subcategoryName}")
    }

    private suspend fun deleteSubcategoryFromFirebase(id: Int) {
        getSubcategoryReference(id).removeValue().await()
        Log.d(TAG, "Subcategory deleted from Firebase: $id")
    }

    private fun getAllSubcategoriesFromRoom(): Flow<List<Subcategory_Entity>> {
        return kotlinx.coroutines.flow.flow {
            emit(withContext(Dispatchers.IO) { BudgetApp.db.subcategoryDao().getAll() })
        }
    }

    private suspend fun insertSubcategoryToRoom(subcategory: Subcategory_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.subcategoryDao().insert(subcategory) }
    }

    private suspend fun updateSubcategoryInRoom(subcategory: Subcategory_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.subcategoryDao().update(subcategory) }
    }

    private suspend fun deleteSubcategoryFromRoom(subcategory: Subcategory_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.subcategoryDao().delete(subcategory) }
    }

    private suspend fun syncToFirebase(subcategory: Subcategory_Entity) {
        if (!isUserAuthenticated()) return
        try {
            insertSubcategoryToFirebase(subcategory)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync subcategory to Firebase (non-critical)", e)
        }
    }

    private suspend fun syncToRoom(subcategory: Subcategory_Entity) {
        try {
            withContext(Dispatchers.IO) {
                val dao = BudgetApp.db.subcategoryDao()
                val existing = dao.getById(subcategory.id)
                if (existing == null) {
                    dao.insert(subcategory)
                } else {
                    dao.update(subcategory)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync subcategory to Room (non-critical)", e)
        }
    }

    private fun DataSnapshot.toSubcategoryEntity(): Subcategory_Entity? {
        return try {
            val id = child("id").getValue(Int::class.java)
                ?: key?.toIntOrNull()
                ?: return null.also {
                    Log.w(TAG, "Subcategory snapshot missing ID. key=$key")
                }

            val categoryId = child("categoryId").getValue(Int::class.java)
                ?: child("categoryId").getValue(Long::class.java)?.toInt()
                ?: 0
            val name = child("subcategoryName").getValue(String::class.java) ?: ""
            val color = child("subcategoryColor").getValue(Int::class.java)
                ?: child("subcategoryColor").getValue(Long::class.java)?.toInt()
                ?: 0
            val balance = child("subcategoryBalance").getValue(Double::class.java)
                ?: child("subcategoryBalance").getValue(Long::class.java)?.toDouble()
                ?: 0.0
            val allocation = child("subcategoryAllocation").getValue(Double::class.java)
                ?: child("subcategoryAllocation").getValue(Long::class.java)?.toDouble()
                ?: 0.0
            val notes = child("subcategoryNotes").getValue(String::class.java)

            Subcategory_Entity(
                id = id,
                categoryId = categoryId,
                subcategoryName = name,
                subcategoryColor = color,
                subcategoryBalance = balance,
                subcategoryAllocation = allocation,
                subcategoryNotes = notes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting snapshot to Subcategory_Entity. Key=$key", e)
            null
        }
    }

    private fun Subcategory_Entity.toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "categoryId" to categoryId,
            "subcategoryName" to subcategoryName,
            "subcategoryColor" to subcategoryColor,
            "subcategoryBalance" to subcategoryBalance,
            "subcategoryAllocation" to subcategoryAllocation,
            "subcategoryNotes" to subcategoryNotes
        )
    }
}

