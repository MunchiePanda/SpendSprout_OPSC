package com.example.spendsprout_opsc.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsViewModel(private val context: Context) {
    
    private val sharedPref: SharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    
    fun isDarkMode(): Boolean = sharedPref.getBoolean("DarkMode", false)
    
    fun setDarkMode(enabled: Boolean) {
        sharedPref.edit().putBoolean("DarkMode", enabled).apply()
    }
    
    fun isFingerprintEnabled(): Boolean = sharedPref.getBoolean("Fingerprint", false)
    
    fun setFingerprintEnabled(enabled: Boolean) {
        sharedPref.edit().putBoolean("Fingerprint", enabled).apply()
    }
    
    fun isNotificationsEnabled(): Boolean = sharedPref.getBoolean("Notifications", true)
    
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPref.edit().putBoolean("Notifications", enabled).apply()
    }
    
    fun getCurrency(): String = sharedPref.getString("Currency", "ZAR") ?: "ZAR"
    
    fun setCurrency(currency: String) {
        sharedPref.edit().putString("Currency", currency).apply()
    }
    
    fun getLanguage(): String = sharedPref.getString("Language", "English") ?: "English"
    
    fun setLanguage(language: String) {
        sharedPref.edit().putString("Language", language).apply()
    }
    
    // Goals functionality for submission requirements
    fun getMinMonthlyGoal(): Float = sharedPref.getFloat("MinMonthlyGoal", 0f)
    
    fun setMinMonthlyGoal(goal: Float) {
        sharedPref.edit().putFloat("MinMonthlyGoal", goal).apply()
    }
    
    fun getMaxMonthlyGoal(): Float = sharedPref.getFloat("MaxMonthlyGoal", 0f)
    
    fun setMaxMonthlyGoal(goal: Float) {
        sharedPref.edit().putFloat("MaxMonthlyGoal", goal).apply()
    }
}

