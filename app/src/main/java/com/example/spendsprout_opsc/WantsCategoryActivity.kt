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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.example.spendsprout_opsc.EditCategoryActivity

class WantsCategoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private val subCategories = mutableListOf<SubCategory>()

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
        supportActionBar?.title = intent.getStringExtra("categoryName") ?: "Subcategories"

        navView.setNavigationItemSelectedListener(this)

        // Initialize with mock data
        subCategories.addAll(listOf(
            SubCategory("Eating Out", "R 2,000", "R 1,800", "#FF5722"),
            SubCategory("Entertainment", "R 1,500", "R 1,600", "#F44336"),
            SubCategory("Shopping", "R 1,000", "R 900", "#9C27B0")
        ))

        setupSubCategoryRecyclerView()
        setupFab()
    }

    private fun setupSubCategoryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_SubCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        subCategoryAdapter = SubCategoryAdapter(subCategories)
        recyclerView.adapter = subCategoryAdapter
    }

    private fun setupFab() {
        val fabAddSubCategory = findViewById<FloatingActionButton>(R.id.fab_AddSubCategory)
        fabAddSubCategory.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java)
            startActivityForResult(intent, 1)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val categoryName = data?.getStringExtra("categoryName") ?: ""
            val allocatedAmount = data?.getStringExtra("allocatedAmount") ?: "R 0"
            val color = data?.getStringExtra("color") ?: "#000000"

            // Add the new subcategory
            subCategories.add(SubCategory(categoryName, allocatedAmount, "R 0", color))
            subCategoryAdapter.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    data class SubCategory(val name: String, val allocated: String, val spent: String, val color: String)

    class SubCategoryAdapter(private val subCategories: MutableList<SubCategory>) :
        RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>() {

        class SubCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.txt_Name)
            val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
            val allocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.subcategory_layout, parent, false)
            return SubCategoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
            val subCategory = subCategories[position]
            holder.nameTextView.text = subCategory.name
            holder.balanceTextView.text = "- ${subCategory.spent}"

            // Highlight in red if overspent, green if under budget
            val spentAmount = subCategory.spent.replace("R ", "").replace(",", "").toDouble()
            val allocatedAmount = subCategory.allocated.replace("R ", "").replace(",", "").toDouble()
            if (spentAmount > allocatedAmount) {
                holder.balanceTextView.setTextColor(holder.itemView.resources.getColor(R.color.NegativeBalanceColor))
            } else {
                holder.balanceTextView.setTextColor(holder.itemView.resources.getColor(R.color.PositiveBalanceColor))
            }

            holder.allocationTextView.text = subCategory.allocated
        }

        override fun getItemCount(): Int = subCategories.size
    }
}
