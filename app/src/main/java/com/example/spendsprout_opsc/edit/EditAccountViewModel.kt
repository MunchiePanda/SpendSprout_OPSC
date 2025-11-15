package com.example.spendsprout_opsc.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendsprout_opsc.model.Account
import com.example.spendsprout_opsc.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    fun saveAccount(accountName: String, accountBalance: Double, accountType: String) {
        viewModelScope.launch {
            val account = Account(
                accountName = accountName,
                accountBalance = accountBalance,
                accountType = accountType
            )
            accountRepository.addAccount(account)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.updateAccount(account)
        }
    }

    fun getAccount(accountId: String, callback: (Account?) -> Unit) {
        viewModelScope.launch {
            callback(accountRepository.getAccount(accountId))
        }
    }
}
