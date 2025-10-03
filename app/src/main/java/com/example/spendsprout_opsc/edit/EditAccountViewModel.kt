package com.example.spendsprout_opsc.edit

class EditAccountViewModel {
    
    fun saveAccount(name: String, type: String, balance: Double, notes: String) {
        // Save account logic - for now just validate
        require(name.isNotBlank()) { "Account name is required" }
        
        // In a real app, this would save to database
        // For now, we'll just validate the data
    }
}

