package com.example.spendsprout_opsc

import android.app.Application
import androidx.room.Room
import com.example.spendsprout_opsc.roomdb.BudgetDatabase
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BudgetApp : Application() {
    companion object {
        lateinit var db: BudgetDatabase
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Room (keep for gradual migration)
        db = Room.databaseBuilder(this, BudgetDatabase::class.java, "budget.db")
            .fallbackToDestructiveMigration()   // prototype speed
            .build()
    }
}
