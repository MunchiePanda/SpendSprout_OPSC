package com.example.spendsprout_opsc.roomdb

data class Subcategory_Entity(
    var id: Int = 0,
    var categoryId: Int = 0,
    var subcategoryName: String = "",
    var subcategoryColor: Int = 0xFFCCCCCC.toInt(),
    var subcategoryBalance: Double = 0.0,
    var subcategoryAllocation: Double = 0.0,
    var subcategoryNotes: String? = null,
)