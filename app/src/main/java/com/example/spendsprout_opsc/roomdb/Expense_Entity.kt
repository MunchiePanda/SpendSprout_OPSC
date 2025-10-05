package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.ExpenseType

@Entity(tableName = "Expense")
data class Expense_Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    //ForeignKey for single subcategory
    //ForeignKey for single account
    //ForeignKey for Contact (opt. if isOwed = true)

    @ColumnInfo(name= "expense_name") val expenseName: String,                  //the name/description of the expense
    @ColumnInfo(name= "expense_date") val expenseDate: Long,                    //the date of the expense
    @ColumnInfo(name= "expense_amount") val expenseAmount: Double,              //the amount of the expense
    @ColumnInfo(name= "expense_type") val expenseType: ExpenseType,             //Type determines if expenseAmount is + or -
    @ColumnInfo(name= "expense_is_owed") val expenseIsOwed: Boolean = false,    //if the expense is an reimbursable (contact must pay the user back) (default to false)
    @ColumnInfo(name= "expense_repeat") val expenseRepeat: RepeatType = RepeatType.None,    //if the expense is recurring (defaults to None)
    @ColumnInfo(name= "expense_notes") val expenseNotes: String?,               //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
    @ColumnInfo(name= "expense_image") val expenseImage: String?,               //optional image, can be stored as ByteArray as well
    // New fields for submission requirements
    @ColumnInfo(name= "expense_category") val expenseCategory: String,          //category for the expense
    @ColumnInfo(name= "expense_start") val expenseStart: Long?,                 //start time for the expense
    @ColumnInfo(name= "expense_end") val expenseEnd: Long?,                     //end time for the expense
)