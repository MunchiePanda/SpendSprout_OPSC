package com.example.spendsprout_opsc.overview

import com.example.spendsprout_opsc.overview.model.Transaction
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import com.example.spendsprout_opsc.overview.model.CategorySummary
import com.example.spendsprout_opsc.overview.model.AccountSummary

/**
 * OverviewViewModel - Data Management Script
 * 
 * This is like Unity's MonoBehaviour script that manages game state and data.
 * Similar to Unity's GameManager or DataManager scripts.
 * 
 * Responsibilities:
 * - Manage transaction data (like Unity's player inventory or score system)
 * - Handle chart data for visualization (like Unity's analytics data)
 * - Provide reactive data updates (like Unity's Events or UnityEvents)
 * - Calculate total balance (like Unity's score calculation)
 */
class OverviewViewModel {
    
    fun getTotalBalance(): Double = 12780.0
    
    fun getRecentTransactions(): List<Transaction> {
        return listOf(
            Transaction("10 August 2025", "Petrol", "- R 1,500", "#FF6B6B"),
            Transaction("08 August 2025", "Mug 'n Bean", "- R 360", "#FFB6C1"),
            Transaction("25 July 2025", "Salary", "+ R 20,000", "#9370DB")
        )
    }
    
    fun getCategorySummary(): List<CategorySummary> {
        return listOf(
            CategorySummary("Needs", "R 8,900", "R 10,000", "#BD804A"),
            CategorySummary("Wants", "- R 120", "R 6,000", "#88618E"),
            CategorySummary("Savings", "R 4,000", "R 4,000", "#6EA19E")
        )
    }
    
    fun getAccountSummary(): List<AccountSummary> {
        return listOf(
            AccountSummary("Cash", "R 160", "R 19,00"),
            AccountSummary("FNB Next Transact", "R 1,720", "R 15,000")
        )
    }
    
    fun getAccountBalances(): List<AccountSummary> {
        return getAccountSummary()
    }
    
    fun getChartData(): List<ChartDataPoint> {
        return listOf(
            ChartDataPoint("2025-01", 15000.0, 12000.0),
            ChartDataPoint("2025-02", 18000.0, 15000.0),
            ChartDataPoint("2025-03", 20000.0, 18000.0),
            ChartDataPoint("2025-04", 22000.0, 20000.0),
            ChartDataPoint("2025-05", 25000.0, 22000.0),
            ChartDataPoint("2025-06", 28000.0, 25000.0)
        )
    }
}


