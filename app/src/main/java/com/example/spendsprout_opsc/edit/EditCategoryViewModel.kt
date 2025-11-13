package com.example.spendsprout_opsc.edit

import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditCategoryViewModel {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun saveCategory(name: String, type: String, allocatedBudget: Double, color: String, notes: String, callback: (Boolean, String?) -> Unit) {
        // Save category logic - validate first
        try {
            require(name.isNotBlank()) { "Category name is required" }
            require(allocatedBudget > 0) { "Allocated budget must be greater than 0" }

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                callback(false, "User not logged in")
                return
            }

            val userId = currentUser.uid
            val databaseRef = FirebaseDatabase.getInstance("https://spendsprout-49aaa-default-rtdb.europe-west1.firebasedatabase.app/").reference

            // Resolve or create the parent category
            val normalizedType = when (type.lowercase()) {
                "needs" -> "Needs"
                "wants" -> "Wants"
                "savings" -> "Savings"
                else -> type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }

            // Create a new subcategory
            val subcategoryId = databaseRef.child("users").child(userId).child("subcategories").push().key

            if (subcategoryId == null) {
                callback(false, "Failed to generate subcategory ID")
                return
            }

            // Create subcategory data
            val subcategoryData = hashMapOf(
                "id" to subcategoryId,
                "categoryId" to normalizedType,
                "name" to name,
                "color" to color,
                "allocation" to allocatedBudget,
                "balance" to 0.0,
                "notes" to notes.ifBlank { null }
            )

            // Save to Firebase
            databaseRef.child("users").child(userId).child("subcategories").child(subcategoryId).setValue(subcategoryData)
                .addOnSuccessListener {
                    android.util.Log.d("EditCategoryViewModel", "Subcategory saved to Firebase: $name under $type")
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("EditCategoryViewModel", "Error saving subcategory to Firebase", e)
                    callback(false, e.message)
                }
        } catch (e: Exception) {
            android.util.Log.e("EditCategoryViewModel", "Error saving subcategory", e)
            callback(false, e.message)
        }
    }
}
