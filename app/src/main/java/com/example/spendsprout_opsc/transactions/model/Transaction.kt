package com.example.spendsprout_opsc.transactions.model

data class Transaction(
    val id: String,
    val date: String,
    val description: String,
    val amount: String,
    val color: String,
    val imagePath: String? = null
)

