package com.example.spendsprout_opsc.firebase.model

import com.example.spendsprout_opsc.AccountType
import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.RepeatType

data class AccountDto(
    var id: String? = null,
    var name: String? = null,
    var type: String? = AccountType.Cash.name,
    var balance: Double? = 0.0,
    var notes: String? = null,
)

data class CategoryDto(
    var id: String? = null,
    var name: String? = null,
    var color: String? = "#D3D3D3",
    var allocation: Double? = 0.0,
    var balance: Double? = 0.0,
    var notes: String? = null,
)

data class SubcategoryDto(
    var id: String? = null,
    var categoryId: String? = null,
    var name: String? = null,
    var color: String? = "#D3D3D3",
    var allocation: Double? = 0.0,
    var balance: Double? = 0.0,
    var notes: String? = null,
)

data class TransactionDto(
    var id: String? = null,
    var name: String? = null,
    var date: Long? = null,
    var amount: Double? = 0.0,
    var type: String? = ExpenseType.Expense.name,
    var isOwed: Boolean? = false,
    var repeat: String? = RepeatType.None.name,
    var notes: String? = null,
    var imagePath: String? = null,
    var category: String? = null,
    var start: Long? = null,
    var end: Long? = null,
)


