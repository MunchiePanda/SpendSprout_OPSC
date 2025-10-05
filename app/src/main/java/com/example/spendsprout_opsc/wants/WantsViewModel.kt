package com.example.spendsprout_opsc.wants

import com.example.spendsprout_opsc.wants.model.Subcategory

class WantsViewModel {
    
    fun getSubcategories(): List<Subcategory> {
        return listOf(
            Subcategory(
                id = "1",
                name = "Eating Out",
                spent = "R 120",
                allocation = "R 1,000",
                color = "#88618E"
            ),
            Subcategory(
                id = "2",
                name = "Entertainment",
                spent = "R 0",
                allocation = "R 2,000",
                color = "#88618E"
            )
        )
    }
}

