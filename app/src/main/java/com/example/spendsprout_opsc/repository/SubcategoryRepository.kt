package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Subcategory // <-- IMPORT THE NEW MODEL
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubcategoryRepository @Inject constructor(
    private val db: FirebaseDatabase // Injected by Hilt from your FirebaseModule
) {
    // Subcategories are also stored globally for simplicity.
    private val subcategoriesRef = db.reference.child("subcategories")

    // Gets a realtime stream of ALL subcategories.
    fun getAllSubcategories(): Flow<List<Subcategory>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Map the data from Firebase into a list of our Subcategory data class
                val subcategories = snapshot.children.mapNotNull { it.getValue(Subcategory::class.java) }
                trySend(subcategories) // Send the new list to the collector
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException()) // Close the flow on error
            }
        }

        subcategoriesRef.addValueEventListener(listener)

        // When the flow is cancelled, remove the Firebase listener
        awaitClose { subcategoriesRef.removeEventListener(listener) }
    }

    // Gets a realtime stream of subcategories for a SPECIFIC parent category.
    fun getSubcategoriesForCategory(categoryId: String): Flow<List<Subcategory>> {
        // We get all subcategories and then filter them in the repository.
        // This is efficient because we are already listening to the entire subcategories list.
        return getAllSubcategories().map { allSubcategories ->
            allSubcategories.filter { it.categoryId == categoryId }
        }
    }

    // Adds a new subcategory linked to a parent category.
    suspend fun addSubcategory(subcategoryName: String, parentCategoryId: String) {
        // Create a new unique ID for the subcategory
        val subcategoryId = subcategoriesRef.push().key ?: return
        val newSubcategory = Subcategory(
            id = subcategoryId,
            name = subcategoryName,
            categoryId = parentCategoryId
        )

        // Save the subcategory object to the global subcategories path
        subcategoriesRef.child(subcategoryId).setValue(newSubcategory)
    }

    // Deletes a subcategory
    suspend fun deleteSubcategory(subcategoryId: String) {
        subcategoriesRef.child(subcategoryId).removeValue()
    }
}
