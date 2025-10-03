# SpendSprout Kotlin Scripts Documentation

## Overview
This document explains the Kotlin classes and scripts used in the SpendSprout financial app. The app follows MVVM architecture with proper separation of concerns and modern Android development practices.

## Architecture Overview

### MVVM Pattern
- **Model**: Data classes** for transactions, accounts, categories
- **View**: Activities and XML layouts
- **ViewModel**: Business logic and data management
- **Repository**: Data access layer (implied)

## Core Activities

### 1. Main Navigation Activities

#### `OverviewActivity.kt`
**Purpose**: Main dashboard showing financial overview
**Location**: `app/src/main/java/com/example/spendsprout_opsc/overview/`
**Key Features**:
- Displays total balance
- Shows income vs expenses chart
- Lists recent transactions
- Navigation drawer integration

**Key Methods**:
```kotlin
private fun setupUI() // Initialize UI components
private fun setupTransactionRecyclerView() // Configure transaction list
private fun observeData() // Watch for data changes
```

**Navigation**: Central hub connecting to all other screens

#### `TransactionsActivity.kt`
**Purpose**: Manages transaction list and filtering
**Location**: `app/src/main/java/com/example/spendsprout_opsc/transactions/`
**Key Features**:
- Transaction list with RecyclerView
- Filter functionality (All, Income, Expenses, etc.)
- FAB for adding new transactions
- Navigation drawer

**Key Methods**:
```kotlin
private fun setupTransactionRecyclerView() // Configure transaction adapter
private fun setupFab() // Handle FAB click navigation
private fun showFilterDialog() // Display filter options
private fun applyFilter(filter: String) // Apply selected filter
```

**Data Flow**:
- Uses `TransactionsViewModel` for data management
- `TransactionAdapter` handles list display
- FAB navigates to `EditTransactionActivity`

#### `CategoriesActivity.kt`
**Purpose**: Manages expense categories and subcategories
**Location**: `app/src/main/java/com/example/spendsprout_opsc/categories/`
**Key Features**:
- Hierarchical category display
- Filter by category type (Needs, Wants, Savings)
- FAB for adding new categories
- Navigation to subcategory screens

**Key Methods**:
```kotlin
private fun setupCategoryRecyclerView() // Configure category adapter
private fun setupFab() // Handle FAB navigation
private fun setupFilters() // Configure filter button
private fun showFilterDialog() // Display category filters
private fun applyFilter(type: String) // Apply category filter
```

**Data Flow**:
- Uses `CategoriesViewModel` for data management
- `HierarchicalCategoryAdapter` handles category display
- Filter system for category types

#### `AccountsActivity.kt`
**Purpose**: Manages user accounts and balances
**Location**: `app/src/main/java/com/example/spendsprout_opsc/accounts/`
**Key Features**:
- Account list with balances
- Recent transaction display
- FAB for adding new accounts
- Account management

**Key Methods**:
```kotlin
private fun setupAccountRecyclerView() // Configure account adapter
private fun setupFab() // Handle FAB navigation
private fun observeData() // Watch for account changes
```

**Data Flow**:
- Uses `AccountsViewModel` for data management
- `AccountAdapter` handles account display
- FAB navigates to `EditAccountActivity`

#### `WantsCategoryActivity.kt`
**Purpose**: Manages subcategories under main categories
**Location**: `app/src/main/java/com/example/spendsprout_opsc/wants/`
**Key Features**:
- Subcategory list display
- Category summary (e.g., "- R 120")
- FAB for adding subcategories
- Parent category context

**Key Methods**:
```kotlin
private fun setupSubcategoryRecyclerView() // Configure subcategory adapter
private fun setupFab() // Handle FAB navigation
private fun updateSummary() // Update category totals
```

**Data Flow**:
- Uses `SubcategoryAdapter` for list management
- FAB navigates to `EditCategoryActivity` for subcategories

### 2. Settings Activity

#### `SettingsActivity.kt`
**Purpose**: App settings and preferences management
**Location**: `app/src/main/java/com/example/spendsprout_opsc/settings/`
**Key Features**:
- Row-based settings interface
- Currency, language, theme selection
- Security settings (fingerprint, PIN)
- Notification preferences
- About and help dialogs

**Key Methods**:
```kotlin
private fun setHeaderTitle() // Set "Settings" title
private fun setupRows() // Configure all setting rows
private fun setupBack() // Handle back button
private fun showSingleChoice() // Display selection dialogs
private fun showDialog() // Show info dialogs
```

**Settings Handled**:
- **Currency**: ZAR, USD, EUR, GBP selection
- **Language**: English, Afrikaans, Zulu, Xhosa
- **Theme**: Dark/Light mode toggle
- **Security**: Fingerprint vs PIN authentication
- **Notifications**: Enable/disable toggle
- **About/Help**: Information dialogs

**Data Persistence**:
```kotlin
val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
// Save/load preferences for all settings
```

### 3. Edit Activities

#### `EditTransactionActivity.kt`
**Purpose**: Create and edit financial transactions
**Location**: `app/src/main/java/com/example/spendsprout_opsc/edit/`
**Key Features**:
- Comprehensive transaction form
- Category and account selection
- Date picker integration
- Repeat transaction options
- Owe/owed functionality
- Notes and image attachment

**Form Fields**:
- **Description**: Transaction description
- **Amount**: Numeric input with currency formatting
- **Category**: Dropdown selection
- **Date**: Date picker (defaults to current date)
- **Account**: Account selection
- **Repeat**: Checkbox with frequency selection
- **Owe**: Checkbox with contact selection
- **Notes**: Multi-line text with image attachment

**Key Methods**:
```kotlin
private fun setupForm() // Initialize form fields
private fun loadTransactionData() // Populate for editing
private fun validateForm() // Validate input data
private fun saveTransaction() // Save to database
private fun setupDatePicker() // Configure date selection
```

#### `EditCategoryActivity.kt`
**Purpose**: Create and edit expense categories
**Location**: `app/src/main/java/com/example/spendsprout_opsc/edit/`
**Key Features**:
- Category name input
- Allocated amount setting
- Main category reference
- Color selection
- Notes field

**Form Fields**:
- **Sub-Category Name**: Text input
- **Allocated Amount**: Numeric input
- **Main Category**: Read-only reference
- **Color**: Dropdown selection
- **Notes**: Multi-line text

**Key Methods**:
```kotlin
private fun setupForm() // Initialize form
private fun loadCategoryData() // Populate for editing
private fun validateForm() // Validate inputs
private fun saveCategory() // Save category data
```

#### `EditAccountActivity.kt`
**Purpose**: Create and edit user accounts
**Location**: `app/src/main/java/com/example/spendsprout_opsc/edit/`
**Key Features**:
- Account name input
- Account type selection
- Balance setting
- Notes field

**Form Fields**:
- **Account Name**: Text input
- **Account Type**: Dropdown (Cash, Bank, etc.)
- **Balance**: Numeric input
- **Notes**: Multi-line text

**Key Methods**:
```kotlin
private fun setupForm() // Initialize form
private fun loadAccountData() // Populate for editing
private fun validateForm() // Validate inputs
private fun saveAccount() // Save account data
```

## Adapter Classes

### 1. Transaction Adapter

#### `TransactionAdapter.kt`
**Purpose**: Manages transaction list display
**Location**: `app/src/main/java/com/example/spendsprout_opsc/transactions/`
**Key Features**:
- RecyclerView adapter for transactions
- Edit button integration
- Color-coded transaction types
- Amount formatting (positive/negative)

**Key Methods**:
```kotlin
override fun onBindViewHolder() // Bind transaction data to views
private fun setupEditButton() // Configure edit button click
fun updateData() // Refresh transaction list
```

**ViewHolder Structure**:
```kotlin
class TransactionViewHolder {
    val dateTextView: TextView
    val descriptionTextView: TextView
    val amountTextView: TextView
    val colorIndicator: View
    val editButton: ImageButton
}
```

**Edit Functionality**:
```kotlin
holder.editButton.setOnClickListener {
    val intent = Intent(context, EditTransactionActivity::class.java)
    intent.putExtra("transactionId", transaction.id)
    intent.putExtra("isEdit", true)
    context.startActivity(intent)
}
```

### 2. Account Adapter

#### `AccountAdapter.kt`
**Purpose**: Manages account list display
**Location**: `app/src/main/java/com/example/spendsprout_opsc/accounts/`
**Key Features**:
- Account card display
- Recent transaction integration
- Balance and limit display
- Edit functionality

**Key Methods**:
```kotlin
override fun onBindViewHolder() // Bind account data
private fun setupRecentTransactions() // Configure transaction display
private fun setupEditButton() // Handle edit clicks
```

**ViewHolder Structure**:
```kotlin
class AccountViewHolder {
    val nameTextView: TextView
    val balanceTextView: TextView
    val limitTextView: TextView
    val transaction1TextView: TextView
    val transaction2TextView: TextView
    val transaction3TextView: TextView
    val amount1TextView: TextView
    val amount2TextView: TextView
    val amount3TextView: TextView
    val colorIndicator1: View
    val colorIndicator2: View
    val colorIndicator3: View
    val editButton: ImageButton
}
```

### 3. Category Adapter

#### `HierarchicalCategoryAdapter.kt`
**Purpose**: Manages category hierarchy display
**Location**: `app/src/main/java/com/example/spendsprout_opsc/categories/`
**Key Features**:
- Main category display
- Subcategory integration
- Spent vs allocated amounts
- Color-coded categories

**Key Methods**:
```kotlin
override fun onBindViewHolder() // Bind category data
private fun setupAmountDisplay() // Configure amount formatting
private fun setupEditButton() // Handle edit clicks
```

### 4. Subcategory Adapter

#### `SubcategoryAdapter.kt`
**Purpose**: Manages subcategory list display
**Location**: `app/src/main/java/com/example/spendsprout_opsc/wants/`
**Key Features**:
- Subcategory item display
- Multiple amount fields
- Edit functionality
- Parent category context

## ViewModel Classes

### 1. Transactions ViewModel

#### `TransactionsViewModel.kt`
**Purpose**: Manages transaction data and business logic
**Key Features**:
- Transaction CRUD operations
- Filtering capabilities
- Data validation
- Repository integration

**Key Methods**:
```kotlin
fun getAllTransactions(): List<Transaction>
fun getFilteredTransactions(filter: String): List<Transaction>
fun addTransaction(transaction: Transaction)
fun updateTransaction(transaction: Transaction)
fun deleteTransaction(transactionId: String)
```

### 2. Categories ViewModel

#### `CategoriesViewModel.kt`
**Purpose**: Manages category data and hierarchy
**Key Features**:
- Category hierarchy management
- Filtering by type
- Subcategory relationships
- Amount calculations

**Key Methods**:
```kotlin
fun getAllCategories(): List<Category>
fun getFilteredCategories(type: String): List<Category>
fun addCategory(category: Category)
fun updateCategory(category: Category)
fun deleteCategory(categoryId: String)
```

### 3. Accounts ViewModel

#### `AccountsViewModel.kt`
**Purpose**: Manages account data and balances
**Key Features**:
- Account management
- Balance calculations
- Recent transaction tracking
- Account type handling

**Key Methods**:
```kotlin
fun getAllAccounts(): List<Account>
fun addAccount(account: Account)
fun updateAccount(account: Account)
fun deleteAccount(accountId: String)
fun getAccountBalance(accountId: String): Double
```

## Data Models

### 1. Transaction Model

#### `Transaction.kt`
**Purpose**: Represents a financial transaction
**Properties**:
```kotlin
data class Transaction(
    val id: String,
    val description: String,
    val amount: String,
    val date: String,
    val category: String,
    val account: String,
    val color: String,
    val isIncome: Boolean,
    val repeatFrequency: String?,
    val notes: String?
)
```

### 2. Account Model

#### `Account.kt`
**Purpose**: Represents a user account
**Properties**:
```kotlin
data class Account(
    val id: String,
    val name: String,
    val type: String,
    val balance: String,
    val limit: String,
    val recentTransactions: List<Transaction>
)
```

### 3. Category Model

#### `Category.kt`
**Purpose**: Represents expense categories
**Properties**:
```kotlin
data class Category(
    val id: String,
    val name: String,
    val type: String,
    val allocatedAmount: String,
    val spentAmount: String,
    val color: String,
    val subcategories: List<Subcategory>
)
```

## Navigation Implementation

### 1. Drawer Navigation

**Implementation Pattern**:
```kotlin
override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        R.id.nav_overview -> startActivity(Intent(this, OverviewActivity::class.java))
        R.id.nav_categories -> startActivity(Intent(this, CategoriesActivity::class.java))
        R.id.nav_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
        R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
        R.id.nav_reports -> startActivity(Intent(this, ReportsActivity::class.java))
        R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        R.id.nav_exit -> finishAffinity()
    }
    drawerLayout.closeDrawer(GravityCompat.START)
    return true
}
```

### 2. FAB Navigation

**Implementation Pattern**:
```kotlin
private fun setupFab() {
    val fab = findViewById<FloatingActionButton>(R.id.fab_AddTransaction)
    fab.setOnClickListener {
        val intent = Intent(this, EditTransactionActivity::class.java)
        startActivity(intent)
    }
}
```

### 3. Edit Navigation

**Implementation Pattern**:
```kotlin
holder.editButton.setOnClickListener {
    val intent = Intent(context, EditTransactionActivity::class.java)
    intent.putExtra("transactionId", transaction.id)
    intent.putExtra("isEdit", true)
    context.startActivity(intent)
}
```

## Data Persistence

### 1. SharedPreferences Usage

**Settings Storage**:
```kotlin
val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)

// Save preference
sharedPref.edit().putString("Currency", "ZAR").apply()

// Load preference
val currency = sharedPref.getString("Currency", "ZAR")
```

**Common Settings**:
- Currency selection
- Language preference
- Theme mode (Dark/Light)
- Security settings
- Notification preferences

### 2. Intent Extras

**Edit Mode Detection**:
```kotlin
val isEdit = intent.getBooleanExtra("isEdit", false)
val itemId = intent.getStringExtra("transactionId")

if (isEdit && itemId != null) {
    loadExistingData(itemId)
} else {
    setupNewItem()
}
```

## Error Handling

### 1. Form Validation

**Transaction Validation**:
```kotlin
private fun validateForm(): Boolean {
    if (description.isEmpty()) {
        showError("Description is required")
        return false
    }
    if (amount.isEmpty() || amount.toDoubleOrNull() == null) {
        showError("Valid amount is required")
        return false
    }
    return true
}
```

### 2. Navigation Error Handling

**Safe Navigation**:
```kotlin
private fun navigateToEdit(itemId: String) {
    try {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("itemId", itemId)
        startActivity(intent)
    } catch (e: Exception) {
        showError("Unable to open edit screen")
    }
}
```

## Performance Optimizations

### 1. RecyclerView Optimization

**Adapter Efficiency**:
```kotlin
class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private var transactions: List<Transaction> = emptyList()
    
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
```

### 2. ViewHolder Pattern

**Efficient View Binding**:
```kotlin
class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val dateTextView: TextView = view.findViewById(R.id.txt_Date)
    val descriptionTextView: TextView = view.findViewById(R.id.txt_Description)
    val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
    val editButton: ImageButton = view.findViewById(R.id.btn_Edit)
}
```

## Testing Considerations

### 1. Unit Testing

**ViewModel Testing**:
```kotlin
@Test
fun testGetAllTransactions() {
    val viewModel = TransactionsViewModel()
    val transactions = viewModel.getAllTransactions()
    assertTrue(transactions.isNotEmpty())
}
```

### 2. UI Testing

**Activity Testing**:
```kotlin
@Test
fun testFabClick() {
    onView(withId(R.id.fab_AddTransaction))
        .perform(click())
    intended(hasComponent(EditTransactionActivity::class.java.name))
}
```

## Best Practices Implemented

### 1. Code Organization
- Package structure by feature
- Clear separation of concerns
- Consistent naming conventions

### 2. Error Handling
- Form validation
- Safe navigation
- User-friendly error messages

### 3. Performance
- RecyclerView optimization
- Efficient data binding
- Minimal memory usage

### 4. Maintainability
- Clear method names
- Comprehensive documentation
- Modular architecture

This Kotlin implementation provides a solid foundation for the SpendSprout financial app with proper architecture, efficient data management, and user-friendly interfaces.
