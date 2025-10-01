package com.example.spendsprout_opsc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {
    private val repository = Repository

    // LiveData for UI
    private val _totalBalance = MutableLiveData<Double>(12780.0)
    val totalBalance: LiveData<Double> = _totalBalance

    private val _recentTransactions = MutableLiveData<List<Repository.Transaction>>()
    val recentTransactions: LiveData<List<Repository.Transaction>> = _recentTransactions

    private val _categorySummaries = MutableLiveData<Map<String, Repository.Category>>()
    val categorySummaries: LiveData<Map<String, Repository.Category>> = _categorySummaries

    private val _accountSummaries = MutableLiveData<List<Repository.Account>>()
    val accountSummaries: LiveData<List<Repository.Account>> = _accountSummaries

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load recent transactions (last 3)
            repository.transactions.observeForever { transactions ->
                _recentTransactions.value = transactions.takeLast(3).reversed()
            }

            // Load categories
            repository.categories.observeForever { categories ->
                _categorySummaries.value = categories
            }

            // Load accounts
            repository.accounts.observeForever { accounts ->
                _accountSummaries.value = accounts
            }
        }
    }
}
