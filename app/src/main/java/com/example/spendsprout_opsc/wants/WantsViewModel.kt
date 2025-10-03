package com.example.spendsprout_opsc.wants

import com.example.spendsprout_opsc.wants.model.Subcategory

class WantsViewModel {
    
    fun getSubcategories(): List<Subcategory> {
        return listOf(
            Subcategory("1", "Eating-Out", "R 110", "R 1,000", "#FFB6C1", listOf(
                Subcategory("1a", "McDonalds", "R 110", "R 110", "#FFB6C1", emptyList())
            )),
            Subcategory("2", "Entertainment", "R 6,010", "R 4,000", "#FFA500", listOf(
                Subcategory("2a", "Birthday Bash", "R 1,010", "R 1,010", "#FFA500", emptyList()),
                Subcategory("2b", "Monte Casino", "R 4,000", "R 4,000", "#FFA500", emptyList()),
                Subcategory("2c", "Bowling", "R 500", "R 500", "#FFA500", emptyList()),
                Subcategory("2d", "Bad Decision", "R 0,00", "R 0,00", "#FFA500", emptyList()),
                Subcategory("2e", "Bad Decision", "R 0,00", "R 0,00", "#FFA500", emptyList()),
                Subcategory("2f", "Bad Decision", "R 0,00", "R 0,00", "#FFA500", emptyList())
            ))
        )
    }
}

