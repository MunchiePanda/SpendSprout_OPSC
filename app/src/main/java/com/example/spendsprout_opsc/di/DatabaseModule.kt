package com.example.spendsprout_opsc.di

import android.content.Context
import androidx.room.Room
import com.example.spendsprout_opsc.database.DatabaseMigration
import com.example.spendsprout_opsc.roomdb.Account_DAO
import com.example.spendsprout_opsc.roomdb.BudgetDatabase
import com.example.spendsprout_opsc.roomdb.Category_DAO
import com.example.spendsprout_opsc.roomdb.Contact_DAO
import com.example.spendsprout_opsc.roomdb.Expense_DAO
import com.example.spendsprout_opsc.roomdb.Subcategory_DAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBudgetDatabase(@ApplicationContext context: Context): BudgetDatabase {
        return Room.databaseBuilder(
            context,
            BudgetDatabase::class.java,
            "budget_database"
        )
                .addMigrations(*DatabaseMigration.ALL_MIGRATIONS)
                .fallbackToDestructiveMigration(true)
        .build()
    }

    @Provides
    fun provideAccountDao(database: BudgetDatabase): Account_DAO = database.accountDao()

    @Provides
    fun provideCategoryDao(database: BudgetDatabase): Category_DAO = database.categoryDao()

    @Provides
    fun provideSubcategoryDao(database: BudgetDatabase): Subcategory_DAO = database.subcategoryDao()

    @Provides
    fun provideExpenseDao(database: BudgetDatabase): Expense_DAO = database.expenseDao()

    @Provides
    fun provideContactDao(database: BudgetDatabase): Contact_DAO = database.contactDao()
}
