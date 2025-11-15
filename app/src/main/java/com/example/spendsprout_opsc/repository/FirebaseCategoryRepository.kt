package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
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

class FirebaseCategoryRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : CategoryRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val categoriesRef = database.getReference("users").child(userId).child("categories")

    override fun getAllCategories(): Flow<List<Category>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                trySend(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        categoriesRef.addValueEventListener(listener)
        awaitClose { categoriesRef.removeEventListener(listener) }
    }

    override suspend fun getCategory(categoryId: String): Category? {
        val snapshot = categoriesRef.child(categoryId).get().await()
        return snapshot.getValue(Category::class.java)
    }

    override suspend fun addCategory(category: Category) {
        val categoryId = categoriesRef.push().key ?: throw IllegalStateException("Could not generate category ID")
        categoriesRef.child(categoryId).setValue(category.copy(categoryId = categoryId)).await()
    }

    override suspend fun updateCategory(category: Category) {
        categoriesRef.child(category.categoryId).setValue(category).await()
    }

    override suspend fun deleteCategory(categoryId: String) {
        categoriesRef.child(categoryId).removeValue().await()
    }

    override suspend fun getSubcategory(categoryId: String, subcategoryId: String): Subcategory? {
        val snapshot = categoriesRef.child(categoryId).child("subcategories").child(subcategoryId).get().await()
        return snapshot.getValue(Subcategory::class.java)
    }

    override suspend fun addSubcategory(categoryId: String, subcategory: Subcategory) {
        val subcategoryId = categoriesRef.child(categoryId).child("subcategories").push().key ?: throw IllegalStateException("Could not generate subcategory ID")
        categoriesRef.child(categoryId).child("subcategories").child(subcategoryId).setValue(subcategory.copy(subcategoryId = subcategoryId)).await()
    }

    override suspend fun updateSubcategory(categoryId: String, subcategory: Subcategory) {
        categoriesRef.child(categoryId).child("subcategories").child(subcategory.subcategoryId).setValue(subcategory).await()
    }

    override suspend fun deleteSubcategory(categoryId: String, subcategoryId: String) {
        categoriesRef.child(categoryId).child("subcategories").child(subcategoryId).removeValue().await()
    }

    override suspend fun addDefaultCategoriesIfEmpty() {
        // TODO: Implement this method to add default categories if there are none in the database.
    }
}
