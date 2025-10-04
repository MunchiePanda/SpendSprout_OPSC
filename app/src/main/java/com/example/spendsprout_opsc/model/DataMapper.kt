package com.example.spendsprout_opsc.model

import com.example.spendsprout_opsc.roomdb.Account_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import com.example.spendsprout_opsc.roomdb.Payment_Entity
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import com.example.spendsprout_opsc.roomdb.Contact_Entity
import com.example.spendsprout_opsc.overview.model.AccountSummary
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.Transaction
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.wants.model.Subcategory
import com.example.spendsprout_opsc.transactions.model.Transaction as TransactionModel

/**
 * DataMapper - Essential Data Flow Connection
 * 
 * This class connects Room database entities to application models
 * and handles data transformation between database and UI layers.
 */

// ==================== ACCOUNT MAPPINGS ====================

fun Account_Entity.toAccount(): Account {
    return Account(
        id = this.id,
        name = this.accountName,
        type = this.accountType,
        balance = this.accountBalance,
        notes = this.accountNotes
    )
}

fun Account.toAccountEntity(): Account_Entity {
    return Account_Entity(
        id = this.id,
        accountName = this.name,
        accountType = this.type,
        accountBalance = this.balance,
        accountNotes = this.notes
    )
}

fun Account_Entity.toAccountSummary(): AccountSummary {
    return AccountSummary(
        name = this.accountName,
        balance = "R ${String.format("%.0f", this.accountBalance)}",
        limit = "R ${String.format("%.0f", this.accountBalance * 1.5)}" // Mock limit
    )
}

// ==================== CATEGORY MAPPINGS ====================

fun Category_Entity.toCategory(): Category {
    return Category(
        id = this.id,
        name = this.categoryName,
        color = this.categoryColor,
        balance = this.categoryBalance,
        allocation = this.categoryAllocation,
        notes = this.categoryNotes
    )
}

fun Category.toCategoryEntity(): Category_Entity {
    return Category_Entity(
        id = this.id,
        categoryName = this.name,
        categoryColor = this.color,
        categoryBalance = this.balance,
        categoryAllocation = this.allocation,
        categoryNotes = this.notes
    )
}

fun Category_Entity.toCategorySummary(): CategorySummary {
    return CategorySummary(
        name = this.categoryName,
        spent = "R ${String.format("%.0f", this.categoryBalance)}",
        allocated = "R ${String.format("%.0f", this.categoryAllocation)}",
        color = getColorFromInt(this.categoryColor)
    )
}

// ==================== SUBCATEGORY MAPPINGS ====================

fun Subcategory_Entity.toSubcategory(): Subcategory {
    return Subcategory(
        id = this.id,
        categoryId = this.categoryId,
        name = this.subcategoryName,
        color = this.subcategoryColor,
        balance = this.subcategoryBalance,
        allocation = this.subcategoryAllocation,
        notes = this.subcategoryNotes
    )
}

fun Subcategory.toSubcategoryEntity(): Subcategory_Entity {
    return Subcategory_Entity(
        id = this.id,
        categoryId = this.categoryId,
        subcategoryName = this.name,
        subcategoryColor = this.color,
        subcategoryBalance = this.balance,
        subcategoryAllocation = this.allocation,
        subcategoryNotes = this.notes
    )
}

// ==================== TRANSACTION MAPPINGS ====================

fun Payment_Entity.toTransaction(): Transaction {
    return Transaction(
        date = formatDate(this.paymentDate),
        description = this.paymentName,
        amount = formatAmount(this.paymentAmount, this.paymentType.name),
        color = getCategoryColor(this.subcategoryId)
    )
}

fun Payment_Entity.toTransactionModel(): TransactionModel {
    return TransactionModel(
        id = this.id,
        subcategoryId = this.subcategoryId,
        accountId = this.accountId,
        contactId = this.contactId,
        name = this.paymentName,
        date = this.paymentDate,
        amount = this.paymentAmount,
        type = this.paymentType,
        isOwed = this.paymentIsOwed,
        repeat = this.paymentRepeat,
        notes = this.paymentNotes,
        image = this.paymentImage
    )
}

fun TransactionModel.toPaymentEntity(): Payment_Entity {
    return Payment_Entity(
        id = this.id,
        subcategoryId = this.subcategoryId,
        accountId = this.accountId,
        contactId = this.contactId,
        paymentName = this.name,
        paymentDate = this.date,
        paymentAmount = this.amount,
        paymentType = this.type,
        paymentIsOwed = this.isOwed,
        paymentRepeat = this.repeat,
        paymentNotes = this.notes,
        paymentImage = this.image
    )
}

// ==================== HELPER FUNCTIONS ====================

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}

private fun formatAmount(amount: Double, type: String): String {
    val sign = if (type == "Income") "+" else "-"
    return "$sign R ${String.format("%.0f", amount)}"
}

private fun getCategoryColor(subcategoryId: Int): String {
    // Mock color based on subcategory ID
    val colors = listOf("#FF6B6B", "#FFB6C1", "#9370DB", "#4ECDC4", "#45B7D1")
    return colors[subcategoryId % colors.size]
}

private fun getColorFromInt(colorInt: Int): String {
    return String.format("#%06X", 0xFFFFFF and colorInt)
}