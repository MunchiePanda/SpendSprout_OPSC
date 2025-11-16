package com.example.spendsprout_opsc.edit

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.databinding.ActivityEditCategoryBinding
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.SpendingType
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

    private val colorMap = mapOf(
        "Red" to Color.RED,
        "Green" to Color.GREEN,
        "Blue" to Color.BLUE,
        "Yellow" to Color.YELLOW,
        "Cyan" to Color.CYAN,
        "Magenta" to Color.MAGENTA
    )

    companion object {
        const val EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID"
        const val EXTRA_SUBCATEGORY_ID = "EXTRA_SUBCATEGORY_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupColorSpinner()
        setupSpendingTypeSpinner()

        categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID)
        subcategoryId = intent.getStringExtra(EXTRA_SUBCATEGORY_ID)
        isEditMode = categoryId != null

        if (isEditMode && categoryId != null) {
            viewModel.loadCategoryAndSubcategory(categoryId!!, subcategoryId)
        }

        observeUiState()

        binding.btnSave.setOnClickListener { onSave() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun setupColorSpinner() {
        val colorNames = colorMap.keys.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerColor.adapter = adapter
    }

    private fun setupSpendingTypeSpinner() {
        val spendingTypeNames = SpendingType.values().map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spendingTypeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is EditUiState.Loading -> { /* Show loading */ }
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
        val colorToSet = subcategory?.color ?: category.color
        val colorName = colorMap.entries.find { (_, value) -> value == colorToSet }?.key
        val colorPosition = (binding.spinnerColor.adapter as ArrayAdapter<String>).getPosition(colorName)

        val spendingTypeToSet = subcategory?.spendingType ?: category.spendingType
        val spendingTypePosition = (binding.spinnerType.adapter as ArrayAdapter<String>).getPosition(spendingTypeToSet.name)

        if (subcategory != null) {
            binding.edtCategoryName.setText(subcategory.name)
            binding.edtAllocatedAmount.setText(subcategory.allocated.toString())
            if (colorPosition >= 0) {
                binding.spinnerColor.setSelection(colorPosition)
            }
            if (spendingTypePosition >= 0) {
                binding.spinnerType.setSelection(spendingTypePosition)
            }
        } else {
            binding.edtCategoryName.setText(category.name)
            binding.edtAllocatedAmount.setText(category.allocated.toString())
            if (colorPosition >= 0) {
                binding.spinnerColor.setSelection(colorPosition)
            }
            if (spendingTypePosition >= 0) {
                binding.spinnerType.setSelection(spendingTypePosition)
            }
        }
    }

    private fun onSave() {
        val name = binding.edtCategoryName.text.toString()
        val allocation = binding.edtAllocatedAmount.text.toString().toDoubleOrNull()
        val selectedColorName = binding.spinnerColor.selectedItem.toString()
        val selectedColor = colorMap[selectedColorName] ?: 0
        val selectedSpendingType = SpendingType.valueOf(binding.spinnerType.selectedItem.toString())

        if (name.isBlank() || allocation == null) {
            Toast.makeText(this, "Name and allocation cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.save(
            parentCategory = currentCategory,
            isEditing = isEditMode,
            name = name,
            allocation = allocation,
            color = selectedColor,
            spendingType = selectedSpendingType,
            existingSubcategory = currentSubcategory
        )

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
