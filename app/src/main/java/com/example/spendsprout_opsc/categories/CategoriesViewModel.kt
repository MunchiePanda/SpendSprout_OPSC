package com.example.spendsprout_opsc.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    data class CategoryWithSubcategories(
        val category: Category,
        val subcategories: List<Subcategory>
    )

    init {
        viewModelScope.launch {
            categoryRepository.addDefaultCategoriesIfEmpty()
        }
    }

    private val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val subcategories: StateFlow<List<Subcategory>> = categoryRepository.getAllSubcategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val transactions: StateFlow<List<com.example.spendsprout_opsc.model.Transaction>> = transactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoriesWithSubcategories: Flow<List<CategoryWithSubcategories>> =
        combine(
            categories,
            subcategories,
            transactions
        ) { categories, allSubcategories, allTransactions ->
            val spentPerCategory = allTransactions
                .groupBy { it.categoryId }
                .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

            categories.map { category ->
                val subcategoriesForThisCategory = allSubcategories.filter { it.categoryId == category.categoryId }

                CategoryWithSubcategories(
                    category = category,
                    subcategories = subcategoriesForThisCategory
                )
            }
        }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryRepository.addCategory(Category(name = name))
        }
    }

    fun addSubcategory(name: String, parentCategoryId: String) {
        viewModelScope.launch {
            categoryRepository.addSubcategory(parentCategoryId, Subcategory(name = name, categoryId = parentCategoryId))
        }
    }
}
