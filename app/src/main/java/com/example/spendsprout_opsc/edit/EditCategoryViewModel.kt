package com.example.spendsprout_opsc.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
import com.example.spendsprout_opsc.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditUiState>(EditUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Function to load data when editing
    fun loadCategoryAndSubcategory(categoryId: String, subcategoryId: String?) {
        viewModelScope.launch {
            _uiState.value = EditUiState.Loading
            val category = categoryRepository.getCategory(categoryId)
            val subcategory = if (subcategoryId != null) categoryRepository.getSubcategory(categoryId, subcategoryId) else null
            if (category != null) {
                _uiState.value = EditUiState.Success(category, subcategory)
            } else {
                _uiState.value = EditUiState.Error("Category not found")
            }
        }
    }

    // Function to save a new category or subcategory
    fun save(
        parentCategory: Category?, // Can be null if creating a new parent
        isEditing: Boolean,
        name: String,
        allocation: Double,
        // Add other fields like color, notes if they are in your model
        existingSubcategory: Subcategory?
    ) {
        viewModelScope.launch {
            if (isEditing) {
                // We are editing a subcategory
                if (parentCategory != null && existingSubcategory != null) {
                    val updatedSubcategory = existingSubcategory.copy(
                        name = name,
                        allocated = allocation
                        // update other fields
                    )
                    categoryRepository.updateSubcategory(parentCategory.categoryId, updatedSubcategory)
                }
            } else {
                // We are creating a new item
                if (parentCategory != null) {
                    // This means we are creating a new SUBCATEGORY under an existing category
                    val newSubcategory = Subcategory(
                        name = name,
                        allocated = allocation
                    )
                    categoryRepository.addSubcategory(parentCategory.categoryId, newSubcategory)
                } else {
                    // This means we are creating a new PARENT CATEGORY
                    val newCategory = Category(
                        name = name,
                        allocated = allocation
                    )
                    categoryRepository.addCategory(newCategory)
                }
            }
        }
    }
}

// Sealed interface to represent the state of the screen
sealed interface EditUiState {
    object Loading : EditUiState
    data class Success(val category: Category, val subcategory: Subcategory?) : EditUiState
    data class Error(val message: String) : EditUiState
}
