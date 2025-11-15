package com.example.spendsprout_opsc.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Subcategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference("categories")
    private val subcategoriesRef = database.getReference("subcategories")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _subcategories = MutableLiveData<List<Subcategory>>()
    val subcategories: LiveData<List<Subcategory>> = _subcategories

    fun loadCategories() {
        currentUser?.let { user ->
            val userId = user.uid
            categoriesRef.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryList = mutableListOf<Category>()
                    for (categorySnapshot in snapshot.children) {
                        val category = categorySnapshot.getValue(Category::class.java)
                        category?.let { categoryList.add(it) }
                    }
                    _categories.value = categoryList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    fun loadSubcategories(categoryId: String) {
        currentUser?.let { user ->
            val userId = user.uid
            subcategoriesRef.child(userId).child(categoryId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val subcategoryList = mutableListOf<Subcategory>()
                    for (subcategorySnapshot in snapshot.children) {
                        val subcategory = subcategorySnapshot.getValue(Subcategory::class.java)
                        subcategory?.let { subcategoryList.add(it) }
                    }
                    _subcategories.value = subcategoryList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    fun loadCategoriesWithSubcategoriesFromDatabase(startDate: Long?, endDate: Long?, onCategoriesLoaded: (List<HierarchicalCategoryAdapter.CategoryWithSubcategories>) -> Unit) {
        // Implement your logic to fetch categories and subcategories from Firebase
        // and then call onCategoriesLoaded with the result.
        // For now, I'm just calling it with an empty list.
        onCategoriesLoaded(emptyList())
    }
}
