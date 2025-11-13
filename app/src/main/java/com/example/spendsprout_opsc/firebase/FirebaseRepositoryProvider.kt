package com.example.spendsprout_opsc.firebase

import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.SubcategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseRepositoryProvider {

    private val rootReference: DatabaseReference
        get() = FirebaseDatabase.getInstance().reference

    val accountRepository: AccountRepository by lazy {
        AccountRepository(rootReference = rootReference)
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(rootReference = rootReference)
    }

    val subcategoryRepository: SubcategoryRepository by lazy {
        SubcategoryRepository(rootReference = rootReference)
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(rootReference = rootReference)
    }
}


