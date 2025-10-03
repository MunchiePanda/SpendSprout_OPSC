package com.example.spendsprout_opsc.wants.model

data class Subcategory(
    val id: String,
    val name: String,
    val spent: String,
    val allocated: String,
    val color: String,
    val transactions: List<Subcategory> = emptyList()
)

