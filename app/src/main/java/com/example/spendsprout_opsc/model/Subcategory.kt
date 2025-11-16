package com.example.spendsprout_opsc.model

import com.google.firebase.database.Exclude

data class Subcategory(
    @get:Exclude // Exclude from Firebase write, as it's the key
    var subcategoryId: String = "",
    var categoryId: String = "", // Link back to the parent Category
    var name: String = "",
    var allocated: Double = 0.0,
    var color: Int = 0,
    var spendingType: SpendingType = SpendingType.NEEDS
)
