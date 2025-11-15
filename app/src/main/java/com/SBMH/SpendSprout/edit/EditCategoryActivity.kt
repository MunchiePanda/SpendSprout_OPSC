package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var viewModel: EditCategoryViewModel
    private lateinit var categorySpinner: Spinner
    private lateinit var subcategoryNameEditText: EditText
    private lateinit var saveButton: Button
    private var subcategoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        viewModel = ViewModelProvider(this).get(EditCategoryViewModel::class.java)

        categorySpinner = findViewById(R.id.spinner_Type)
        subcategoryNameEditText = findViewById(R.id.edt_CategoryName)
        saveButton = findViewById(R.id.btn_Save)

        subcategoryId = intent.getStringExtra("subcategoryId")

        viewModel.allCategories.observe(this) { categories ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter

            if (subcategoryId != null) {
                viewModel.getSubcategory(subcategoryId!!).observe(this) { subcategory ->
                    subcategoryNameEditText.setText(subcategory.name)
                    val categoryPosition = categories.indexOfFirst { it.id == subcategory.categoryId }
                    categorySpinner.setSelection(categoryPosition)
                }
            }
        }

        saveButton.setOnClickListener {
            saveSubcategory()
        }
    }

    private fun saveSubcategory() {
        val subcategoryName = subcategoryNameEditText.text.toString()
        val selectedCategory = viewModel.allCategories.value?.get(categorySpinner.selectedItemPosition)

        if (subcategoryName.isBlank()) {
            Toast.makeText(this, "Subcategory name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (subcategoryId == null) {
            viewModel.saveSubcategory(selectedCategory.id, subcategoryName)
        } else {
            viewModel.updateSubcategory(subcategoryId!!, selectedCategory.id, subcategoryName)
        }

        finish()
    }
}
