package com.example.spendsprout_opsc.reports

import com.example.spendsprout_opsc.overview.model.ChartDataPoint

class ReportsViewModel {
    
    fun getTotalSpent(): Double = 6000.0
    
    fun getTotalBudget(): Double = 8000.0
    
    fun getProgressPercentage(): Int {
        return ((getTotalSpent() / getTotalBudget()) * 100).toInt()
    }
    
    fun getChartData(): List<ChartDataPoint> {
        return listOf(
            ChartDataPoint("2010/01", 15000.0, 12000.0),
            ChartDataPoint("2010/02", 18000.0, 15000.0),
            ChartDataPoint("2010/03", 20000.0, 18000.0),
            ChartDataPoint("2010/04", 22000.0, 20000.0),
            ChartDataPoint("2010/05", 25000.0, 22000.0),
            ChartDataPoint("2010/06", 28000.0, 25000.0),
            ChartDataPoint("2010/07", 30000.0, 28000.0),
            ChartDataPoint("2010/08", 32000.0, 30000.0),
            ChartDataPoint("2010/09", 35000.0, 32000.0),
            ChartDataPoint("2010/10", 38000.0, 35000.0),
            ChartDataPoint("2010/11", 40000.0, 38000.0),
            ChartDataPoint("2010/12", 42000.0, 40000.0),
            ChartDataPoint("2011/01", 45000.0, 42000.0),
            ChartDataPoint("2011/02", 48000.0, 45000.0),
            ChartDataPoint("2011/03", 50000.0, 48000.0),
            ChartDataPoint("2011/04", 52000.0, 50000.0),
            ChartDataPoint("2011/05", 55000.0, 52000.0),
            ChartDataPoint("2011/06", 58000.0, 55000.0),
            ChartDataPoint("2011/07", 60000.0, 58000.0),
            ChartDataPoint("2011/08", 62000.0, 60000.0),
            ChartDataPoint("2011/09", 65000.0, 62000.0),
            ChartDataPoint("2011/10", 68000.0, 65000.0),
            ChartDataPoint("2011/11", 70000.0, 68000.0),
            ChartDataPoint("2011/12", 72000.0, 70000.0),
            ChartDataPoint("2012/01", 75000.0, 72000.0),
            ChartDataPoint("2012/02", 78000.0, 75000.0),
            ChartDataPoint("2012/03", 80000.0, 78000.0),
            ChartDataPoint("2012/04", 82000.0, 80000.0),
            ChartDataPoint("2012/05", 85000.0, 82000.0),
            ChartDataPoint("2012/06", 88000.0, 85000.0),
            ChartDataPoint("2012/07", 90000.0, 88000.0),
            ChartDataPoint("2012/08", 92000.0, 90000.0),
            ChartDataPoint("2012/09", 95000.0, 92000.0),
            ChartDataPoint("2012/10", 98000.0, 95000.0),
            ChartDataPoint("2012/11", 100000.0, 98000.0)
        )
    }
}

