package com.SBMH.SpendSprout.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Subcategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class EditCategoryViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _allCategories = MutableLiveData<List<Category>>()
    val allCategories: LiveData<List<Category>> = _allCategories

    init {
        loadAllCategories()
    }

    private fun loadAllCategories() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val categoriesRef = database.getReference("users/${currentUser.uid}/categories")
                categoriesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categoryList = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                        _allCategories.postValue(categoryList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }

    fun saveSubcategory(categoryId: String, subcategoryName: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val subcategoriesRef = database.getReference("users/${currentUser.uid}/subcategories")
                val subcategoryId = subcategoriesRef.push().key!!
                val subcategory = Subcategory(subcategoryId, categoryId, subcategoryName)
                subcategoriesRef.child(subcategoryId).setValue(subcategory)
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

    fun getSubcategory(subcategoryId: String): LiveData<Subcategory> {
        val subcategory = MutableLiveData<Subcategory>()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val subcategoryRef = database.getReference("users/${currentUser.uid}/subcategories/$subcategoryId")
            subcategoryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val subcategoryData = snapshot.getValue(Subcategory::class.java)
                    if (subcategoryData != null) {
                        subcategory.value = subcategoryData!!
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
        return subcategory
    }
}
