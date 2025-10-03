package com.example.spendsprout_opsc.edit

class EditCategoryViewModel {
    
    fun saveCategory(name: String, type: String, allocatedBudget: Double, color: String, notes: String) {
        // Save category logic - for now just validate
        require(name.isNotBlank()) { "Category name is required" }
        require(allocatedBudget > 0) { "Allocated budget must be greater than 0" }
        
        // In a real app, this would save to database
        // For now, we'll just validate the data
    }
}

