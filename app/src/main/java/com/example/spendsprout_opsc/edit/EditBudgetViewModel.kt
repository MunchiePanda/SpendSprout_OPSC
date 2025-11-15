package com.example.spendsprout_opsc.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Budget
import com.example.spendsprout_opsc.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    fun saveBudget(budgetName: String, budgetAmount: Double) {
        viewModelScope.launch {
            val budget = Budget(
                budgetName = budgetName,
                budgetAmount = budgetAmount
            )
            budgetRepository.addBudget(budget)
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.updateBudget(budget)
        }
    }

    fun getBudget(budgetId: String, callback: (Budget?) -> Unit) {
        viewModelScope.launch {
            callback(budgetRepository.getBudget(budgetId))
        }
    }
}
