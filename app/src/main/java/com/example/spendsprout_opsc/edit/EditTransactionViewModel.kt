package com.example.spendsprout_opsc.edit

import com.example.spendsprout_opsc.ExpenseType
import com.example.spendsprout_opsc.RepeatType
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionViewModel {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun saveTransaction(
        description: String,
        amount: Double,
        category: String,
        date: Long,
        account: String,
        repeat: String,
        oweOwed: Boolean,
        notes: String,
        imagePath: String? = null,
        callback: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        try {
            require(description.isNotBlank()) { "Description is required" }
            require(amount > 0) { "Amount must be greater than 0" }
            require(category.isNotBlank()) { "Category is required" }

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                android.util.Log.e("EditTransactionViewModel", "User not logged in")
                callback(false, "User not logged in")
                return
            }

            val userId = currentUser.uid
            val databaseRef = FirebaseDatabase.getInstance("https://spendsprout-49aaa-default-rtdb.europe-west1.firebasedatabase.app/").reference
            val transactionId = databaseRef.child("users").child(userId).child("transactions").push().key

            if (transactionId == null) {
                android.util.Log.e("EditTransactionViewModel", "Couldn't get push key for transactions")
                callback(false, "Failed to generate transaction ID")
                return
            }

            val transactionMap = hashMapOf(
                "id" to transactionId,
                "expenseName" to description,
                "expenseDate" to date,
                "expenseAmount" to amount,
                "expenseType" to if (oweOwed) ExpenseType.Income.name else ExpenseType.Expense.name,
                "expenseIsOwed" to oweOwed,
                "expenseRepeat" to parseRepeatType(repeat).name,
                "expenseNotes" to notes.ifBlank { null },
                "expenseImage" to imagePath,
                "expenseCategory" to category,
                "expenseStart" to null,
                "expenseEnd" to null
            )

            databaseRef.child("users").child(userId).child("transactions").child(transactionId).setValue(transactionMap)
                .addOnSuccessListener {
                    android.util.Log.d("EditTransactionViewModel", "Transaction saved to Firebase: $description, $amount, $category")
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("EditTransactionViewModel", "Error saving transaction to Firebase", e)
                    callback(false, e.message)
                }
        } catch (e: Exception) {
            android.util.Log.e("EditTransactionViewModel", "Error saving transaction", e)
            callback(false, e.message)
        }
    }

    fun updateTransaction(
        transactionId: String,
        name: String,
        amount: Double,
        category: String,
        date: Long,
        account: String,
        repeat: String,
        oweOwed: Boolean,
        notes: String,
        imagePath: String? = null,
        callback: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                android.util.Log.e("EditTransactionViewModel", "User not logged in")
                callback(false, "User not logged in")
                return
            }

            val userId = currentUser.uid
            val databaseRef = FirebaseDatabase.getInstance("https://spendsprout-49aaa-default-rtdb.europe-west1.firebasedatabase.app/").reference

            val transactionMap = hashMapOf(
                "id" to transactionId,
                "expenseName" to name,
                "expenseDate" to date,
                "expenseAmount" to amount,
                "expenseType" to if (oweOwed) ExpenseType.Income.name else ExpenseType.Expense.name,
                "expenseIsOwed" to oweOwed,
                "expenseRepeat" to parseRepeatType(repeat).name,
                "expenseNotes" to notes.ifBlank { null },
                "expenseImage" to imagePath,
                "expenseCategory" to category,
                "expenseStart" to null,
                "expenseEnd" to null
            )

            databaseRef.child("users").child(userId).child("transactions").child(transactionId).setValue(transactionMap)
                .addOnSuccessListener {
                    android.util.Log.d("EditTransactionViewModel", "Transaction updated in Firebase: $name")
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("EditTransactionViewModel", "Error updating transaction in Firebase", e)
                    callback(false, e.message)
                }
        } catch (e: Exception) {
            android.util.Log.e("EditTransactionViewModel", "Error updating transaction", e)
            callback(false, e.message)
        }
    }

    fun parseUiDateToMillis(ui: String): Long {
        return try {
            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).parse(ui)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun parseRepeatType(repeat: String): RepeatType {
        return when (repeat.lowercase()) {
            "daily" -> RepeatType.Daily
            "weekly" -> RepeatType.Weekly
            "monthly" -> RepeatType.Monthly
            else -> RepeatType.None
        }
    }

    fun loadTransactionById(transactionId: String, callback: (Expense_Entity?) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            callback(null)
            return
        }
        val userId = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance("https://spendsprout-49aaa-default-rtdb.europe-west1.firebasedatabase.app/").reference

        databaseRef.child("users").child(userId).child("transactions").child(transactionId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("expenseName").getValue(String::class.java) ?: ""
                        val amount = snapshot.child("expenseAmount").getValue(Double::class.java) ?: 0.0
                        val category = snapshot.child("expenseCategory").getValue(String::class.java) ?: ""
                        val date = snapshot.child("expenseDate").getValue(Long::class.java) ?: 0L
                        val isOwed = snapshot.child("expenseIsOwed").getValue(Boolean::class.java) ?: false
                        val repeat = snapshot.child("expenseRepeat").getValue(String::class.java) ?: "None"
                        val notes = snapshot.child("expenseNotes").getValue(String::class.java)
                        val image = snapshot.child("expenseImage").getValue(String::class.java)

                        val expense = Expense_Entity(
                            id = 0, // Not used
                            expenseName = name,
                            expenseAmount = amount,
                            expenseCategory = category,
                            expenseDate = date,
                            expenseIsOwed = isOwed,
                            expenseRepeat = parseRepeatType(repeat),
                            expenseType = if (isOwed) ExpenseType.Income else ExpenseType.Expense,
                            expenseNotes = notes,
                            expenseImage = image,
                            expenseStart = null,
                            expenseEnd = null
                        )
                        callback(expense)
                    } else {
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("EditTransactionViewModel", "Error loading transaction from Firebase", error.toException())
                    callback(null)
                }
            })
    }
}
