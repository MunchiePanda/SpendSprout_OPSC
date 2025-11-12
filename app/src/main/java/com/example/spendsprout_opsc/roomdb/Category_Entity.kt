package com.example.spendsprout_opsc.roomdb

data class Category_Entity(
    var id: Int = 0,
    var categoryName: String = "",
    var categoryColor: Int = 0xFFCCCCCC.toInt(),
    var categoryBalance: Double = 0.0,
    var categoryAllocation: Double = 0.0,
    var categoryNotes: String? = null,
)