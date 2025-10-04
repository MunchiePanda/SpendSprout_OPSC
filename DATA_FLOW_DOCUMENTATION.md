# Data Flow Documentation - Room Database to Application Logic

## Overview
This document explains how the Room database scripts are connected to the essential Kotlin scripts for data handling in the SpendSprout application.

## Room Database Structure

### Core Entities
- **Account_Entity**: Bank accounts, cash, debit cards
- **Category_Entity**: Main budget categories (Needs, Wants, Savings)
- **Subcategory_Entity**: Sub-categories within main categories
- **Payment_Entity**: Individual transactions (income/expenses)
- **Contact_Entity**: People you owe money to or who owe you

### Database Relationships
```
Account_Entity (1) ←→ (N) Payment_Entity
Category_Entity (1) ←→ (N) Subcategory_Entity
Subcategory_Entity (1) ←→ (N) Payment_Entity
Contact_Entity (1) ←→ (N) Payment_Entity (optional)
```

## Data Flow Architecture

### 1. DataMapper.kt
**Purpose**: Converts between Room entities and application models

**Key Functions**:
- `Account_Entity.toAccount()`: Converts database account to app model
- `Category_Entity.toCategory()`: Converts database category to app model
- `Payment_Entity.toTransaction()`: Converts database payment to app model
- `Subcategory_Entity.toSubcategory()`: Converts database subcategory to app model

**Example**:
```kotlin
// Database entity to app model
val account = accountEntity.toAccount()

// App model to database entity
val accountEntity = account.toAccountEntity()
```

### 2. DataService.kt
**Purpose**: Provides clean interface between Room database and application logic

**Key Operations**:
- **Account Operations**: `getAllAccounts()`, `insertAccount()`, `updateAccount()`, `deleteAccount()`
- **Category Operations**: `getAllCategories()`, `insertCategory()`, `updateCategory()`, `deleteCategory()`
- **Transaction Operations**: `getAllTransactions()`, `insertTransaction()`, `updateTransaction()`, `deleteTransaction()`
- **Dashboard Data**: `getDashboardData()` - combines all data for overview screen

**Example**:
```kotlin
// Get all accounts for AccountsActivity
val accounts = dataService.getAllAccounts()

// Create new account from EditAccountActivity
dataService.insertAccount(newAccount)
```

### 3. DataFlowManager.kt
**Purpose**: Coordinates data flow between Room database and UI components

**Screen-Specific Data Flow**:
- **OverviewActivity**: `getDashboardDataForOverview()`, `getAccountSummariesForOverview()`
- **AccountsActivity**: `getAccountsForAccountsScreen()`
- **CategoriesActivity**: `getCategoriesForCategoriesScreen()`
- **TransactionsActivity**: `getTransactionsForTransactionsScreen()`
- **ReportsActivity**: `getTotalIncomeForReports()`, `getTotalExpensesForReports()`

**Example**:
```kotlin
// In OverviewActivity
val dashboardData = dataFlowManager.getDashboardDataForOverview()

// In AccountsActivity
val accounts = dataFlowManager.getAccountsForAccountsScreen()
```

## Data Flow Connections

### Room Database → Application Models
```
Room Entity → DataMapper → App Model → UI Component
```

**Example Flow**:
1. `Account_Entity` (from Room database)
2. `DataMapper.toAccount()` (converts to app model)
3. `Account` (app model)
4. `AccountAdapter` (displays in UI)

### Application Models → Room Database
```
UI Input → App Model → DataMapper → Room Entity → Database
```

**Example Flow**:
1. User inputs account data in `EditAccountActivity`
2. Creates `Account` app model
3. `DataMapper.toAccountEntity()` (converts to database entity)
4. `Account_Entity` (saved to Room database)

## Screen-Specific Data Connections

### OverviewActivity
- **Data Source**: `DataFlowManager.getDashboardDataForOverview()`
- **Room Entities**: `Account_Entity`, `Category_Entity`, `Payment_Entity`
- **App Models**: `AccountSummary`, `CategorySummary`, `Transaction`
- **UI Components**: `AccountSummaryAdapter`, `CategorySummaryAdapter`, `TransactionAdapter`

### AccountsActivity
- **Data Source**: `DataFlowManager.getAccountsForAccountsScreen()`
- **Room Entities**: `Account_Entity`
- **App Models**: `Account`
- **UI Components**: `AccountAdapter`

### CategoriesActivity
- **Data Source**: `DataFlowManager.getCategoriesForCategoriesScreen()`
- **Room Entities**: `Category_Entity`, `Subcategory_Entity`
- **App Models**: `Category`, `Subcategory`
- **UI Components**: `CategoryAdapter`, `HierarchicalCategoryAdapter`

### TransactionsActivity
- **Data Source**: `DataFlowManager.getTransactionsForTransactionsScreen()`
- **Room Entities**: `Payment_Entity`
- **App Models**: `Transaction`
- **UI Components**: `TransactionAdapter`

### ReportsActivity
- **Data Source**: `DataFlowManager.getTotalIncomeForReports()`, `getTotalExpensesForReports()`
- **Room Entities**: `Payment_Entity`
- **App Models**: Chart data, summary statistics
- **UI Components**: `ChartView`, `CircularProgressView`

## Data Validation

### Account Validation
```kotlin
fun validateAccount(account: Account): Boolean {
    return account.name.isNotBlank() && account.balance >= 0
}
```

### Category Validation
```kotlin
fun validateCategory(category: Category): Boolean {
    return category.name.isNotBlank() && category.allocation >= 0
}
```

### Transaction Validation
```kotlin
fun validateTransaction(transaction: Transaction): Boolean {
    return transaction.name.isNotBlank() && transaction.amount > 0
}
```

## Dependency Injection

### DataFlowModule.kt
Provides dependency injection for data flow components:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataFlowModule {
    @Provides
    @Singleton
    fun provideDataService(...): DataService
    
    @Provides
    @Singleton
    fun provideDataFlowManager(dataService: DataService): DataFlowManager
}
```

## Usage Examples

### Creating a New Account
```kotlin
// 1. User inputs data in EditAccountActivity
val newAccount = Account(
    id = 0, // Auto-generated
    name = "FNB Savings",
    type = AccountType.Debit,
    balance = 5000.0,
    notes = "Main savings account"
)

// 2. Validate data
if (dataFlowManager.validateAccount(newAccount)) {
    // 3. Save to database
    dataFlowManager.createAccount(newAccount)
}
```

### Displaying Account Data
```kotlin
// 1. Get accounts from database
val accounts = dataFlowManager.getAccountsForAccountsScreen()

// 2. Observe data changes
lifecycleScope.launch {
    accounts.collect { accountList ->
        // 3. Update UI
        accountAdapter.updateData(accountList)
    }
}
```

### Dashboard Data Flow
```kotlin
// 1. Get complete dashboard data
val dashboardData = dataFlowManager.getDashboardDataForOverview()

// 2. Observe data changes
lifecycleScope.launch {
    dashboardData.collect { data ->
        // 3. Update all dashboard components
        accountAdapter.updateData(data.accounts)
        categoryAdapter.updateData(data.categories)
        transactionAdapter.updateData(data.recentTransactions)
    }
}
```

## Key Benefits

1. **Separation of Concerns**: Database logic separated from UI logic
2. **Data Consistency**: Single source of truth for all data operations
3. **Type Safety**: Compile-time validation of all data operations
4. **Reactive Updates**: Automatic UI updates when data changes
5. **Validation**: Centralized data validation before database operations
6. **Testability**: Easy to mock and test data operations

## File Structure
```
app/src/main/java/com/example/spendsprout_opsc/
├── model/
│   └── DataMapper.kt                    # Entity ↔ Model conversions
├── service/
│   └── DataService.kt                  # Database operations interface
├── manager/
│   └── DataFlowManager.kt             # Data flow coordination
├── di/
│   └── DataFlowModule.kt              # Dependency injection
└── roomdb/                            # Room database entities
    ├── Account_Entity.kt
    ├── Category_Entity.kt
    ├── Payment_Entity.kt
    └── ...
```

This architecture ensures that all Room database operations are properly connected to the application logic, providing a clean, maintainable, and scalable data flow system.
