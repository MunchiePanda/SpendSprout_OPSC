package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.firebase.FirebaseConstants
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class SubcategoryRepository @Inject constructor(
    @Named("rootDatabaseReference") rootReference: DatabaseReference,
) {

    private val subcategoriesReference: DatabaseReference =
        rootReference
            .child(FirebaseConstants.USERS_NODE)
            .child(FirebaseConstants.DEFAULT_USER_ID)
            .child(FirebaseConstants.SUBCATEGORIES_NODE)

    fun getAllSubcategories(): Flow<List<Subcategory_Entity>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subcategories = snapshot.children.mapNotNull { child ->
                    child.getValue(Subcategory_Entity::class.java)?.apply {
                        if (id == 0) {
                            id = child.key?.toIntOrNull() ?: id
                        }
                    }
                }
                trySend(subcategories).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        subcategoriesReference.addValueEventListener(listener)
        awaitClose { subcategoriesReference.removeEventListener(listener) }
    }

    suspend fun getSubcategoryById(id: Int): Subcategory_Entity? {
        val snapshot = subcategoriesReference.child(id.toString()).get().await()
        return snapshot.getValue(Subcategory_Entity::class.java)?.apply { this.id = id }
    }

    suspend fun insertSubcategory(subcategory: Subcategory_Entity) {
        val subcategoryId = if (subcategory.id != 0) subcategory.id else generateNextSubcategoryId()
        subcategoriesReference
            .child(subcategoryId.toString())
            .setValue(subcategory.copy(id = subcategoryId))
            .await()
    }

    suspend fun updateSubcategory(subcategory: Subcategory_Entity) {
        if (subcategory.id == 0) return
        subcategoriesReference
            .child(subcategory.id.toString())
            .setValue(subcategory)
            .await()
    }

    suspend fun deleteSubcategory(subcategory: Subcategory_Entity) {
        if (subcategory.id == 0) return
        subcategoriesReference
            .child(subcategory.id.toString())
            .removeValue()
            .await()
    }

    private suspend fun generateNextSubcategoryId(): Int {
        val snapshot = subcategoriesReference.get().await()
        val maxId = snapshot.children
            .mapNotNull { child -> child.key?.toIntOrNull() }
            .maxOrNull() ?: 0
        return maxId + 1
    }
}
