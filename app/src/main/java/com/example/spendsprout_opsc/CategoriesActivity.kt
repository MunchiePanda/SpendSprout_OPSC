package com.example.spendsprout_opsc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class CategoriesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

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

        setupCategoryRecyclerView()
        setupFab()
    }

    private fun setupCategoryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Categories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mock data for categories
        val categories = listOf(
            Category("Needs", "R 10,000", "R 8,000", "#BD804A"),
            Category("Wants", "R 5,000", "R 4,500", "#88618E"),
            Category("Savings", "R 3,000", "R 2,500", "#6EA19E")
        )

        recyclerView.adapter = CategoryAdapter(categories, { category: Category ->
            // Handle category click
            val intent = Intent(this, WantsCategoryActivity::class.java)
            intent.putExtra("categoryName", category.name)
            startActivity(intent)
        })
    }

    private fun setupFab() {
        val fabAddCategory = findViewById<FloatingActionButton>(R.id.fab_AddCategory)
        fabAddCategory.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java)
            startActivity(intent)
        }
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

    data class Category(val name: String, val allocated: String, val spent: String, val color: String)

    class CategoryAdapter(private val categories: List<Category>, private val onItemClick: (Category) -> Unit) :
        RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.txt_Name)
            val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
            val allocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.category_layout, parent, false)
            return CategoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val category = categories[position]
            holder.nameTextView.text = category.name
            holder.balanceTextView.text = "- ${category.spent}"
            holder.allocationTextView.text = category.allocated

            // Set click listener
            holder.view.setOnClickListener { onItemClick(category) }
        }

        override fun getItemCount(): Int = categories.size
    }
}
