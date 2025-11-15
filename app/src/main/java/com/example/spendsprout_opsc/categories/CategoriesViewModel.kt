package com.example.spendsprout_opsc.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.SubcategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
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

    val categoriesWithSubcategories: Flow<List<CategoryWithSubcategories>> =
        combine(
            categoryRepository.getAllCategories(),
            subcategoryRepository.getAllSubcategories(),
            transactionRepository.getAllTransactions()
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
