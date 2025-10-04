package com.example.spendsprout_opsc.roomdb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.TransactionType

@Entity(
    tableName = "Payment",
    foreignKeys = [
        ForeignKey(
            entity = Subcategory_Entity::class,
            parentColumns = ["id"],
            childColumns = ["subcategory_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Account_Entity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Contact_Entity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        androidx.room.Index(value = ["subcategory_id"]),
        androidx.room.Index(value = ["account_id"]),
        androidx.room.Index(value = ["contact_id"])
    ]
)
data class Payment_Entity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "subcategory_id") val subcategoryId: Int, // Foreign key to Subcategory
    @ColumnInfo(name = "account_id") val accountId: Int, // Foreign key to Account
    @ColumnInfo(name = "contact_id") val contactId: Int? = null, // Foreign key to Contact (optional)

    @ColumnInfo(name= "payment_name") val paymentName: String,                  //the name/description of the payment
    @ColumnInfo(name= "payment_date") val paymentDate: Long,                    //the date of the payment
    @ColumnInfo(name= "payment_amount") val paymentAmount: Double,              //the amount of the payment
    @ColumnInfo(name= "payment_type") val paymentType: TransactionType,         //Type determines if paymentAmount is + or -
    @ColumnInfo(name= "payment_is_owed") val paymentIsOwed: Boolean = false,    //if the payment is an reimbursable (contact must pay the user back) (default to false)
    @ColumnInfo(name= "payment_repeat") val paymentRepeat: RepeatType = RepeatType.None,    //if the payment is recurring (defaults to None)
    @ColumnInfo(name= "payment_notes") val paymentNotes: String?,               //optional user notes (change [?] to [= ""] if you don't want to deal with null checks)
    @ColumnInfo(name= "payment_image") val paymentImage: String?,               //optional image, can be stored as ByteArray as well
)