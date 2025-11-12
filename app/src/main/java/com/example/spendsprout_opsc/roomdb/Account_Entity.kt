package com.example.spendsprout_opsc.roomdb

import com.example.spendsprout_opsc.AccountType
import java.io.Serializable

data class Account_Entity(
    var id: Int = 0,
    var accountName: String = "",
    var accountType: AccountType = AccountType.Cash,
    var accountBalance: Double = 0.0,
    var accountNotes: String? = null,
) : Serializable
