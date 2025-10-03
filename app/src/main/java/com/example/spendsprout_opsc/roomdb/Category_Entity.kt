package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class Category_Entity(
    @PrimaryKey val id: Int,
    //ForeignKey for multiple sub-categories

    @ColumnInfo(name= "category_name") val categoryName: String,                //the name of the category
    @ColumnInfo(name= "category_color") val categoryColor: Int,                 //the color of the category (there are other data type options like String)
    @ColumnInfo(name= "category_balance") val categoryBalance: Double,          //the current balance of the category
    @ColumnInfo(name= "category_allocation") val categoryAllocation: Double,    //the budgeted amount/ allocation of the category
    @ColumnInfo(name= "category_notes") val categoryNotes: String?,             //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
)