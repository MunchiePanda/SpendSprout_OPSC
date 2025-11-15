package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Subcategory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var viewModel: EditCategoryViewModel
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var subcategoryEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton

    private var subcategoryId: String? = null
    private var categoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        viewModel = ViewModelProvider(this).get(EditCategoryViewModel::class.java)

        categoryAutoComplete = findViewById(R.id.act_Category)
        subcategoryEditText = findViewById(R.id.edt_SubcategoryName)
        saveButton = findViewById(R.id.btn_SaveCategory)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subcategoryId = intent.getStringExtra("subcategoryId")

        if (subcategoryId != null) {
            supportActionBar?.title = "Edit Category"
            loadSubcategoryDetails(subcategoryId!!)
        } else {
            supportActionBar?.title = "Add Category"
        }

        setupCategorySpinner()

        saveButton.setOnClickListener {
            val categoryName = categoryAutoComplete.text.toString()
            val subcategoryName = subcategoryEditText.text.toString()

            if (categoryName.isNotBlank() && subcategoryName.isNotBlank()) {
                // This is a simplified logic. You might need a more robust way to get the category ID
                getCategoryIdFromName(categoryName) { catId ->
                    if (catId != null) {
                        if (subcategoryId != null) {
                            viewModel.updateSubcategory(subcategoryId!!, catId, subcategoryName)
                            Toast.makeText(this, "Subcategory updated", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveCategory(categoryName, subcategoryName)
                            Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    } else {
                        Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSubcategoryDetails(subcategoryId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val subcategoryRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/subcategories/$subcategoryId")
            subcategoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val subcategory = snapshot.getValue(Subcategory::class.java)
                    if (subcategory != null) {
                        subcategoryEditText.setText(subcategory.name)
                        categoryId = subcategory.categoryId
                        // Load category name from categoryId
                        getCategoryNameFromId(categoryId!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditCategoryActivity, "Failed to load subcategory details", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupCategorySpinner() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val categoriesRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/categories")
            categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories = snapshot.children.mapNotNull { it.getValue(Category::class.java)?.name }
                    val adapter = ArrayAdapter(this@EditCategoryActivity, android.R.layout.simple_dropdown_item_1line, categories)
                    categoryAutoComplete.setAdapter(adapter)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun getCategoryIdFromName(categoryName: String, callback: (String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val categoriesRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/categories")
            categoriesRef.orderByChild("name").equalTo(categoryName).limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val category = snapshot.children.first().getValue(Category::class.java)
                        callback(category?.id)
                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
        }
    }

    private fun getCategoryNameFromId(categoryId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val categoryRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/categories/$categoryId")
            categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val category = snapshot.getValue(Category::class.java)
                    if (category != null) {
                        categoryAutoComplete.setText(category.name, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
