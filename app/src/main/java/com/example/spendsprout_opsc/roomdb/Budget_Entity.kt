package com.example.spendsprout_opsc.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Budget")
data class Budget_Entity(
    @PrimaryKey val id: Int,
    //ForeignKeys for associated accounts
    //ForeignKeys for associated categories

    @ColumnInfo(name= "budget_name") val budgetName: String,
    @ColumnInfo(name= "budget_opening_balance") val openingBalance: Double, //the balance at the start of the month (rollover + income), or just starting income
    @ColumnInfo(name= "budget_min_goal") val budgetMinGoal: Double,         //the minimum that should be spent (has to be within opening balance), determines colour
    @ColumnInfo(name= "budget_max_goal") val budgetMaxGoal: Double,         //the maximum that should be spent (has to be within opening balance), determines colour
    @ColumnInfo(name= "budget_balance") val budgetBalance: Double,          //the current balance (opening balance - (total of all categories))
    @ColumnInfo(name= "budget_notes") val budgetNotes: String?,             //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
) : Serializable

