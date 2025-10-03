package com.example.spendsprout_opsc.categories.model

data class Category(
    val name: String,
    val spent: String,
    val allocated: String,
    val color: String,
    val subcategories: List<Category> = emptyList()
)

