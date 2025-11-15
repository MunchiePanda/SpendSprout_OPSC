package com.example.spendsprout_opsc.di

import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.BudgetRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.FirebaseAccountRepository
import com.example.spendsprout_opsc.repository.FirebaseBudgetRepository
import com.example.spendsprout_opsc.repository.FirebaseCategoryRepository
import com.example.spendsprout_opsc.repository.FirebaseTransactionRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance("https://spendsprout-49aaa-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: FirebaseAccountRepository): AccountRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: FirebaseBudgetRepository): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: FirebaseCategoryRepository): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: FirebaseTransactionRepository): TransactionRepository
}
