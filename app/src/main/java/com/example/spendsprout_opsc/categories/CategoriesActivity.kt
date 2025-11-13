package com.example.spendsprout_opsc.categories

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.edit.EditCategoryActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoriesActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    //UI Elements
    lateinit var rvCategories: RecyclerView
    lateinit var btnAddCategory: FloatingActionButton

    //Adapters
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Initialize UI elements
        setupUI()

        // Load categories
        loadCategories()

        // Set up FAB
        findViewById<FloatingActionButton>(R.id.fab_AddCategory).setOnClickListener {
            startActivityForResult(Intent(this, EditCategoryActivity::class.java), 1)
        }
    }

    private fun setupUI() {
        // Set up drawer layout
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Set up the toolbar from the included layout
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up menu button
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up RecyclerView
        rvCategories = findViewById(R.id.recyclerView_Categories)
        rvCategories.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            // Handle category click
            val intent = Intent(this, EditCategoryActivity::class.java).apply {
                putExtra("isEditMode", true)
                putExtra("subcategoryId", category.id)
                putExtra("subcategoryName", category.name)
            }
            startActivityForResult(intent, 1)
        }
        rvCategories.adapter = categoryAdapter

        // Set up add category button
        btnAddCategory = findViewById(R.id.fab_AddCategory)
        btnAddCategory.setOnClickListener {
            startActivityForResult(Intent(this, EditCategoryActivity::class.java), 1)
        }
    }

    private fun loadCategories() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance("https://spendsprout-49aaa-default-rtdb.europe-west1.firebasedatabase.app/").reference

        databaseRef.child("users").child(userId).child("subcategories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories = snapshot.children.mapNotNull { child ->
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val allocation = child.child("allocation").getValue(Double::class.java) ?: 0.0
                        val balance = child.child("balance").getValue(Double::class.java) ?: 0.0
                        val color = child.child("color").getValue(String::class.java) ?: "#D3D3D3"
                        val categoryId = child.child("categoryId").getValue(String::class.java) ?: "Needs"

                        Category(
                            id = child.key ?: "",
                            name = name,
                            spent = "R ${String.format("%.0f", balance)}",
                            allocation = "R ${String.format("%.0f", allocation)}",
                            color = color,
                            categoryType = categoryId
                        )
                    }

                    runOnUiThread {
                        categoryAdapter.updateData(categories)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CategoriesActivity, "Error loading categories: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh categories after adding/editing
            loadCategories()
        }
    }

    // Menu handling
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
