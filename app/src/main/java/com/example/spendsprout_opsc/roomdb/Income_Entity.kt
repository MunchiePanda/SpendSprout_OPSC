package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendsprout_opsc.RepeatType

@Entity(tableName = "Income")
data class Income_Entity(
    @PrimaryKey val id: Int,
    //ForeignKey for single Budget
    //ForeignKey for single account

    @ColumnInfo(name= "income_name") val incomeName: String,                  //the name/description of the income
    @ColumnInfo(name= "income_date") val incomeDate: Long,                    //the date of the income
    @ColumnInfo(name= "income_amount") val incomeAmount: Double,              //the amount of the income
    @ColumnInfo(name= "income_is_owed") val incomeIsOwed: Boolean = false,    //if the income is an reimbursable (contact must pay the user back) (default to false)
    @ColumnInfo(name= "income_repeat") val incomeRepeat: RepeatType = RepeatType.None,    //if the income is recurring (defaults to None)
    @ColumnInfo(name= "income_notes") val incomeNotes: String?,               //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
    @ColumnInfo(name= "income_image") val incomeImage: String?,               //optional image, can be stored as ByteArray as well
)