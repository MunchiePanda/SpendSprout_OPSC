package com.example.spendsprout_opsc.wants

import com.example.spendsprout_opsc.wants.model.Subcategory

class WantsViewModel {
    
    fun getSubcategories(): List<Subcategory> {
        return listOf(
            Subcategory(
                id = 1,
                categoryId = 2,
                name = "Eating Out",
                color = 0xFF88618E.toInt(),
                balance = 120.0,
                allocation = 1000.0,
                notes = "Restaurant and takeout"
            ),
            Subcategory(
                id = 2,
                categoryId = 2,
                name = "Entertainment",
                color = 0xFF88618E.toInt(),
                balance = 0.0,
                allocation = 2000.0,
                notes = "Movies, concerts, etc."
            )
        )
    }
}

