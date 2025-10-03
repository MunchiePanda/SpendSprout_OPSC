package com.example.spendsprout_opsc.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.example.spendsprout_opsc.wants.WantsCategoryActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class CategoriesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var categoryAdapter: HierarchicalCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the drawer indicator in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Categories"

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        categoriesViewModel = CategoriesViewModel()

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupCategoryRecyclerView()
        setupFab()
        setupFilters()
    }

    private fun setupCategoryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categories = categoriesViewModel.getAllCategories()
        categoryAdapter = HierarchicalCategoryAdapter(categories) { category ->
            // Category items are not clickable - they just display information
            // Only the FAB should navigate to edit screen for adding new categories
        }
        recyclerView.adapter = categoryAdapter
    }

    private fun setupFab() {
        val fabAddCategory = findViewById<FloatingActionButton>(R.id.fab_AddCategory)
        fabAddCategory.setOnClickListener {
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFilters() {
        // Find the filter button (it's in the layout as a LinearLayout)
        val filterContainer = findViewById<android.widget.LinearLayout>(R.id.filter_container)
        filterContainer?.setOnClickListener {
            showFilterDialog()
        }
    }


    private fun observeData() {
        // Observe ViewModel data changes
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1001, 0, "Filters")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1001) {
            showFilterDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        val types = arrayOf("All", "Needs", "Wants", "Savings")
        AlertDialog.Builder(this)
            .setTitle("Filter by Type")
            .setItems(types) { _, which ->
                val selected = types[which]
                applyFilter(selected)
            }
            .show()
    }

    private fun applyFilter(type: String) {
        val filteredCategories = when (type) {
            "All" -> categoriesViewModel.getAllCategories()
            else -> categoriesViewModel.getFilteredCategories(type)
        }
        
        // Create new adapter with filtered data
        categoryAdapter = HierarchicalCategoryAdapter(filteredCategories) { category ->
            if (category.subcategories.isNotEmpty()) {
                android.widget.Toast.makeText(this, "Clicked ${category.name}", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
                intent.putExtra("categoryName", category.name)
                startActivity(intent)
            }
        }
        findViewById<RecyclerView>(R.id.recyclerView_Categories).adapter = categoryAdapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                // Already in Categories, do nothing
            }
            R.id.nav_transactions -> {
                startActivity(Intent(this, TransactionsActivity::class.java))
            }
            R.id.nav_accounts -> {
                startActivity(Intent(this, AccountsActivity::class.java))
            }
            R.id.nav_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

