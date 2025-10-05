package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.categories.model.Category

class CategoriesViewModel {
    
    fun getAllCategories(): List<Category> {
        return listOf(
            Category(
                id = "1",
                name = "Needs",
                spent = "R 8,900",
                allocation = "R 10,000",
                color = "#BD804A"
            ),
            Category(
                id = "2", 
                name = "Wants",
                spent = "- R 120",
                allocation = "R 6,000",
                color = "#88618E"
            ),
            Category(
                id = "3",
                name = "Savings", 
                spent = "R 4,000",
                allocation = "R 4,000",
                color = "#6EA19E"
            )
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

