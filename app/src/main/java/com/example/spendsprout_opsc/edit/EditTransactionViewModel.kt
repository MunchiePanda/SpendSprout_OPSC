package com.example.spendsprout_opsc.edit

class EditTransactionViewModel {
    
    fun saveTransaction(
        description: String,
        amount: Double,
        category: String,
        date: String,
        account: String,
        repeat: String,
        oweOwed: Boolean,
        notes: String
    ) {
        // Save transaction logic - for now just validate
        require(description.isNotBlank()) { "Description is required" }
        require(amount > 0) { "Amount must be greater than 0" }
        require(category.isNotBlank()) { "Category is required" }
        
        // In a real app, this would save to database
        // For now, we'll just validate the data
    }
}

