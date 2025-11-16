package com.example.spendsprout_opsc.reports

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.navigation.NavigationView
import android.graphics.Color
import android.widget.TextView
import com.example.spendsprout_opsc.BudgetApp
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReportsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var reportsViewModel: ReportsViewModel
    private lateinit var progressBarSpending: ProgressBar

    private lateinit var budgetLineChart: LineChart
    private lateinit var categoryPieChart: PieChart
    private lateinit var subcategoryPieChart: PieChart

    private var startDate: Long? = null
    private var endDate: Long? = null
    private lateinit var btnSelectDateRange: com.google.android.material.button.MaterialButton
    private lateinit var txtDateRange: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        //progressBarSpending = findViewById(R.id.progressBar_Spending)

        // Set up the toolbar from the included header bar
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Reports"
        
        // Set up menu button click listener
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        reportsViewModel = ReportsViewModel()

        setupUI()
        observeData()
    }

    private fun setupUI() {
        //setupMonthSpinner()
        //setupExportButton()
        setupDateRangePicker()
        setupChart()
        setupLineChart()
        updateProgressBar()
    }

    override fun onResume() {
        super.onResume()
        // Refresh chart and progress in case data changed
        setupChart()
        updateProgressBar()
    }

    /*
    private fun setupMonthSpinner() {
        val spinnerMonth = findViewById<Spinner>(R.id.spinner_Month)
        val months = arrayOf("January 2025", "February 2025", "March 2025", "April 2025", "May 2025", "June 2025")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = adapter
    }

    private fun setupExportButton() {
        val btnExport = findViewById<Button>(R.id.btn_Export)
        btnExport.setOnClickListener {
            Toast.makeText(this, "Report exported", Toast.LENGTH_SHORT).show()
        }
    }
     */

    private fun setupLineChart(){
        budgetLineChart = findViewById<LineChart>(R.id.lineChart_BudgetOverview)
        budgetLineChart.description.isEnabled = false
        budgetLineChart.setTouchEnabled(true)
        budgetLineChart.isDragEnabled = true
        budgetLineChart.setScaleEnabled(true)
        budgetLineChart.setPinchZoom(true)
        budgetLineChart.legend.isEnabled = true

        val xAxis = budgetLineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                val millis = value.toLong()
                return dateFormat.format(Date(millis))
            }
        }

        budgetLineChart.axisRight.isEnabled = false
        budgetLineChart.axisLeft.setDrawGridLines(true)

        loadAndRenderLineChartData()
    }

    private fun setupDateRangePicker() {
        val btn = findViewById<MaterialButton>(R.id.btn_selectDateRange)
        val txt = findViewById<TextView>(R.id.txt_dateRange)
        // late init proxies
        btnSelectDateRange = btn
        txtDateRange = txt

        updateDateRangeDisplay()

        btnSelectDateRange.setOnClickListener { showDateRangePicker() }
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .setSelection(
                androidx.core.util.Pair(
                    startDate ?: MaterialDatePicker.todayInUtcMilliseconds(),
                    endDate ?: MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        dateRangePicker.show(supportFragmentManager, "REPORTS_DATE_RANGE_PICKER")
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            startDate = selection.first
            endDate = selection.second
            updateDateRangeDisplay()
            loadAndRenderLineChartData()
        }
    }

    private fun updateDateRangeDisplay() {
        if (!::txtDateRange.isInitialized) return
        if (startDate != null && endDate != null) {
            val days = ((endDate!! - startDate!!) / (1000 * 60 * 60 * 24)).toInt()
            txtDateRange.text = when {
                days == 0 -> "Today"
                days == 1 -> "Yesterday"
                days < 7 -> "Last $days days"
                days < 30 -> "Last ${days / 7} weeks"
                days < 365 -> "Last ${days / 30} months"
                else -> "Last ${days / 365} years"
            }
        } else {
            txtDateRange.text = "All Time"
        }
    }

    private fun loadAndRenderLineChartData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (effectiveStart, effectiveEnd) = resolveEffectiveDateRange()

                val expenses = BudgetApp.db.expenseDao().getBetweenDates(effectiveStart, effectiveEnd)

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val totalsByDay = expenses.groupBy { sdf.format(Date(it.expenseDate)) }
                    .mapValues { (_, list) ->
                        list.sumOf { entity ->
                            if (entity.expenseType == com.example.spendsprout_opsc.ExpenseType.Expense) {
                                entity.expenseAmount
                            } else {
                                -entity.expenseAmount
                            }
                        }
                    }

                val dailyEntries = mutableListOf<Entry>()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = effectiveStart
                while (calendar.timeInMillis <= effectiveEnd) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val key = sdf.format(Date(calendar.timeInMillis))
                    val amount = totalsByDay[key]?.toFloat() ?: 0f
                    dailyEntries.add(Entry(calendar.timeInMillis.toFloat(), amount))
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
                val minBudget = prefs.getFloat("MinMonthlyGoal", 0f).toFloat()
                val maxBudget = prefs.getFloat("MaxMonthlyGoal", 0f).toFloat()

                val minLineEntries = listOf(
                    Entry(effectiveStart.toFloat(), minBudget),
                    Entry(effectiveEnd.toFloat(), minBudget)
                )
                val maxLineEntries = listOf(
                    Entry(effectiveStart.toFloat(), maxBudget),
                    Entry(effectiveEnd.toFloat(), maxBudget)
                )

                val transactionsDataSet = LineDataSet(dailyEntries, "Transactions").apply {
                    color = Color.parseColor("#2E7D32")
                    setDrawCircles(false)
                    lineWidth = 2f
                    setDrawValues(false)
                }

                val minBudgetDataSet = LineDataSet(minLineEntries, "Min Budget").apply {
                    color = Color.RED
                    setDrawCircles(false)
                    lineWidth = 1.5f
                    setDrawValues(false)
                }

                val maxBudgetDataSet = LineDataSet(maxLineEntries, "Max Budget").apply {
                    color = Color.RED
                    setDrawCircles(false)
                    lineWidth = 1.5f
                    setDrawValues(false)
                }

                val lineData = LineData(transactionsDataSet, minBudgetDataSet, maxBudgetDataSet)

                CoroutineScope(Dispatchers.Main).launch {
                    budgetLineChart.data = lineData
                    budgetLineChart.invalidate()
                }
            } catch (_: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@ReportsActivity, "Failed to load chart data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resolveEffectiveDateRange(): Pair<Long, Long> {
        var s = startDate
        var e = endDate
        if (s == null || e == null) {
            val all = BudgetApp.db.expenseDao().getAll()
            if (all.isNotEmpty()) {
                s = all.minOf { it.expenseDate }
                e = all.maxOf { it.expenseDate }
            } else {
                s = reportsViewModel.getStartOfMonth()
                e = reportsViewModel.getEndOfMonth()
            }
        }
        return Pair(s!!, e!!)
    }
    private fun setupChart() {
        val chartView = findViewById<com.example.spendsprout_opsc.overview.ChartView>(R.id.chartView)
        // Load daily spend for current month and use max goal as target
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val monthlyTarget = prefs.getFloat("MaxMonthlyGoal", 0f).toDouble()
        reportsViewModel.loadDailySpendSeries(
            reportsViewModel.getStartOfMonth(),
            reportsViewModel.getEndOfMonth(),
            monthlyTarget
        ) { points ->
            chartView.setData(points)
        }
    }

    private fun updateProgressBar() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val maxGoal = prefs.getFloat("MaxMonthlyGoal", 0f).toDouble()
        reportsViewModel.loadMonthlySpent(
            reportsViewModel.getStartOfMonth(),
            reportsViewModel.getEndOfMonth()
        ) { totalSpent ->
            val percent = if (maxGoal > 0) ((totalSpent / maxGoal) * 100).toInt().coerceIn(0, 100) else 0
            progressBarSpending.progress = percent
        }
    }

    private fun observeData() {
        // Observe ViewModel data changes
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                startActivity(Intent(this, com.example.spendsprout_opsc.CategoryOverviewActivity::class.java))
            }
            R.id.nav_transactions -> {
                startActivity(Intent(this, TransactionsActivity::class.java))
            }
            R.id.nav_accounts -> {
                startActivity(Intent(this, AccountsActivity::class.java))
            }
            R.id.nav_reports -> {
                // Already in Reports, do nothing
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_exit -> {
                finishAffinity()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle back button - finish this activity
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

