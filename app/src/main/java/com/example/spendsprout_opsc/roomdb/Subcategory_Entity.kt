package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Subcategory")
data class Subcategory_Entity(
    @PrimaryKey val id: Int,
    //ForeignKey for single category
    //ForeignKey for multiple payments

    @ColumnInfo(name= "subcategory_name") val subcategoryName: String,                //the name of the category
    @ColumnInfo(name= "subcategory_color") val subcategoryColor: Int,                 //the color of the category (there are other data type options like String)
    @ColumnInfo(name= "subcategory_balance") val subcategoryBalance: Double,          //the current balance of the category
    @ColumnInfo(name= "subcategory_allocation") val subcategoryAllocation: Double,    //the budgeted amount/ allocation of the category
    @ColumnInfo(name= "subcategory_notes") val subcategoryNotes: String?,             //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
)