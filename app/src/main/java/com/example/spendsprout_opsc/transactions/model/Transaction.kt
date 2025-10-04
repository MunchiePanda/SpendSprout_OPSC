package com.example.spendsprout_opsc.transactions.model

import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.TransactionType

data class Transaction(
    val id: Int,
    val subcategoryId: Int,
    val accountId: Int,
    val contactId: Int?,
    val name: String,
    val date: Long,
    val amount: Double,
    val type: TransactionType,
    val isOwed: Boolean,
    val repeat: RepeatType,
    val notes: String?,
    val image: String?
)

