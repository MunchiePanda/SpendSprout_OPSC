package com.example.spendsprout_opsc

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BudgetApp : Application() {

    companion object {
        val db: com.example.spendsprout_opsc.roomdb.BudgetDatabase
            get() = throw UnsupportedOperationException(
                "Room database has been removed. Use Firebase repositories instead."
            )
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        com.google.firebase.database.FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
