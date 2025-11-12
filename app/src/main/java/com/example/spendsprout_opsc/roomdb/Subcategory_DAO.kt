package com.example.spendsprout_opsc.roomdb

interface Subcategory_DAO {
    fun insertAll(vararg subcategories: Subcategory_Entity)
    suspend fun insert(subcategory: Subcategory_Entity)
    suspend fun update(subcategory: Subcategory_Entity)
    fun delete(subcategory: Subcategory_Entity)
    fun getAll(): List<Subcategory_Entity>
    fun loadAllByIds(subcategoryIds: List<Int>): List<Subcategory_Entity>
    fun loadAllByNames(subcategoryNames: List<String>): List<Subcategory_Entity>
    suspend fun getById(subcategoryId: Int): Subcategory_Entity?
    suspend fun getByCategoryId(categoryId: Long): List<Subcategory_Entity>
}
