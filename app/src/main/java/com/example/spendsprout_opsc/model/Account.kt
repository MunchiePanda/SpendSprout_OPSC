package com.example.spendsprout_opsc.model

import com.google.firebase.database.Exclude

data class Account(
    @get:Exclude
    var accountId: String = "",
    var accountName: String = "",
    var accountType: String = "",
    var accountBalance: Double = 0.0,
    var accountNotes: String? = null
)
