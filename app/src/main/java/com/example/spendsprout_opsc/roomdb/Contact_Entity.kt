package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact")
data class Contact_Entity(
    @PrimaryKey val id: Int,
    //ForeignKey for multiple expenses (owed expenses)

    @ColumnInfo(name= "contact_name") val contactName: String,          //the name of the contact
    @ColumnInfo(name= "contact_id") val contactId: Long?,               //the id of the contact in the contacts provider (system ContactsContract ID)
    @ColumnInfo(name= "contact_balance") val contactBalance: Double,    //the current balance of the contact (how much they owe/is owed to them)
    @ColumnInfo(name= "contact_notes") val contactNotes: String?,   //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
    //Any other API relevant info
)
