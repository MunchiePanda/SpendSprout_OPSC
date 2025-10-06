package com.example.spendsprout_opsc.model

import com.example.spendsprout_opsc.roomdb.Account_Entity
import com.example.spendsprout_opsc.roomdb.Category_Entity
import com.example.spendsprout_opsc.roomdb.Expense_Entity
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import com.example.spendsprout_opsc.roomdb.Contact_Entity
import com.example.spendsprout_opsc.overview.model.AccountSummary
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.Transaction
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.wants.model.Subcategory
import com.example.spendsprout_opsc.transactions.model.Transaction as UiTransaction

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
        limit = "R ${String.format("%.0f", this.accountBalance * 1.5)}"
    )
}

// ==================== CATEGORY MAPPINGS ====================

fun Category_Entity.toCategory(): Category {
    return Category(
        id = this.id.toString(),
        name = this.categoryName,
        spent = "R ${String.format("%.0f", this.categoryBalance)}",
        allocation = "R ${String.format("%.0f", this.categoryAllocation)}",
        color = getColorFromInt(this.categoryColor)
    )
}

fun Category.toCategoryEntity(): Category_Entity {
    return Category_Entity(
        id = this.id.toIntOrNull() ?: 0,
        categoryName = this.name,
        categoryColor = parseColorToInt(this.color),
        categoryBalance = parseMoneyToDouble(this.spent),
        categoryAllocation = parseMoneyToDouble(this.allocation),
        categoryNotes = null
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
        id = this.id.toString(),
        name = this.subcategoryName,
        spent = "R ${String.format("%.0f", this.subcategoryBalance)}",
        allocation = "R ${String.format("%.0f", this.subcategoryAllocation)}",
        color = getColorFromInt(this.subcategoryColor)
    )
}

fun Subcategory.toSubcategoryEntity(): Subcategory_Entity {
    return Subcategory_Entity(
        id = this.id.toIntOrNull() ?: 0,
        categoryId = 0,
        subcategoryName = this.name,
        subcategoryColor = parseColorToInt(this.color),
        subcategoryBalance = parseMoneyToDouble(this.spent),
        subcategoryAllocation = parseMoneyToDouble(this.allocation),
        subcategoryNotes = null
    )
}

// ==================== TRANSACTION MAPPINGS ====================

fun Expense_Entity.toOverviewTransaction(): Transaction {
    return Transaction(
        date = formatDate(this.expenseDate),
        description = this.expenseName,
        amount = formatAmount(this.expenseAmount, this.expenseType.name),
        color = getCategoryColorFromName(this.expenseCategory)
    )
}

fun Expense_Entity.toUiTransaction(): UiTransaction {
    return UiTransaction(
        id = this.id.toString(),
        date = formatDate(this.expenseDate),
        description = this.expenseName,
        amount = formatAmount(this.expenseAmount, this.expenseType.name),
        color = getCategoryColorFromName(this.expenseCategory),
        imagePath = this.expenseImage
    )
}

fun UiTransaction.toExpenseEntity(): Expense_Entity {
    val parsedAmount = this.amount.replace("R", "").replace("+", "").replace("-", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
    val isIncome = this.amount.trim().startsWith("+")
    val type = if (isIncome) com.example.spendsprout_opsc.ExpenseType.Income else com.example.spendsprout_opsc.ExpenseType.Expense
    // Note: category, start/end are not provided by UI model; using placeholders
    return Expense_Entity(
        expenseName = this.description,
        expenseDate = java.util.Date().time,
        expenseAmount = parsedAmount,
        expenseType = type,
        expenseIsOwed = false,
        expenseRepeat = com.example.spendsprout_opsc.RepeatType.None,
        expenseNotes = null,
        expenseImage = this.imagePath,
        expenseCategory = "Misc",
        expenseStart = null,
        expenseEnd = null
    )
}

// ==================== HELPER FUNCTIONS ====================

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}

private fun formatAmount(amount: Double, type: String): String {
    val sign = if (type == "Expense") "-" else "+"
    return "$sign R ${String.format("%.0f", amount)}"
}

private fun getCategoryColorFromName(categoryName: String): String {
    return when (categoryName.lowercase()) {
        "groceries" -> "#87CEEB"
        "needs" -> "#4169E1"
        "wants" -> "#9370DB"
        "savings" -> "#32CD32"
        else -> "#D3D3D3"
    }
}

private fun parseColorToInt(color: String): Int {
    return try { android.graphics.Color.parseColor(color) } catch (_: Throwable) { 0xFFCCCCCC.toInt() }
}

private fun parseMoneyToDouble(text: String): Double {
    return text.replace("R", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
}

private fun getColorFromInt(colorInt: Int): String {
    return String.format("#%06X", 0xFFFFFF and colorInt)
}