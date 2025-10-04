package com.example.spendsprout_opsc

import android.app.Application

/**
 * SpendSproutApplication - Main Application Class
 * 
 * This is the entry point for the application.
 * For now, we'll keep it simple without Hilt dependency injection
 * to avoid build issues until the Java environment is properly set up.
 */
class SpendSproutApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // TODO: Initialize database and dependencies once build environment is fixed
        // This will be re-enabled when Hilt annotation processing works properly
    }
}
