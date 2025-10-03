package com.example.spendsprout_opsc.overview.model

/**
 * CategorySummary - Data Model for Category Summary Display
 * 
 * This is like Unity's ScriptableObject or data structure for category information.
 * Similar to Unity's data classes used for UI display.
 * 
 * Responsibilities:
 * - Store category summary data (like Unity's data containers)
 * - Provide category information for UI display (like Unity's UI data binding)
 * - Handle category color and amounts (like Unity's visual data)
 */
data class CategorySummary(
    val name: String,
    val spent: String,
    val allocated: String,
    val color: String
)
