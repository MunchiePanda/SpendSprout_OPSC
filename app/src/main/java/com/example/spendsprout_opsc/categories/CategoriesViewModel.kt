package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.categories.model.Category

class CategoriesViewModel {
    
    fun getAllCategories(): List<Category> {
        return listOf(
            Category("Needs", "R 8,900", "R 10,000", "#BD804A", listOf(
                Category("Rent", "R 3,500", "R 3,500", "#9370DB"),
                Category("Gas", "R 1,500", "R 1,500", "#20B2AA"),
                Category("Groceries", "R 360", "R 360", "#4169E1")
            )),
            Category("Wants", "- R 120", "R 6,000", "#88618E", listOf(
                Category("Eating-Out", "R 110", "R 1,000", "#FFB6C1"),
                Category("Entertainment", "R 6,010", "R 4,000", "#FFA500")
            )),
            Category("Savings", "R 4,000", "R 4,000", "#6EA19E", listOf(
                Category("Rainy-Day", "R 2,500", "R 2,500", "#D3D3D3"),
                Category("Investments", "R 1,500", "R 1,500", "#808080")
            ))
        )
    }
    
    fun getFilteredCategories(type: String): List<Category> {
        val allCategories = getAllCategories()
        return when (type) {
            "Needs" -> allCategories.filter { it.name == "Needs" }
            "Wants" -> allCategories.filter { it.name == "Wants" }
            "Savings" -> allCategories.filter { it.name == "Savings" }
            else -> allCategories
        }
    }
}

