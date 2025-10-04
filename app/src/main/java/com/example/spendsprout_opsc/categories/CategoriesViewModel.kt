package com.example.spendsprout_opsc.categories

import com.example.spendsprout_opsc.categories.model.Category

class CategoriesViewModel {
    
    fun getAllCategories(): List<Category> {
        return listOf(
            Category(
                id = 1,
                name = "Needs",
                color = 0xFFBD804A.toInt(),
                balance = 8900.0,
                allocation = 10000.0,
                notes = "Essential expenses"
            ),
            Category(
                id = 2,
                name = "Wants",
                color = 0xFF88618E.toInt(),
                balance = 120.0,
                allocation = 6000.0,
                notes = "Non-essential expenses"
            ),
            Category(
                id = 3,
                name = "Savings",
                color = 0xFF6EA19E.toInt(),
                balance = 4000.0,
                allocation = 4000.0,
                notes = "Savings goals"
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

