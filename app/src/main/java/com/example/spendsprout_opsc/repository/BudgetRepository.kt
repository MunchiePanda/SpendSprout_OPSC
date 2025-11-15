package com.example.spendsprout_opsc.repository

import com.example.spendsprout_opsc.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun getBudget(budgetId: String): Budget?
    suspend fun addBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budgetId: String)
}
