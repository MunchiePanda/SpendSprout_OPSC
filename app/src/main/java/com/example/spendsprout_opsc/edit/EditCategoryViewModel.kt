package com.example.spendsprout_opsc.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.categories.model.Subcategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditCategoryViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference("categories")
    private val subcategoriesRef = database.getReference("subcategories")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun saveCategory(name: String, type: String, allocatedBudget: Double, color: String, notes: String) {
        require(name.isNotBlank()) { "Category name is required" }
        require(allocatedBudget > 0) { "Allocated budget must be greater than 0" }

        currentUser?.let { user ->
            val userId = user.uid
            val categoryId = categoriesRef.child(userId).push().key ?: ""
            val subcategoryId = subcategoriesRef.child(userId).child(categoryId).push().key ?: ""

            val category = Category(categoryId, type, 0, 0.0, 0.0, null)
            val subcategory = Subcategory(subcategoryId, categoryId, name, color, 0.0, allocatedBudget, notes.ifBlank { null })

            categoriesRef.child(userId).child(categoryId).setValue(category)
            subcategoriesRef.child(userId).child(categoryId).child(subcategoryId).setValue(subcategory)
                .addOnSuccessListener {
                    Log.d("EditCategoryViewModel", "Subcategory saved: $name under $type")
                }
                .addOnFailureListener { e ->
                    Log.e("EditCategoryViewModel", "Error saving subcategory: ${e.message}", e)
                }
        }
    }
}
