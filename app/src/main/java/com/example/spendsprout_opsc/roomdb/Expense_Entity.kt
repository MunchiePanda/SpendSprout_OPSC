package com.example.spendsprout_opsc.roomdb

import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.RepeatType

data class Expense_Entity(
    var id: Long = 0,
    var expenseName: String = "",
    var expenseDate: Long = 0L,
    var expenseAmount: Double = 0.0,
    var expenseType: ExpenseType = ExpenseType.Expense,
    var expenseIsOwed: Boolean = false,
    var expenseRepeat: RepeatType = RepeatType.None,
    var expenseNotes: String? = null,
    var expenseImage: String? = null,
    var expenseCategory: String = "",
    var expenseStart: Long? = null,
    var expenseEnd: Long? = null,
)
