package com.example.spendsprout_opsc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WantsCategoryViewModel : ViewModel() {
    private val repository = Repository

    private val _category = MutableLiveData<Repository.Category>()
    val category: LiveData<Repository.Category> = _category

    fun loadCategory(categoryName: String) {
        viewModelScope.launch {
            repository.categories.observeForever { categories ->
                _category.value = categories[categoryName]
            }
        }
    }
}
