package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.roomdb.Subcategory_DAO
import com.example.spendsprout_opsc.roomdb.Subcategory_Entity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubcategoryRepository @Inject constructor(
    private val subcategoryDao: Subcategory_DAO
) {
    fun getAllSubcategories(): Flow<List<Subcategory_Entity>> = subcategoryDao.getAll()
    
    suspend fun insertSubcategory(subcategory: Subcategory_Entity) = subcategoryDao.insert(subcategory)
    
    suspend fun updateSubcategory(subcategory: Subcategory_Entity) = subcategoryDao.update(subcategory)
    
    suspend fun deleteSubcategory(subcategory: Subcategory_Entity) = subcategoryDao.delete(subcategory)
    
    suspend fun getSubcategoryById(id: Int) = subcategoryDao.getById(id)
}
