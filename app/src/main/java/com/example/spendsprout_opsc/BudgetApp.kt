package com.example.spendsprout_opsc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// This annotation is all that's needed. Hilt will handle the rest.
@HiltAndroidApp
class BudgetApp : Application() {
    // The onCreate logic for Room is no longer needed.
    // The Companion Object for the database is also no longer needed.
}
