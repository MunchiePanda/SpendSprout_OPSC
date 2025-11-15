package com.SBMH.SpendSprout.model

data class CategoryWithSubcategories(
    val category: Category,
    val subcategories: List<Subcategory>
)
