package com.SBMH.SpendSprout.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Subcategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class EditCategoryViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveCategory(categoryName: String, subcategoryName: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val categoriesRef = database.getReference("users/${currentUser.uid}/categories")
                val categoryId = categoriesRef.push().key!!
                val category = Category(categoryId, categoryName)
                categoriesRef.child(categoryId).setValue(category).addOnSuccessListener {
                    val subcategoriesRef = database.getReference("users/${currentUser.uid}/subcategories")
                    val subcategoryId = subcategoriesRef.push().key!!
                    val subcategory = Subcategory(subcategoryId, categoryId, subcategoryName)
                    subcategoriesRef.child(subcategoryId).setValue(subcategory)
                }
            }
        }
    }

    fun updateSubcategory(subcategoryId: String, categoryId: String, subcategoryName: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val subcategoryRef = database.getReference("users/${currentUser.uid}/subcategories/$subcategoryId")
                val subcategory = Subcategory(subcategoryId, categoryId, subcategoryName)
                subcategoryRef.setValue(subcategory)
            }
        }
    }
}
