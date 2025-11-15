package com.SBMH.SpendSprout.model

data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val date: Long = 0,
    val category: String = "",
    val account: String = "",
    val type: String = "",
    val notes: String? = null,
    val isRepeating: Boolean = false,
    val userId: String = ""
)
