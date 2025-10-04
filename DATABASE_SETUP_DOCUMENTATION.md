# SpendSprout Database Setup Documentation

## Overview
This document describes the complete database setup for the SpendSprout application, including Room database configuration, entity relationships, and data initialization.

## Database Architecture

### Entities
1. **Category_Entity** - Main budget categories (Needs, Wants, Savings)
2. **Subcategory_Entity** - Subcategories under main categories
3. **Account_Entity** - User financial accounts (Cash, Bank, Credit)
4. **Payment_Entity** - Individual transactions
5. **Contact_Entity** - People who owe or are owed money

### Relationships
- Subcategory → Category (Many-to-One)
- Payment → Subcategory (Many-to-One)
- Payment → Account (Many-to-One)
- Payment → Contact (Many-to-One, Optional)

### Database Features
- **Foreign Key Constraints** with proper cascading
- **Database Indices** for optimal query performance
- **Migration Support** for schema updates
- **Error Handling** with comprehensive logging
- **Data Validation** to ensure database integrity

## Setup Process

### 1. Database Initialization
The database is automatically initialized when the app starts through `SpendSproutApplication`:

```kotlin
@HiltAndroidApp
class SpendSproutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Setup database connection
        databaseSetup.setupDatabase()
        
        // Initialize database with sample data
        databaseInitializer.initializeDatabase()
        
        // Validate database setup
        databaseValidator.validateDatabase()
    }
}
```

### 2. Sample Data
The database is populated with realistic sample data:
- **3 Categories**: Needs, Wants, Savings
- **9 Subcategories**: Groceries, Transport, Entertainment, etc.
- **3 Accounts**: Cash, FNB Next Transact, Standard Bank Credit
- **5 Transactions**: Recent financial activity

### 3. Repository Pattern
All database operations go through repository classes:
- `AccountRepository` - Account management
- `CategoryRepository` - Category operations
- `SubcategoryRepository` - Subcategory management
- `TransactionRepository` - Transaction handling

## Key Features

### Performance Optimizations
- **Database Indices** on all foreign key columns
- **Reactive Data Flow** using StateFlow
- **Coroutine-based** operations for non-blocking database access

### Error Handling
- **Comprehensive error logging** for debugging
- **Graceful error recovery** for database operations
- **Migration fallback** for schema changes

### Data Validation
- **Database health checks** on startup
- **Data integrity validation** for all operations
- **Automatic error reporting** for failed operations

## Usage Examples

### Adding a New Transaction
```kotlin
val transaction = Payment_Entity(
    id = 6,
    subcategoryId = 1,
    accountId = 2,
    contactId = null,
    paymentName = "Coffee",
    paymentDate = System.currentTimeMillis(),
    paymentAmount = 50.0,
    paymentType = TransactionType.Expense,
    paymentIsOwed = false,
    paymentRepeat = RepeatType.None,
    paymentNotes = "Morning coffee",
    paymentImage = null
)

transactionRepository.insertTransaction(transaction)
```

### Querying Recent Transactions
```kotlin
transactionRepository.getRecentTransactions(5).collect { transactions ->
    // Update UI with recent transactions
}
```

## Troubleshooting

### Common Issues
1. **Database not initialized**: Check that `SpendSproutApplication` is properly configured in AndroidManifest.xml
2. **Migration errors**: Ensure database version is incremented and migrations are properly defined
3. **Performance issues**: Check that database indices are properly configured

### Debug Information
- Database operations are logged with tag "DatabaseErrorHandler"
- Validation results are logged with tag "DatabaseValidator"
- All database errors include stack traces for debugging

## Future Enhancements
- **Data synchronization** with cloud services
- **Advanced querying** with complex filters
- **Data export/import** functionality
- **Backup and restore** capabilities
