package com.example.spendsprout_opsc.categories

object SubcategorySuggestions {
    
    val needsSuggestions = listOf(
        "Housing/Rent",
        "Utilities",
        "Groceries",
        "Transportation",
        "Insurance",
        "Healthcare",
        "Debt Payments",
        "Education",
        "Clothing (Essential)",
        "Phone/Internet",
        "Childcare",
        "Basic Personal Care"
    )
    
    val wantsSuggestions = listOf(
        "Entertainment",
        "Dining Out",
        "Shopping",
        "Hobbies",
        "Travel/Vacation",
        "Subscriptions",
        "Gifts",
        "Clothing (Non-Essential)",
        "Electronics",
        "Home Decor",
        "Sports/Recreation",
        "Personal Care (Non-Essential)"
    )
    
    val savingsSuggestions = listOf(
        "Emergency Fund",
        "Retirement",
        "Investments",
        "Savings Goal 1",
        "Savings Goal 2",
        "Education Fund",
        "Vacation Fund",
        "Home Down Payment",
        "Car Fund",
        "General Savings"
    )
    
    fun getSuggestionsForCategory(categoryName: String): List<String> {
        return when (categoryName.lowercase()) {
            "needs" -> needsSuggestions
            "wants" -> wantsSuggestions
            "savings" -> savingsSuggestions
            else -> emptyList()
        }
    }
}

