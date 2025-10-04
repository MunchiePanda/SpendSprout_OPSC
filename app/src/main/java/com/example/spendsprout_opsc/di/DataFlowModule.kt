package com.example.spendsprout_opsc.di

import com.example.spendsprout_opsc.manager.DataFlowManager
import com.example.spendsprout_opsc.service.DataService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DataFlowModule - Essential Data Flow Configuration
 * 
 * This module provides the essential data flow connections between
 * Room database and application logic components.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataFlowModule {

    @Provides
    @Singleton
    fun provideDataService(
        accountRepository: com.example.spendsprout_opsc.repository.AccountRepository,
        categoryRepository: com.example.spendsprout_opsc.repository.CategoryRepository,
        subcategoryRepository: com.example.spendsprout_opsc.repository.SubcategoryRepository,
        transactionRepository: com.example.spendsprout_opsc.repository.TransactionRepository
    ): DataService {
        return DataService(
            accountRepository = accountRepository,
            categoryRepository = categoryRepository,
            subcategoryRepository = subcategoryRepository,
            transactionRepository = transactionRepository
        )
    }

    @Provides
    @Singleton
    fun provideDataFlowManager(dataService: DataService): DataFlowManager {
        return DataFlowManager(dataService)
    }
}
