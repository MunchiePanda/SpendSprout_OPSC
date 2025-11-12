package com.example.spendsprout_opsc.roomdb

import com.example.spendsprout_opsc.RepeatType

data class Income_Entity(
    var id: Int = 0,
    var incomeName: String = "",
    var incomeDate: Long = 0L,
    var incomeAmount: Double = 0.0,
    var incomeIsOwed: Boolean = false,
    var incomeRepeat: RepeatType = RepeatType.None,
    var incomeNotes: String? = null,
    var incomeImage: String? = null
)