package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategory(categoryId: String): Category?
    suspend fun addCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: String)
    suspend fun getSubcategory(categoryId: String, subcategoryId: String): Subcategory?
    suspend fun addSubcategory(categoryId: String, subcategory: Subcategory)
    suspend fun updateSubcategory(categoryId: String, subcategory: Subcategory)
    suspend fun deleteSubcategory(categoryId: String, subcategoryId: String)
    suspend fun addDefaultCategoriesIfEmpty()
}
