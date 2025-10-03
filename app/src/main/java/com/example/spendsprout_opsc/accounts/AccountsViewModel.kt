package com.example.spendsprout_opsc.accounts

import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.accounts.model.Transaction

class AccountsViewModel {
    
    fun getAllAccounts(): List<Account> {
        return listOf(
            Account(
                "1",
                "Cash",
                "R 160",
                "R 19,00",
                listOf(
                    Transaction("Recent Transaction 1", "- R 3,500", "#32CD32"),
                    Transaction("Recent Transaction 2", "- R 1,500", "#20B2AA"),
                    Transaction("Recent Transaction 3", "+ R 360", "#4169E1")
                )
            ),
            Account(
                "2",
                "FNB Next Transact",
                "R 1,720",
                "R 15,000",
                listOf(
                    Transaction("Recent Transaction 1", "- R 3,500", "#D3D3D3"),
                    Transaction("Recent Transaction 2", "- R 1,500", "#D3D3D3"),
                    Transaction("Recent Transaction 3", "- R 400", "#D3D3D3")
                )
            )
        )
    }
}

