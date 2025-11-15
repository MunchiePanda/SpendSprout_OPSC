package com.SBMH.SpendSprout.categories

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.CategoryOverviewActivity
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.edit.EditCategoryActivity
import com.SBMH.SpendSprout.overview.OverviewActivity
import com.SBMH.SpendSprout.transactions.TransactionsActivity
import com.SBMH.SpendSprout.accounts.AccountsActivity
import com.SBMH.SpendSprout.reports.ReportsActivity
import com.SBMH.SpendSprout.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class CategoriesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var categoriesViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        setupDrawer()

        categoriesViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        setupRecyclerView()
        setupFab()

        observeViewModel()

        categoriesViewModel.loadCategoriesWithSubcategories()
    }

    private fun setupDrawer() {
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Categories"

        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navView.getHeaderView(0)
        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        val currentUsername = sharedPreferences.getString("username", "User")
        txtUsername.text = currentUsername

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> startActivity(Intent(this, OverviewActivity::class.java))
                R.id.nav_categories -> { /* Already here */ }
                R.id.nav_transactions -> startActivity(Intent(this, TransactionsActivity::class.java))
                R.id.nav_accounts -> startActivity(Intent(this, AccountsActivity::class.java))
                R.id.nav_reports -> startActivity(Intent(this, ReportsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            val intent = Intent(this, EditCategoryActivity::class.java)
            intent.putExtra("categoryId", category.id)
            startActivity(intent)
        }
        recyclerView.adapter = categoryAdapter
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_AddCategory)
        fab.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        categoriesViewModel.categoriesWithSubcategories.observe(this) { categoriesWithSubcategories ->
            val categories = categoriesWithSubcategories.map { it.category }
            categoryAdapter.updateData(categories)
        }
    }
}
