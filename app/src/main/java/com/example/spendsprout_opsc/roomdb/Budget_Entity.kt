package com.example.spendsprout_opsc.roomdb

import java.io.Serializable

data class Budget_Entity(
    var id: Int = 0,
    var budgetName: String = "",
    var openingBalance: Double = 0.0,
    var budgetMinGoal: Double = 0.0,
    var budgetMaxGoal: Double = 0.0,
    var budgetBalance: Double = 0.0,
    var budgetNotes: String? = null,
) : Serializable

