package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.roomdb.Category_DAO
import com.example.spendsprout_opsc.roomdb.Category_Entity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: Category_DAO
) {
    fun getAllCategories(): Flow<List<Category_Entity>> = categoryDao.getAll()
    
    suspend fun insertCategory(category: Category_Entity) = categoryDao.insert(category)
    
    suspend fun updateCategory(category: Category_Entity) = categoryDao.update(category)
    
    suspend fun deleteCategory(category: Category_Entity) = categoryDao.delete(category)
    
    suspend fun getCategoryById(id: Int) = categoryDao.getById(id)
    
    suspend fun getTotalAllocation() = categoryDao.getTotalAllocation() ?: 0.0
    
    suspend fun getTotalSpent() = categoryDao.getTotalSpent() ?: 0.0
}
