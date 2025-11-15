package com.example.spendsprout_opsc.model

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L, // Store timestamp as a Long
    val description: String = "",
    val accountId: String = "",
    val categoryId: String = "",
    val userId: String = ""
)
    