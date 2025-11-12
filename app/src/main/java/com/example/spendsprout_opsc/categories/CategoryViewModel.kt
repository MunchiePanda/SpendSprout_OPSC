package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.categories.model.Category

class CategoryViewModel {

    fun getAllCategories(): List<Category> = emptyList()

    fun loadCategoriesWithSubcategoriesFromDatabase(
        callback: (List<HierarchicalCategoryAdapter.CategoryWithSubcategories>) -> Unit
    ) {
        callback(emptyList())
    }

    fun loadCategoriesWithSubcategoriesFromDatabase(
        startDate: Long?,
        endDate: Long?,
        callback: (List<HierarchicalCategoryAdapter.CategoryWithSubcategories>) -> Unit
    ) {
        callback(emptyList())
    }

    fun loadCategoriesFromDatabase(callback: (List<Category>) -> Unit) {
        callback(emptyList())
    }

    fun loadCategoriesForDateRange(startDate: Long, endDate: Long, callback: (List<Category>) -> Unit) {
        callback(emptyList())
    }

    fun loadSubcategoriesForCategory(
        categoryName: String,
        callback: (List<com.example.spendsprout_opsc.wants.model.Subcategory>) -> Unit
    ) {
        callback(emptyList())
    }
}
