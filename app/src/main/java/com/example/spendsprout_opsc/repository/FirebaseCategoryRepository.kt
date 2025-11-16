package com.example.spendsprout_opsc.repository

import android.util.Log
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
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

class FirebaseCategoryRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val authState: @JvmSuppressWildcards Flow<FirebaseUser?>
) : CategoryRepository {

    private val tag = "FirebaseCategoryRepo"

    override fun getAllCategories(): Flow<List<Category>> = authState.flatMapLatest { user ->
        if (user == null) {
            Log.w(tag, "getAllCategories: No user logged in.")
            callbackFlow { trySend(emptyList()) }
        } else {
            val categoriesRef = database.getReference("users").child(user.uid).child("categories")
            callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categories = snapshot.children.mapNotNull { catSnapshot ->
                            catSnapshot.getValue<Category>()?.copy(categoryId = catSnapshot.key ?: "")
                        }
                        trySend(categories)
                        Log.d(tag, "Loaded ${categories.size} categories.")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(tag, "getAllCategories failed", error.toException())
                        close(error.toException())
                    }
                }
                categoriesRef.addValueEventListener(listener)
                awaitClose { categoriesRef.removeEventListener(listener) }
            }
        }
    }

    override suspend fun getCategory(categoryId: String): Category? {
        val user = authState.first() ?: return null
        return try {
            val snapshot = database.getReference("users").child(user.uid).child("categories").child(categoryId).get().await()
            snapshot.getValue<Category>()?.copy(categoryId = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e(tag, "getCategory failed for id: $categoryId", e)
            null
        }
    }

    override suspend fun addCategory(category: Category) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        val categoriesRef = database.getReference("users").child(user.uid).child("categories")
        val categoryId = categoriesRef.push().key ?: throw IllegalStateException("Could not generate category ID")
        categoriesRef.child(categoryId).setValue(category.copy(categoryId = categoryId)).await()
        Log.d(tag, "addCategory successful for id: $categoryId")
    }

    override suspend fun updateCategory(category: Category) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (category.categoryId.isEmpty()) throw IllegalArgumentException("Category ID cannot be empty")
        database.getReference("users").child(user.uid).child("categories").child(category.categoryId).setValue(category).await()
        Log.d(tag, "updateCategory successful for id: ${category.categoryId}")
    }

    override suspend fun deleteCategory(categoryId: String) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (categoryId.isEmpty()) throw IllegalArgumentException("Category ID cannot be empty")
        database.getReference("users").child(user.uid).child("categories").child(categoryId).removeValue().await()
        Log.d(tag, "deleteCategory successful for id: $categoryId")
    }

    // Subcategory Methods

    override fun getAllSubcategories(): Flow<List<Subcategory>> = authState.flatMapLatest { user ->
        if (user == null) {
            Log.w(tag, "getAllSubcategories: No user logged in.")
            callbackFlow { trySend(emptyList()) }
        } else {
            val categoriesRef = database.getReference("users").child(user.uid).child("categories")
            callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val allSubcategories = snapshot.children.flatMap { catSnapshot ->
                            val subcategoriesMap = catSnapshot.child("subcategories").getValue<Map<String, Subcategory>>() ?: emptyMap()
                            subcategoriesMap.map { (key, sub) -> sub.copy(subcategoryId = key) } // Ensure ID is set from the key
                        }
                        trySend(allSubcategories)
                        Log.d(tag, "Loaded ${allSubcategories.size} subcategories in total.")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(tag, "getAllSubcategories failed", error.toException())
                        close(error.toException())
                    }
                }
                categoriesRef.addValueEventListener(listener)
                awaitClose { categoriesRef.removeEventListener(listener) }
            }
        }
    }

    override fun getSubcategoriesForCategory(categoryId: String): Flow<List<Subcategory>> = authState.flatMapLatest { user ->
        if (user == null) {
            Log.w(tag, "getSubcategoriesForCategory: No user logged in.")
            callbackFlow { trySend(emptyList()) }
        } else {
            val subcategoriesRef = database.getReference("users").child(user.uid).child("categories").child(categoryId).child("subcategories")
            callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val subcategories = snapshot.children.mapNotNull { subSnapshot ->
                            subSnapshot.getValue<Subcategory>()?.copy(subcategoryId = subSnapshot.key ?: "")
                        }
                        trySend(subcategories)
                        Log.d(tag, "Loaded ${subcategories.size} subcategories for category $categoryId.")
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e(tag, "getSubcategoriesForCategory failed for category $categoryId", error.toException())
                        close(error.toException())
                    }
                }
                subcategoriesRef.addValueEventListener(listener)
                awaitClose { subcategoriesRef.removeEventListener(listener) }
            }
        }
    }

    override suspend fun getSubcategory(categoryId: String, subcategoryId: String): Subcategory? {
        val user = authState.first() ?: return null
        return try {
            val snapshot = database.getReference("users").child(user.uid).child("categories").child(categoryId).child("subcategories").child(subcategoryId).get().await()
            snapshot.getValue<Subcategory>()?.copy(subcategoryId = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e(tag, "getSubcategory failed for id: $subcategoryId", e)
            null
        }
    }

    override suspend fun addSubcategory(categoryId: String, subcategory: Subcategory) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        val subcategoriesRef = database.getReference("users").child(user.uid).child("categories").child(categoryId).child("subcategories")
        val subcategoryId = subcategoriesRef.push().key ?: throw IllegalStateException("Could not generate subcategory ID")
        subcategoriesRef.child(subcategoryId).setValue(subcategory.copy(subcategoryId = subcategoryId)).await()
        Log.d(tag, "addSubcategory successful for id: $subcategoryId")
    }

    override suspend fun updateSubcategory(categoryId: String, subcategory: Subcategory) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (subcategory.subcategoryId.isEmpty()) throw IllegalArgumentException("Subcategory ID cannot be empty")
        database.getReference("users").child(user.uid).child("categories").child(categoryId).child("subcategories").child(subcategory.subcategoryId).setValue(subcategory).await()
        Log.d(tag, "updateSubcategory successful for id: ${subcategory.subcategoryId}")
    }

    override suspend fun deleteSubcategory(categoryId: String, subcategoryId: String) {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        if (subcategoryId.isEmpty()) throw IllegalArgumentException("Subcategory ID cannot be empty")
        database.getReference("users").child(user.uid).child("categories").child(categoryId).child("subcategories").child(subcategoryId).removeValue().await()
        Log.d(tag, "deleteSubcategory successful for id: $subcategoryId")
    }

    override suspend fun addDefaultCategoriesIfEmpty() {
        val user = authState.first() ?: throw IllegalStateException("User not authenticated")
        val categoriesRef = database.getReference("users").child(user.uid).child("categories")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.d(tag, "No categories found. Adding default categories.")
                    // Add your default categories here
                }
                categoriesRef.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "Failed to check for existing categories.", error.toException())
                categoriesRef.removeEventListener(this)
            }
        }
        categoriesRef.addListenerForSingleValueEvent(listener)
    }
}
