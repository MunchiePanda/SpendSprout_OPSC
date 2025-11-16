package com.example.spendsprout_opsc.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collectLatest { transactions ->
                val categoryTotals = transactions.groupBy { it.categoryId }
                    .mapValues { (_, transactions) ->
                        transactions.sumOf { it.amount.toDouble() }
                    }

                _uiState.value = ReportsUiState(categoryTotals = categoryTotals)
            }
        }
    }
}

data class ReportsUiState(
    val categoryTotals: Map<String, Double> = emptyMap()
)
