package com.example.spendsprout_opsc.firebase

import android.util.Log
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.roomdb.Budget_Entity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Repository that abstracts data source - can use Firebase Realtime Database or Room
 * Currently supports both for gradual migration
 */
class BudgetRepository {
    
    private val TAG = "BudgetRepository"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // Migration flag - set to true to use Firebase, false for Room
    private val useFirebase = FirebaseMigrationConfig.useBudgets  // Start with Room, migrate gradually
    
    /**
     * Get all budgets - works with both Firebase and Room
     * Automatically falls back to Room if Firebase is enabled but user not authenticated
     */
    fun getAllBudgets(): Flow<List<Budget_Entity>> {
        return if (useFirebase && isUserAuthenticated()) {
            getAllBudgetsFromFirebase()
        } else {
            // Fall back to Room if Firebase not enabled or user not authenticated
            getAllBudgetsFromRoom()
        }
    }
    
    /**
     * Get budget by ID
     * Automatically falls back to Room if Firebase is enabled but user not authenticated
     */
    suspend fun getBudgetById(id: Int): Budget_Entity? {
        return if (useFirebase && isUserAuthenticated()) {
            getBudgetByIdFromFirebase(id)
        } else {
            // Fall back to Room if Firebase not enabled or user not authenticated
            getBudgetByIdFromRoom(id)
        }
    }
    
    /**
     * Insert new budget
     * Automatically falls back to Room if Firebase is enabled but user not authenticated
     * Syncs to both for gradual migration when authenticated
     */
    suspend fun insertBudget(budget: Budget_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            insertBudgetToFirebase(budget)
            // Sync to Room for gradual migration
            syncToRoom(budget)
        } else {
            // Use Room and optionally sync to Firebase if authenticated
            insertBudgetToRoom(budget)
            // Try to sync to Firebase for gradual migration (non-blocking)
            if (isUserAuthenticated()) {
                syncToFirebase(budget)
            }
        }
    }
    
    /**
     * Update budget
     * Automatically falls back to Room if Firebase is enabled but user not authenticated
     */
    suspend fun updateBudget(budget: Budget_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            updateBudgetInFirebase(budget)
            // Sync to Room
            syncToRoom(budget)
        } else {
            // Use Room and optionally sync to Firebase
            updateBudgetInRoom(budget)
            // Try to sync to Firebase if authenticated
            if (isUserAuthenticated()) {
                syncToFirebase(budget)
            }
        }
    }
    
    /**
     * Delete budget
     * Automatically falls back to Room if Firebase is enabled but user not authenticated
     */
    suspend fun deleteBudget(budget: Budget_Entity) {
        if (useFirebase && isUserAuthenticated()) {
            deleteBudgetFromFirebase(budget.id)
        } else {
            // Always use Room if not authenticated or Firebase disabled
            deleteBudgetFromRoom(budget)
        }
    }
    
    /**
     * Get budget count
     * Automatically falls back to Room if Firebase is enabled but user not authenticated
     */
    suspend fun getBudgetCount(): Int {
        return if (useFirebase && isUserAuthenticated()) {
            getBudgetCountFromFirebase()
        } else {
            // Fall back to Room
            getBudgetCountFromRoom()
        }
    }
    
    // ==================== FIREBASE REALTIME DATABASE IMPLEMENTATION ====================
    
    private fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: "anonymous"
    }
    
    private fun getBudgetsReference(): DatabaseReference {
        val userId = getUserId()
        val path = "users/$userId/budgets"
        Log.d(TAG, "getBudgetsReference: userId=$userId, path=$path")
        return database.reference.child("users").child(userId).child("budgets")
    }
    
    private fun getBudgetReference(id: Int): DatabaseReference {
        return getBudgetsReference().child(id.toString())
    }
    
    private fun getAllBudgetsFromFirebase(): Flow<List<Budget_Entity>> = callbackFlow {
        val isAuthenticated = isUserAuthenticated()
        val userId = getUserId()
        Log.d(TAG, "getAllBudgetsFromFirebase: authenticated=$isAuthenticated, userId=$userId")
        
        if (!isAuthenticated) {
            Log.w(TAG, "User not authenticated, returning empty list")
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        // Try reading from user-specific path first, then fallback to root for backward compatibility
        val budgetsRef = getBudgetsReference()
        val rootRef = database.reference.child("budgets") // Fallback for old data structure
        
        Log.d(TAG, "Listening to budgets at path: ${budgetsRef.path}")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d(TAG, "onDataChange: snapshot exists=${snapshot.exists()}, hasChildren=${snapshot.hasChildren()}, childrenCount=${snapshot.childrenCount}")
                    
                    val budgets = mutableListOf<Budget_Entity>()
                    
                    if (snapshot.exists()) {
                        // Check if this is a direct budget object or a collection
                        if (snapshot.hasChildren()) {
                            // It's a collection of budgets
                            for (budgetSnapshot in snapshot.children) {
                                try {
                                    Log.d(TAG, "Processing budget child: key=${budgetSnapshot.key}")
                                    val budget = budgetSnapshot.toBudgetEntity()
                                    if (budget != null) {
                                        budgets.add(budget)
                                        Log.d(TAG, "Added budget: ${budget.budgetName} (id=${budget.id})")
                                    } else {
                                        Log.w(TAG, "Failed to parse budget from snapshot: ${budgetSnapshot.key}")
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing budget ${budgetSnapshot.key}", e)
                                }
                            }
                        } else {
                            // It's a single budget object at the root level
                            Log.d(TAG, "Single budget object detected at root")
                            val budget = snapshot.toBudgetEntity()
                            if (budget != null) {
                                budgets.add(budget)
                                Log.d(TAG, "Added root budget: ${budget.budgetName} (id=${budget.id})")
                            }
                        }
                    } else {
                        Log.d(TAG, "No data found at user path, checking root fallback...")
                        // Check multiple fallback locations
                        val fallbackPaths = listOf(
                            database.reference.child("budgets"),
                            database.reference  // Absolute root as last resort
                        )
                        
                        var checkedPaths = 0
                        for (fallbackRef in fallbackPaths) {
                            fallbackRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(rootSnapshot: DataSnapshot) {
                                    checkedPaths++
                                    try {
                                        if (rootSnapshot.exists()) {
                                            Log.d(TAG, "Found data at fallback path: ${rootSnapshot.ref.path}")
                                            if (rootSnapshot.hasChildren()) {
                                                // Collection of budgets
                                                for (budgetSnapshot in rootSnapshot.children) {
                                                    val budget = budgetSnapshot.toBudgetEntity()
                                                    if (budget != null && budgets.none { it.id == budget.id }) {
                                                        budgets.add(budget)
                                                        Log.d(TAG, "Added fallback budget: ${budget.budgetName} (id=${budget.id})")
                                                    }
                                                }
                                            } else {
                                                // Single budget object (might be at root)
                                                val budget = rootSnapshot.toBudgetEntity()
                                                if (budget != null && budgets.none { it.id == budget.id }) {
                                                    budgets.add(budget)
                                                    Log.d(TAG, "Added single fallback budget: ${budget.budgetName} (id=${budget.id})")
                                                }
                                            }
                                        }
                                        
                                        // After checking all fallback paths, send results
                                        if (checkedPaths >= fallbackPaths.size) {
                                            budgets.sortBy { it.budgetName }
                                            Log.d(TAG, "Sending ${budgets.size} budgets from fallback paths")
                                            trySend(budgets)
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error processing fallback budgets", e)
                                        if (checkedPaths >= fallbackPaths.size) {
                                            trySend(emptyList())
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    checkedPaths++
                                    Log.e(TAG, "Error reading fallback budgets at ${fallbackRef.path}", error.toException())
                                    if (checkedPaths >= fallbackPaths.size && budgets.isEmpty()) {
                                        trySend(emptyList())
                                    }
                                }
                            })
                        }
                        return
                    }
                    
                    // Sort by name (same as Room query)
                    budgets.sortBy { it.budgetName }
                    Log.d(TAG, "Sending ${budgets.size} budgets to Flow")
                    trySend(budgets)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing budgets", e)
                    trySend(emptyList())
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening to budgets: ${error.message}", error.toException())
                trySend(emptyList())
            }
        }
        
        budgetsRef.addValueEventListener(listener)
        
        awaitClose { budgetsRef.removeEventListener(listener) }
    }
    
    private suspend fun getBudgetByIdFromFirebase(id: Int): Budget_Entity? {
        return suspendCancellableCoroutine { continuation ->
            val budgetRef = getBudgetReference(id)
            budgetRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val budget = snapshot.toBudgetEntity()
                            continuation.resume(budget)
                        } else {
                            continuation.resume(null)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing budget $id", e)
                        continuation.resume(null)
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error getting budget by ID: $id", error.toException())
                    continuation.resume(null)
                }
            })
        }
    }
    
    private suspend fun insertBudgetToFirebase(budget: Budget_Entity) {
        return suspendCancellableCoroutine { continuation ->
            try {
                val budgetRef = getBudgetReference(budget.id)
                val data = budget.toFirebaseMap()
                
                budgetRef.setValue(data) { error, _ ->
                    if (error != null) {
                        Log.e(TAG, "Error inserting budget to Firebase", error.toException())
                        continuation.resumeWith(Result.failure(error.toException()))
                    } else {
                        Log.d(TAG, "Budget inserted to Firebase: ${budget.budgetName}")
                        continuation.resume(Unit)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting budget to Firebase", e)
                continuation.resumeWith(Result.failure(e))
            }
        }
    }
    
    private suspend fun updateBudgetInFirebase(budget: Budget_Entity) {
        return suspendCancellableCoroutine { continuation ->
            try {
                val budgetRef = getBudgetReference(budget.id)
                val data = budget.toFirebaseMap()
                
                budgetRef.updateChildren(data) { error, _ ->
                    if (error != null) {
                        Log.e(TAG, "Error updating budget in Firebase", error.toException())
                        continuation.resumeWith(Result.failure(error.toException()))
                    } else {
                        Log.d(TAG, "Budget updated in Firebase: ${budget.budgetName}")
                        continuation.resume(Unit)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating budget in Firebase", e)
                continuation.resumeWith(Result.failure(e))
            }
        }
    }
    
    private suspend fun deleteBudgetFromFirebase(id: Int) {
        return suspendCancellableCoroutine { continuation ->
            try {
                val budgetRef = getBudgetReference(id)
                budgetRef.removeValue { error, _ ->
                    if (error != null) {
                        Log.e(TAG, "Error deleting budget from Firebase", error.toException())
                        continuation.resumeWith(Result.failure(error.toException()))
                    } else {
                        Log.d(TAG, "Budget deleted from Firebase: $id")
                        continuation.resume(Unit)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting budget from Firebase", e)
                continuation.resumeWith(Result.failure(e))
            }
        }
    }
    
    private suspend fun getBudgetCountFromFirebase(): Int {
        return suspendCancellableCoroutine { continuation ->
            val budgetsRef = getBudgetsReference()
            budgetsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.childrenCount.toInt())
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error getting budget count from Firebase", error.toException())
                    continuation.resume(0)
                }
            })
        }
    }
    
    // ==================== ROOM IMPLEMENTATION ====================
    
    private fun getAllBudgetsFromRoom(): Flow<List<Budget_Entity>> {
        return BudgetApp.db.budgetDao().getAll()
    }
    
    private suspend fun getBudgetByIdFromRoom(id: Int): Budget_Entity? {
        return withContext(Dispatchers.IO) {
            BudgetApp.db.budgetDao().getById(id)
        }
    }
    
    private suspend fun insertBudgetToRoom(budget: Budget_Entity) {
        withContext(Dispatchers.IO) {
            BudgetApp.db.budgetDao().insert(budget)
        }
    }
    
    private suspend fun updateBudgetInRoom(budget: Budget_Entity) {
        withContext(Dispatchers.IO) {
            BudgetApp.db.budgetDao().update(budget)
        }
    }
    
    private suspend fun deleteBudgetFromRoom(budget: Budget_Entity) {
        withContext(Dispatchers.IO) {
            BudgetApp.db.budgetDao().delete(budget)
        }
    }
    
    private suspend fun getBudgetCountFromRoom(): Int {
        return withContext(Dispatchers.IO) {
            BudgetApp.db.budgetDao().getCount()
        }
    }
    
    // ==================== SYNC HELPERS ====================
    
    private suspend fun syncToFirebase(budget: Budget_Entity) {
        if (!isUserAuthenticated()) return
        try {
            insertBudgetToFirebase(budget)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync budget to Firebase (non-critical)", e)
        }
    }
    
    private suspend fun syncToRoom(budget: Budget_Entity) {
        try {
            withContext(Dispatchers.IO) {
                val dao = BudgetApp.db.budgetDao()
                val existing = dao.getById(budget.id)
                if (existing == null) {
                    dao.insert(budget)
                } else {
                    dao.update(budget)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync budget to Room (non-critical)", e)
        }
    }
    
    // ==================== MAPPING HELPERS ====================
    
    private fun DataSnapshot.toBudgetEntity(): Budget_Entity? {
        return try {
            // Handle both modern structure (users/{uid}/budgets/{id}) and legacy root objects
            val id = getIntValue("id") ?: key?.toIntOrNull()
            if (id == null) {
                Log.w(TAG, "Could not determine ID for budget. Key: $key, hasChildren: ${hasChildren()}")
                return null
            }

            val budgetName = getStringValue("budgetName") ?: key ?: "Untitled Budget"
            val openingBalance = getDoubleValue("openingBalance") ?: 0.0
            val budgetMinGoal = getDoubleValue("budgetMinGoal") ?: 0.0
            val budgetMaxGoal = getDoubleValue("budgetMaxGoal") ?: 0.0
            val budgetBalance = getDoubleValue("budgetBalance") ?: 0.0
            val budgetNotes = getStringValue("budgetNotes")

            val budget = Budget_Entity(
                id = id,
                budgetName = budgetName,
                openingBalance = openingBalance,
                budgetMinGoal = budgetMinGoal,
                budgetMaxGoal = budgetMaxGoal,
                budgetBalance = budgetBalance,
                budgetNotes = budgetNotes
            )

            Log.d(TAG, "Parsed budget: id=$id, name=$budgetName, balance=$budgetBalance")
            budget
        } catch (e: Exception) {
            Log.e(TAG, "Error converting snapshot to Budget_Entity. Key: ${key}, Error: ${e.message}", e)
            null
        }
    }

    private fun DataSnapshot.getStringValue(fieldName: String): String? {
        val childSnap = child(fieldName)
        if (childSnap.exists()) {
            childSnap.getValue(String::class.java)?.let { return it }
            // Sometimes Firebase stores numbers but UI expects string (e.g., budgetNotes set to number)
            (childSnap.value as? Number)?.let { return it.toString() }
        }
        // Legacy structure might place the entire budget at this snapshot (root level)
        val mapValue = (value as? Map<*, *>)?.get(fieldName)
        return when (mapValue) {
            is String -> mapValue
            is Number -> mapValue.toString()
            else -> null
        }
    }

    private fun DataSnapshot.getDoubleValue(fieldName: String): Double? {
        val childSnap = child(fieldName)
        if (childSnap.exists()) {
            childSnap.getValue(Double::class.java)?.let { return it }
            childSnap.getValue(Long::class.java)?.let { return it.toDouble() }
            childSnap.getValue(Int::class.java)?.let { return it.toDouble() }
            childSnap.getValue(String::class.java)?.toDoubleOrNull()?.let { return it }
        }
        val mapValue = (value as? Map<*, *>)?.get(fieldName)
        return when (mapValue) {
            is Double -> mapValue
            is Float -> mapValue.toDouble()
            is Long -> mapValue.toDouble()
            is Int -> mapValue.toDouble()
            is String -> mapValue.toDoubleOrNull()
            else -> null
        }
    }

    private fun DataSnapshot.getIntValue(fieldName: String): Int? {
        val childSnap = child(fieldName)
        if (childSnap.exists()) {
            childSnap.getValue(Int::class.java)?.let { return it }
            childSnap.getValue(Long::class.java)?.let { return it.toInt() }
            childSnap.getValue(Double::class.java)?.let { return it.toInt() }
            childSnap.getValue(String::class.java)?.toDoubleOrNull()?.let { return it.toInt() }
        }
        val mapValue = (value as? Map<*, *>)?.get(fieldName)
        return when (mapValue) {
            is Number -> mapValue.toInt()
            is String -> mapValue.toDoubleOrNull()?.toInt()
            else -> null
        }
    }
    
    private fun Budget_Entity.toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "budgetName" to budgetName,
            "openingBalance" to openingBalance,
            "budgetMinGoal" to budgetMinGoal,
            "budgetMaxGoal" to budgetMaxGoal,
            "budgetBalance" to budgetBalance,
            "budgetNotes" to budgetNotes
        )
    }
}
