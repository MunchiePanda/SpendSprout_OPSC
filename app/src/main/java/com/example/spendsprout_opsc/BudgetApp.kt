package com.example.spendsprout_opsc

import android.app.Application
import androidx.room.Room
import com.example.spendsprout_opsc.roomdb.BudgetDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BudgetApp : Application() {
    companion object {
        lateinit var db: BudgetDatabase
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, BudgetDatabase::class.java, "budget.db")
            .fallbackToDestructiveMigration(true)   // prototype speed
            .build()
    }
}
