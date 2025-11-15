/*
package com.example.spendsprout_opsc.di

import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.repository.SubcategoryRepository
import com.example.spendsprout_opsc.repository.TransactionRepository
import com.example.spendsprout_opsc.service.DataService
import org.koin.dsl.module

val dataFlowModule = module {
    single { AccountRepository(get()) }
    single { CategoryRepository(get()) }
    single { SubcategoryRepository(get()) }
    single { TransactionRepository(get()) }
    single { DataService(get(), get(), get(), get()) }
}
*/