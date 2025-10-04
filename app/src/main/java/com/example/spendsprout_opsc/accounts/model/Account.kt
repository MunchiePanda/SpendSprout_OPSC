package com.example.spendsprout_opsc.accounts.model

import com.example.spendsprout_opsc.AccountType

data class Account(
    val id: Int,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val notes: String?
)

