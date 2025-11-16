package com.example.spendsprout_opsc.di

import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.BudgetRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.FirebaseAccountRepository
import com.example.spendsprout_opsc.repository.FirebaseBudgetRepository
import com.example.spendsprout_opsc.repository.FirebaseCategoryRepository
import com.example.spendsprout_opsc.repository.FirebaseTransactionRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAccountRepository(impl: FirebaseAccountRepository): AccountRepository

    @Binds
    abstract fun bindCategoryRepository(impl: FirebaseCategoryRepository): CategoryRepository

    @Binds
    abstract fun bindTransactionRepository(impl: FirebaseTransactionRepository): TransactionRepository

    @Binds
    abstract fun bindBudgetRepository(impl: FirebaseBudgetRepository): BudgetRepository
}
