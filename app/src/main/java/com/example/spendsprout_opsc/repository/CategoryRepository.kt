package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.firebase.FirebaseConstants
import com.example.spendsprout_opsc.roomdb.Category_Entity
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
class CategoryRepository @Inject constructor(
    @Named("rootDatabaseReference") rootReference: DatabaseReference,
) {

    private val categoriesReference: DatabaseReference =
        rootReference
            .child(FirebaseConstants.USERS_NODE)
            .child(FirebaseConstants.DEFAULT_USER_ID)
            .child(FirebaseConstants.CATEGORIES_NODE)

    fun getAllCategories(): Flow<List<Category_Entity>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { child ->
                    child.getValue(Category_Entity::class.java)?.apply {
                        if (id == 0) {
                            id = child.key?.toIntOrNull() ?: id
                        }
                    }
                }
                trySend(categories).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        categoriesReference.addValueEventListener(listener)
        awaitClose { categoriesReference.removeEventListener(listener) }
    }

    suspend fun getCategoryById(id: Int): Category_Entity? {
        val snapshot = categoriesReference.child(id.toString()).get().await()
        return snapshot.getValue(Category_Entity::class.java)?.apply { this.id = id }
    }

    suspend fun insertCategory(category: Category_Entity) {
        val categoryId = if (category.id != 0) category.id else generateNextCategoryId()
        categoriesReference
            .child(categoryId.toString())
            .setValue(category.copy(id = categoryId))
            .await()
    }

    suspend fun updateCategory(category: Category_Entity) {
        if (category.id == 0) return
        categoriesReference
            .child(category.id.toString())
            .setValue(category)
            .await()
    }

    suspend fun deleteCategory(category: Category_Entity) {
        if (category.id == 0) return
        categoriesReference
            .child(category.id.toString())
            .removeValue()
            .await()
    }

    suspend fun getTotalAllocation(): Double {
        val snapshot = categoriesReference.get().await()
        return snapshot.children.sumOf { child ->
            child.child("categoryAllocation").getValue(Double::class.java) ?: 0.0
        }
    }

    suspend fun getTotalSpent(): Double {
        val snapshot = categoriesReference.get().await()
        return snapshot.children.sumOf { child ->
            child.child("categoryBalance").getValue(Double::class.java) ?: 0.0
        }
    }

    private suspend fun generateNextCategoryId(): Int {
        val snapshot = categoriesReference.get().await()
        val maxId = snapshot.children
            .mapNotNull { child -> child.key?.toIntOrNull() }
            .maxOrNull() ?: 0
        return maxId + 1
    }
}
