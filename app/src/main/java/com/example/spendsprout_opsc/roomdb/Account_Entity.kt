package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendsprout_opsc.AccountType

@Entity(tableName = "Account")
data class Account_Entity(
    @PrimaryKey val id: Int,
    //ForeignKey for multiple expenses
    //Foreign key for Budget
    //ForeignKey for incomes

    @ColumnInfo(name= "account_name") val accountName: String,              //the name of the account
    @ColumnInfo(name= "account_type") val accountType: AccountType,         //Type of the account
    @ColumnInfo(name= "account_balance") val accountBalance: Double,        //the current balance of the account (how much was deposited/spent this month)
    @ColumnInfo(name= "account_notes") val accountNotes: String?,           //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
    //Any other API relevant info
)
