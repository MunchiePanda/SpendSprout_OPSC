package com.example.spendsprout_opsc.wants.model

data class Subcategory(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val color: Int,
    val balance: Double,
    val allocation: Double,
    val notes: String?
)

