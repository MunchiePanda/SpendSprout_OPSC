package com.example.spendsprout_opsc

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class CategoryOverviewActivity : AppCompatActivity() {
    
    private lateinit var btnSelectDateRange: MaterialButton
    private lateinit var txtDateRange: TextView
    private var startDate: Long? = null
    private var endDate: Long? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_overview)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize date range picker
        setupDateRangePicker()
    }
    
    private fun setupDateRangePicker() {
        btnSelectDateRange = findViewById(R.id.btn_selectDateRange)
        txtDateRange = findViewById(R.id.txt_dateRange)
        
        // Set default date range (last 30 days)
        val calendar = Calendar.getInstance()
        endDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        startDate = calendar.timeInMillis
        
        updateDateRangeDisplay()
        
        btnSelectDateRange.setOnClickListener {
            showDateRangePicker()
        }
    }
    
    private fun showDateRangePicker() {
        // Create date range picker
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .setSelection(
                androidx.core.util.Pair(
                    startDate ?: MaterialDatePicker.todayInUtcMilliseconds(),
                    endDate ?: MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()
        
        // Show the picker
        dateRangePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")
        
        // Handle the selection
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            startDate = selection.first
            endDate = selection.second
            
            // Format dates for display
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val startDateStr = dateFormat.format(Date(startDate!!))
            val endDateStr = dateFormat.format(Date(endDate!!))
            
            // Update display
            updateDateRangeDisplay()
            
            // Filter categories based on date range
            filterCategoriesByDateRange()
        }
    }
    
    private fun updateDateRangeDisplay() {
        if (startDate != null && endDate != null) {
            val dateFormat = SimpleDateFormat("MMM dd - MMM dd, yyyy", Locale.getDefault())
            val startDateStr = dateFormat.format(Date(startDate!!))
            val endDateStr = dateFormat.format(Date(endDate!!))
            
            // Calculate days difference for a more user-friendly display
            val daysDifference = ((endDate!! - startDate!!) / (1000 * 60 * 60 * 24)).toInt()
            
            when {
                daysDifference == 0 -> txtDateRange.text = "Today"
                daysDifference == 1 -> txtDateRange.text = "Yesterday"
                daysDifference < 7 -> txtDateRange.text = "Last $daysDifference days"
                daysDifference < 30 -> txtDateRange.text = "Last ${daysDifference / 7} weeks"
                daysDifference < 365 -> txtDateRange.text = "Last ${daysDifference / 30} months"
                else -> txtDateRange.text = "Last ${daysDifference / 365} years"
            }
        } else {
            txtDateRange.text = "Select date range"
        }
    }
    
    private fun filterCategoriesByDateRange() {
        // TODO: Implement category filtering based on selected date range
        // This would integrate with your category data source/ViewModel
        // For example:
        // categoryViewModel.filterCategoriesByDateRange(startDate!!, endDate!!)
        
        // For now, just log the selected dates
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateStr = dateFormat.format(Date(startDate!!))
        val endDateStr = dateFormat.format(Date(endDate!!))
        
        println("Filtering categories from $startDateStr to $endDateStr")
    }
}