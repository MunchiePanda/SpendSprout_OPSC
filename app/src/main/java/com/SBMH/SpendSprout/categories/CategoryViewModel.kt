package com.SBMH.SpendSprout.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.CategoryWithSubcategories
import com.SBMH.SpendSprout.model.Subcategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val _categoriesWithSubcategories = MutableLiveData<List<CategoryWithSubcategories>>()
    val categoriesWithSubcategories: LiveData<List<CategoryWithSubcategories>> = _categoriesWithSubcategories

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadCategoriesWithSubcategories() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val categoriesRef = database.getReference("users/${currentUser.uid}/categories")
                val subcategoriesRef = database.getReference("users/${currentUser.uid}/subcategories")

                categoriesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(categoriesSnapshot: DataSnapshot) {
                        val categories = categoriesSnapshot.children.mapNotNull { it.getValue(Category::class.java) }

                        subcategoriesRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(subcategoriesSnapshot: DataSnapshot) {
                                val subcategories = subcategoriesSnapshot.children.mapNotNull { it.getValue(Subcategory::class.java) }
                                val categoriesWithSubcategories = categories.map { category ->
                                    val relatedSubcategories = subcategories.filter { it.categoryId == category.id }
                                    CategoryWithSubcategories(category, relatedSubcategories)
                                }
                                _categoriesWithSubcategories.postValue(categoriesWithSubcategories)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }
}
