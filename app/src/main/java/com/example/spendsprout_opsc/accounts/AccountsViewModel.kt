package com.example.spendsprout_opsc.accounts

import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.AccountType

class AccountsViewModel {
    
    fun getAllAccounts(): List<Account> {
        return listOf(
            Account(
                id = 1,
                name = "Cash",
                type = AccountType.Cash,
                balance = 160.0,
                notes = "Physical cash on hand"
            ),
            Account(
                id = 2,
                name = "FNB Next Transact",
                type = AccountType.Debit,
                balance = 1720.0,
                notes = "Main FNB account"
            )
        )
    }
}

