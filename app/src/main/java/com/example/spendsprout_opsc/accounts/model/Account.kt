package com.example.spendsprout_opsc.accounts.model

data class Account(
    val id: String,
    val name: String,
    val balance: String,
    val limit: String,
    val recentTransactions: List<Transaction>
)

data class Transaction(
    val description: String,
    val amount: String,
    val color: String
)

