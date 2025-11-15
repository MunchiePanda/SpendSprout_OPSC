
package com.example.spendsprout_opsc.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Subcategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CategoriesViewModel : ViewModel() {

    private val _categoriesWithSubcategories = MutableLiveData<List<Pair<Category, List<Subcategory>>>>()
    val categoriesWithSubcategories: LiveData<List<Pair<Category, List<Subcategory>>>> = _categoriesWithSubcategories

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadCategoriesWithSubcategories(startDate: Long?, endDate: Long?) {
        if (userId == null) return

        val categoriesRef = database.getReference("users/$userId/categories")
        val subcategoriesRef = database.getReference("users/$userId/subcategories")

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                
                subcategoriesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(subcategorySnapshot: DataSnapshot) {
                        val subcategories = subcategorySnapshot.children.mapNotNull { it.getValue(Subcategory::class.java) }
                        val categoriesWithSubcategories = categories.map { category ->
                            val categorySubcategories = subcategories.filter { it.categoryId == category.id }
                            Pair(category, categorySubcategories)
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
