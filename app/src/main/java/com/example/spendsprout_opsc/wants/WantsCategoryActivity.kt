package com.example.spendsprout_opsc.wants

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class WantsCategoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var wantsViewModel: WantsViewModel
    private lateinit var subcategoryAdapter: SubcategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wants_category)

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
        supportActionBar?.title = "Wants Category"

        navView.setNavigationItemSelectedListener(this)

        // Initialize ViewModel
        wantsViewModel = WantsViewModel()

        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupSubcategoryRecyclerView()
        setupFab()
        setupSummary()
    }

    private fun setupSubcategoryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Subcategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val subcategories = wantsViewModel.getSubcategories()
        subcategoryAdapter = SubcategoryAdapter(subcategories) { subcategory ->
            // Handle subcategory click - open edit screen
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
            intent.putExtra("subcategoryId", subcategory.id)
            startActivity(intent)
        }
        recyclerView.adapter = subcategoryAdapter
    }

    private fun setupFab() {
        val fabAddSubcategory = findViewById<FloatingActionButton>(R.id.fab_AddSubcategory)
        fabAddSubcategory.setOnClickListener {
            val intent = Intent(this, com.example.spendsprout_opsc.edit.EditCategoryActivity::class.java)
            intent.putExtra("parentCategory", "Wants")
            startActivity(intent)
        }
    }

    private fun setupSummary() {
        //val summaryTextView = findViewById<android.widget.TextView>(R.id.txt_Summary)
        //summaryTextView.text = "- R 120"
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
                startActivity(Intent(this, CategoriesActivity::class.java))
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

