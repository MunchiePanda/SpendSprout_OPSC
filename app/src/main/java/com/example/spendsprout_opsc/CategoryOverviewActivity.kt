package com.example.spendsprout_opsc

import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.categories.HierarchicalCategoryAdapter
import com.example.spendsprout_opsc.categories.CategoryViewModel
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.categories.model.Subcategory
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class CategoryOverviewActivity : AppCompatActivity() {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btnSelectDateRange: MaterialButton
    private lateinit var txtDateRange: TextView
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var hierarchicalCategoryAdapter: HierarchicalCategoryAdapter
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
        
        // Set up drawer + toolbar
        setupDrawer()

        // Initialize ViewModel
        categoryViewModel = CategoryViewModel()
        
        // Initialize date range picker
        setupDateRangePicker()
        
        // Setup UI
        setupCategoryRecyclerView()
        setupFab()
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.main)
        navView = findViewById(R.id.navigationView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Categories"

        // Set up menu button click listener
        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> startActivity(Intent(this, com.example.spendsprout_opsc.overview.OverviewActivity::class.java))
                R.id.nav_categories -> { /* already here */ }
                R.id.nav_transactions -> startActivity(Intent(this, com.example.spendsprout_opsc.transactions.TransactionsActivity::class.java))
                R.id.nav_accounts -> startActivity(Intent(this, com.example.spendsprout_opsc.accounts.AccountsActivity::class.java))
                R.id.nav_reports -> Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> startActivity(Intent(this, com.example.spendsprout_opsc.settings.SettingsActivity::class.java))
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
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
    
    private fun setupCategoryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize with empty list, will be populated from database
        hierarchicalCategoryAdapter = HierarchicalCategoryAdapter(emptyList(), 
            onCategoryClick = { category ->
                // Navigate to activity_categories (CategoriesActivity) for selected main category (Needs, Wants, Savings)
                val intent = Intent(this, com.example.spendsprout_opsc.categories.CategoriesActivity::class.java)
                intent.putExtra("filterByCategory", category.name) // Filter to show only this category's subcategories
                startActivity(intent)
            },
            onSubcategoryClick = { subcategory ->
                // Navigate to edit screen for selected subcategory with prefilled data
                val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
                intent.putExtra("subcategoryId", subcategory.id)
                intent.putExtra("subcategoryName", subcategory.name)
                intent.putExtra("isEditMode", true)
                startActivity(intent)
            }
        )
        recyclerView.adapter = hierarchicalCategoryAdapter

        // Load categories with subcategories from database
        loadCategoriesWithSubcategoriesFromDatabase()
    }
    
    private fun setupFab() {
        val fabAddCategory = findViewById<FloatingActionButton>(R.id.fab_AddCategory)
        fabAddCategory.setOnClickListener {
            // Navigate directly to edit categories screen to add new category/subcategory
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun loadCategoriesWithSubcategoriesFromDatabase() {
        categoryViewModel.loadCategoriesWithSubcategoriesFromDatabase { categoriesWithSubcategories ->
            hierarchicalCategoryAdapter.updateData(categoriesWithSubcategories)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reload categories when returning to this activity
        loadCategoriesWithSubcategoriesFromDatabase()
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
        // Reload categories with the new date range
        loadCategoriesWithSubcategoriesFromDatabase()
        
        // Log the selected dates for debugging
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateStr = dateFormat.format(Date(startDate!!))
        val endDateStr = dateFormat.format(Date(endDate!!))
        
        println("Filtering categories from $startDateStr to $endDateStr")
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