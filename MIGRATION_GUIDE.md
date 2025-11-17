# Firebase Migration Guide

## Setup Instructions

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use existing)
3. Add Android app with package name: `com.example.spendsprout_opsc`

### 2. Download google-services.json
1. Download `google-services.json` from Firebase Console
2. Place it in `app/` directory (same level as `build.gradle.kts`)

### 3. Enable Realtime Database
1. In Firebase Console, go to **Realtime Database**
2. Click **Create Database**
3. Start in **test mode** (for now - we'll add security rules later)
4. Choose a location closest to your users

### 4. Enable Authentication (Optional but Recommended)
1. In Firebase Console, go to **Authentication**
2. Click **Get Started**
3. Enable **Email/Password** sign-in method

## Current Status

### ✅ What's Done
- Firebase dependencies added
- BudgetRepository created (abstraction layer)
- Repository supports both Firebase and Room
- Budget operations migrated to use repository
- Room still works as fallback
- Accounts, categories, subcategories, transactions, Sprout, and Reports now read/write via Firebase-backed repositories

### ⚙️ Current Configuration
- **Firebase flags enabled** in `FirebaseMigrationConfig` for budgets, accounts, categories, subcategories, and transactions
- Users must be authenticated for Firebase paths (`users/{uid}/...`)
- Automatic fallback to Room remains for offline/unauthenticated states
- Data syncs in both directions (writes mirror to the other store)

## Migration Strategy

### Phase 1: Setup (Current)
- ✅ Firebase setup complete
- ✅ Repository abstraction layer created
- ✅ Budget operations use repository
- ⏳ Room still active (no breaking changes)

### Phase 2: Test Firebase (Next)
1. Set `useFirebase = true` in `BudgetRepository.kt`
2. Test budget operations
3. Verify data syncs correctly
4. Fix any issues

### Phase 3: Migrate Other Entities
1. Create repositories for Categories, Expenses, etc.
2. Migrate one entity at a time
3. Test thoroughly before moving to next

### Phase 4: Remove Room (Final)
- Only after all entities migrated
- Only after extensive testing
- Keep Room code commented for rollback

## How to Enable Firebase

1. Open `app/src/main/java/com/example/spendsprout_opsc/firebase/BudgetRepository.kt`
2. Find line: `private val useFirebase = false`
3. Change to: `private val useFirebase = true`
4. Make sure user is authenticated (or handle anonymous auth)
5. Test budget operations

## Data Structure in Realtime Database

```
users/
  {userId}/
    budgets/
      {budgetId}/  (budget ID as key)
        id: Int
        budgetName: String
        openingBalance: Double
        budgetMinGoal: Double
        budgetMaxGoal: Double
        budgetBalance: Double
        budgetNotes: String? (nullable)
```

## Security Rules (To Add Later)

For Realtime Database, add these rules in Firebase Console:

```json
{
  "rules": {
    "users": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid",
        "budgets": {
          "$budgetId": {
            ".validate": "newData.hasChildren(['id', 'budgetName', 'openingBalance', 'budgetMinGoal', 'budgetMaxGoal', 'budgetBalance'])"
          }
        }
      }
    }
  }
}
```

## Troubleshooting

### "Missing google-services.json"
- Make sure the file is in `app/` directory
- Rebuild the project
- Check that `google-services` plugin is in `build.gradle.kts`

### "User not authenticated"
- For testing, you can use anonymous auth
- Or enable email/password auth in Firebase Console
- Add auth logic before enabling Firebase

### "Data not syncing"
- Check Firebase Console Realtime Database to see if data is created
- Check Logcat for errors
- Verify Realtime Database is enabled in Firebase Console
- Check Realtime Database rules (should allow read/write for authenticated users)

## Notes

- Room database still works - no breaking changes
- Firebase is optional for now
- Can switch back to Room anytime by setting `useFirebase = false`
- Data will sync to both when Firebase is enabled (gradual migration)

