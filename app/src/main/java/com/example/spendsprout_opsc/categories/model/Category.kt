package com.example.spendsprout_opsc.categories.model

data class Category(
    val id: Int,
    val name: String,
    val color: Int,
    val balance: Double,
    val allocation: Double,
    val notes: String?
)

