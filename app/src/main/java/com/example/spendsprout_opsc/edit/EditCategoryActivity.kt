package com.example.spendsprout_opsc.edit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.databinding.ActivityEditCategoryBinding
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCategoryBinding
    private val viewModel: EditCategoryViewModel by viewModels()

    private var categoryId: String? = null
    private var subcategoryId: String? = null
    private var isEditMode = false

    private var currentCategory: Category? = null
    private var currentSubcategory: Subcategory? = null

    // Companion object to define the keys for Intent extras
    companion object {
        const val EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID"
        const val EXTRA_SUBCATEGORY_ID = "EXTRA_SUBCATEGORY_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get IDs from the Intent
        categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID)
        subcategoryId = intent.getStringExtra(EXTRA_SUBCATEGORY_ID)
        isEditMode = categoryId != null // If we have an ID, we are in edit mode

        // Load data if in edit mode
        if (isEditMode && categoryId != null) {
            viewModel.loadCategoryAndSubcategory(categoryId!!, subcategoryId)
        }

        observeUiState()

        binding.btnSave.setOnClickListener { onSave() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EditUiState.Loading -> {
                        // Optional: show a loading spinner
                    }
                    is EditUiState.Success -> {
                        currentCategory = state.category
                        currentSubcategory = state.subcategory
                        prefillForm(state.category, state.subcategory)
                    }
                    is EditUiState.Error -> {
                        Toast.makeText(this@EditCategoryActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun prefillForm(category: Category, subcategory: Subcategory?) {
        // If we are editing a subcategory, prefill its details
        if (subcategory != null) {
            binding.edtCategoryName.setText(subcategory.name)
            binding.edtAllocatedAmount.setText(subcategory.allocated.toString())
            // Prefill other fields like notes if you have them
        }
        // If we are editing a main category, prefill its details
        else {
            binding.edtCategoryName.setText(category.name)
            binding.edtAllocatedAmount.setText(category.allocated.toString())
        }
    }

    private fun onSave() {
        val name = binding.edtCategoryName.text.toString()
        val allocation = binding.edtAllocatedAmount.text.toString().toDoubleOrNull()

        if (name.isBlank() || allocation == null) {
            Toast.makeText(this, "Name and allocation cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        // The viewModel handles whether to create or update
        viewModel.save(
            parentCategory = currentCategory,
            isEditing = isEditMode,
            name = name,
            allocation = allocation,
            existingSubcategory = currentSubcategory
        )

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
