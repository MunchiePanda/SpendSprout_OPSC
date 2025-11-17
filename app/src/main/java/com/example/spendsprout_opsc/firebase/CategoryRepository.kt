package com.example.spendsprout_opsc.firebase

import android.util.Log
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Category_Entity
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

class CategoryRepository {

    private val TAG = "CategoryRepository"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val useFirebase = FirebaseMigrationConfig.useCategories

    fun getAllCategories(): Flow<List<Category_Entity>> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllCategoriesFromFirebase()
        } else {
            getAllCategoriesFromRoom()
        }
    }

    suspend fun insertCategory(category: Category_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            insertCategoryToFirebase(category)
            syncToRoom(category)
        } else {
            insertCategoryToRoom(category)
            if (isUserAuthenticated()) {
                syncToFirebase(category)
            }
        }
    }

    suspend fun updateCategory(category: Category_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            updateCategoryInFirebase(category)
            syncToRoom(category)
        } else {
            updateCategoryInRoom(category)
            if (isUserAuthenticated()) {
                syncToFirebase(category)
            }
        }
    }

    suspend fun deleteCategory(category: Category_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            deleteCategoryFromFirebase(category.id)
            deleteCategoryFromRoom(category)
        } else {
            deleteCategoryFromRoom(category)
            if (isUserAuthenticated()) {
                deleteCategoryFromFirebase(category.id)
            }
        }
    }

    suspend fun getCategoryById(id: Int): Category_Entity? {
        return if (useFirebase && isUserAuthenticated()) {
            getCategoryByIdFromFirebase(id)
        } else {
            withContext(Dispatchers.IO) { BudgetApp.db.categoryDao().getById(id) }
        }
    }

    suspend fun getTotalAllocation(): Double {
        return if (useFirebase && isUserAuthenticated()) {
            val categories = getAllCategoriesFromFirebaseOnce()
            categories.sumOf { it.categoryAllocation }
        } else {
            BudgetApp.db.categoryDao().getTotalAllocation() ?: 0.0
        }
    }

    suspend fun getTotalSpent(): Double {
        return if (useFirebase && isUserAuthenticated()) {
            val categories = getAllCategoriesFromFirebaseOnce()
            categories.sumOf { it.categoryBalance }
        } else {
            BudgetApp.db.categoryDao().getTotalSpent() ?: 0.0
        }
    }

    private fun isUserAuthenticated(): Boolean = auth.currentUser != null

    private fun getUserId(): String = auth.currentUser?.uid ?: "anonymous"

    private fun getCategoriesReference(): DatabaseReference {
        val userId = getUserId()
        val path = "users/$userId/categories"
        Log.d(TAG, "getCategoriesReference: userId=$userId, path=$path")
        return database.reference.child("users").child(userId).child("categories")
    }

    private fun getCategoryReference(id: Int): DatabaseReference =
        getCategoriesReference().child(id.toString())

    private fun getAllCategoriesFromFirebase(): Flow<List<Category_Entity>> = callbackFlow {
        if (!isUserAuthenticated()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = getCategoriesReference()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.toCategoryEntity() }
                trySend(categories.sortedBy { it.categoryName })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to categories: ${error.message}", error.toException())
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private suspend fun getAllCategoriesFromFirebaseOnce(): List<Category_Entity> {
        return suspendCancellableCoroutine { continuation ->
            val ref = getCategoriesReference()
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.children.mapNotNull { it.toCategoryEntity() })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching categories once: ${error.message}", error.toException())
                    continuation.resume(emptyList())
                }
            })
        }
    }

    private suspend fun getCategoryByIdFromFirebase(id: Int): Category_Entity? {
        return suspendCancellableCoroutine { continuation ->
            val ref = getCategoryReference(id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.toCategoryEntity())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching category $id: ${error.message}", error.toException())
                    continuation.resume(null)
                }
            })
        }
    }

    private suspend fun insertCategoryToFirebase(category: Category_Entity) {
        getCategoryReference(category.id).setValue(category.toFirebaseMap()).await()
        Log.d(TAG, "Category inserted to Firebase: ${category.categoryName}")
    }

    private suspend fun updateCategoryInFirebase(category: Category_Entity) {
        getCategoryReference(category.id).updateChildren(category.toFirebaseMap()).await()
        Log.d(TAG, "Category updated in Firebase: ${category.categoryName}")
    }

    private suspend fun deleteCategoryFromFirebase(id: Int) {
        getCategoryReference(id).removeValue().await()
        Log.d(TAG, "Category deleted from Firebase: $id")
    }

    private fun getAllCategoriesFromRoom(): Flow<List<Category_Entity>> {
        return BudgetApp.db.categoryDao().getAll()
    }

    private suspend fun insertCategoryToRoom(category: Category_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.categoryDao().insert(category) }
    }

    private suspend fun updateCategoryInRoom(category: Category_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.categoryDao().update(category) }
    }

    private suspend fun deleteCategoryFromRoom(category: Category_Entity) {
        withContext(Dispatchers.IO) { BudgetApp.db.categoryDao().delete(category) }
    }

    private suspend fun syncToFirebase(category: Category_Entity) {
        if (!isUserAuthenticated()) return
        try {
            insertCategoryToFirebase(category)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync category to Firebase (non-critical)", e)
        }
    }

    private suspend fun syncToRoom(category: Category_Entity) {
        try {
            withContext(Dispatchers.IO) {
                val dao = BudgetApp.db.categoryDao()
                val existing = dao.getById(category.id)
                if (existing == null) {
                    dao.insert(category)
                } else {
                    dao.update(category)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync category to Room (non-critical)", e)
        }
    }

    private fun DataSnapshot.toCategoryEntity(): Category_Entity? {
        return try {
            val id = child("id").getValue(Int::class.java)
                ?: key?.toIntOrNull()
                ?: return null.also {
                    Log.w(TAG, "Category snapshot missing ID. key=$key")
                }

            val name = child("categoryName").getValue(String::class.java) ?: ""
            val color = child("categoryColor").getValue(Int::class.java)
                ?: child("categoryColor").getValue(Long::class.java)?.toInt()
                ?: 0
            val balance = child("categoryBalance").getValue(Double::class.java)
                ?: child("categoryBalance").getValue(Long::class.java)?.toDouble()
                ?: 0.0
            val allocation = child("categoryAllocation").getValue(Double::class.java)
                ?: child("categoryAllocation").getValue(Long::class.java)?.toDouble()
                ?: 0.0
            val notes = child("categoryNotes").getValue(String::class.java)

            Category_Entity(
                id = id,
                categoryName = name,
                categoryColor = color,
                categoryBalance = balance,
                categoryAllocation = allocation,
                categoryNotes = notes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting snapshot to Category_Entity. Key=$key", e)
            null
        }
    }

    private fun Category_Entity.toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "categoryName" to categoryName,
            "categoryColor" to categoryColor,
            "categoryBalance" to categoryBalance,
            "categoryAllocation" to categoryAllocation,
            "categoryNotes" to categoryNotes
        )
    }
}

