package com.example.spendsprout_opsc.model

import com.google.firebase.database.Exclude

data class Category(
    @get:Exclude // Exclude from Firebase write, as it's the key
    var categoryId: String = "",
    var name: String = "",
    var allocated: Double = 0.0,
    var color: Int = 0,
    var spendingType: SpendingType = SpendingType.NEEDS,
    // Keep any other fields you have (e.g., notes)
    var subcategories: Map<String, Subcategory> = emptyMap() // To hold nested subcategories
)
