package com.example.spendsprout_opsc.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Subcategory
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.edit.EditCategoryActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class CategoriesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val categoriesViewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subcategoryAdapter: SubcategoryAdapter
    private var filterByCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Categories"

        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)

        filterByCategory = intent.getStringExtra("filterByCategory")

        if (filterByCategory != null) {
            supportActionBar?.title = filterByCategory
        }

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (filterByCategory != null) {
            subcategoryAdapter = SubcategoryAdapter(emptyList()) { subcategory ->
                val intent = Intent(this, EditCategoryActivity::class.java)
                intent.putExtra("subcategoryId", subcategory.id)
                intent.putExtra("isEditMode", true)
                startActivity(intent)
            }
            recyclerView.adapter = subcategoryAdapter
            categoriesViewModel.loadSubcategories(filterByCategory!!)
        } else {
            categoryAdapter = CategoryAdapter(emptyList()) { category ->
                val intent = Intent(this, EditCategoryActivity::class.java)
                intent.putExtra("categoryId", category.id)
                startActivity(intent)
            }
            recyclerView.adapter = categoryAdapter
            categoriesViewModel.loadCategories()
        }
    }

    private fun setupFab() {
        val fabAddCategory = findViewById<FloatingActionButton>(R.id.fab_AddCategory)
        fabAddCategory.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        categoriesViewModel.categories.observe(this) { categories ->
            categoryAdapter.submitList(categories)
        }
        categoriesViewModel.subcategories.observe(this) { subcategories ->
            subcategoryAdapter.submitList(subcategories)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1001, 0, "Filters")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            1001 -> {
                showFilterDialog()
                return true
            }
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
        // Implement filtering logic here
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
