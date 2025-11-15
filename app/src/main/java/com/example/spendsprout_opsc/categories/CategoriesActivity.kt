package com.example.spendsprout_opsc.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.databinding.ActivityCategoriesBinding
import com.example.spendsprout_opsc.edit.EditCategoryActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var categoryAdapter: HierarchicalCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

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

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupCategoryRecyclerView()
        setupFab()
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = HierarchicalCategoryAdapter(emptyList(),
            onCategoryClick = { category ->
                val intent = Intent(this, EditCategoryActivity::class.java).apply {
                    putExtra(EditCategoryActivity.EXTRA_CATEGORY_ID, category.categoryId)
                }
                startActivity(intent)
            },
            onSubcategoryClick = { category, subcategory ->
                val intent = Intent(this, EditCategoryActivity::class.java).apply {
                    putExtra(EditCategoryActivity.EXTRA_CATEGORY_ID, category.categoryId)
                    putExtra(EditCategoryActivity.EXTRA_SUBCATEGORY_ID, subcategory.subcategoryId)
                }
                startActivity(intent)
            })
        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(this@CategoriesActivity)
            adapter = categoryAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddCategory.setOnClickListener {
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.categoriesWithSubcategories.collect { categories ->
                categoryAdapter.updateData(categories)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                startActivity(Intent(this, CategoriesActivity::class.java))
            }
            R.id.nav_transactions -> {
                startActivity(Intent(this, TransactionsActivity::class.java))
            }
            R.id.nav_accounts -> {
                startActivity(Intent(this, AccountsActivity::class.java))
            }
            R.id.nav_reports -> {
                android.widget.Toast.makeText(this, "Reports coming soon!", android.widget.Toast.LENGTH_SHORT).show()
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
