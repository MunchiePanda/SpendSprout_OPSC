package com.example.spendsprout_opsc.model

data class Budget(
    val budgetId: String = "",
    val budgetName: String = "",
    val budgetAmount: Double = 0.0,
    var totalSpent: Double = 0.0
)