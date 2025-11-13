package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCategoryActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private lateinit var editCategoryViewModel: EditCategoryViewModel
    private var isEditMode = false
    private var subcategoryId: Int? = null
    private var subcategoryName: String? = null
    private var existingSubcategory: com.example.spendsprout_opsc.roomdb.Subcategory_Entity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        // Initialize ViewModel
        editCategoryViewModel = EditCategoryViewModel()

        // Check if we're in edit mode
        isEditMode = intent.getBooleanExtra("isEditMode", false)
        val subcategoryIdString = intent.getStringExtra("subcategoryId")
        subcategoryId = subcategoryIdString?.toIntOrNull()
        subcategoryName = intent.getStringExtra("subcategoryName")

        Log.d("EditCategoryActivity", "Edit mode: $isEditMode, Subcategory ID: $subcategoryId, Name: $subcategoryName")

        setupUI()
        if (isEditMode) {
            prefillIfEditing()
        }

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveCategory)
            .setOnClickListener { saveCategory() }
    }

    private fun setupUI() {
        //MENU DRAWER SETUP
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Set up the toolbar from the included layout
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Set up menu button click listener
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //MenuDrawer: Access the close button from the navigation view header
        val headerView = navigationView.getHeaderView(0)
        btnCloseMenu = headerView.findViewById(R.id.btn_CloseMenu)

        //MenuDrawer: Close menu button click listener to close drawer
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }

        //MenuDrawer: respond to menu item clicks - using lambda like OverviewActivity
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> {
                    Toast.makeText(this, "Navigating to Overview", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OverviewActivity::class.java))
                }
                R.id.nav_categories -> {
                    Toast.makeText(this, "Navigating to Categories", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CategoriesActivity::class.java))
                }
                R.id.nav_transactions -> {
                    Toast.makeText(this, "Navigating to Transactions", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, TransactionsActivity::class.java))
                }
                R.id.nav_accounts -> {
                    Toast.makeText(this, "Navigating to Accounts", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AccountsActivity::class.java))
                }
                R.id.nav_reports -> {
                    Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Navigating to Settings", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_exit -> {
                    Toast.makeText(this, "Exiting app", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Set up type spinner
        val spinnerType = findViewById<Spinner>(R.id.spinner_Type)
        val types = arrayOf("Needs", "Wants", "Savings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Set up color spinner
        val spinnerColor = findViewById<Spinner>(R.id.spinner_Color)
        val colors = arrayOf("Red", "Blue", "Green", "Purple", "Orange")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = colorAdapter
    }

    private fun prefillIfEditing() {
        if (subcategoryId == null) return

        // TODO: Load subcategory from Firebase instead of Room
        // For now, just show a message that editing is not fully implemented yet
        Toast.makeText(this, "Loading subcategory data...", Toast.LENGTH_SHORT).show()
    }

    private fun saveCategory() {
        val edtCategoryName = findViewById<EditText>(R.id.edt_CategoryName)
        val spinnerType = findViewById<Spinner>(R.id.spinner_Type)
        val edtAllocatedAmount = findViewById<EditText>(R.id.edt_AllocatedAmount)
        val spinnerColor = findViewById<Spinner>(R.id.spinner_Color)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val categoryName = edtCategoryName.text.toString()
        val type = spinnerType.selectedItem.toString()
        val allocatedAmount = edtAllocatedAmount.text.toString()
        val color = when (spinnerColor.selectedItem.toString()) {
            "Red" -> "#F44336"
            "Blue" -> "#2196F3"
            "Green" -> "#4CAF50"
            "Purple" -> "#9C27B0"
            "Orange" -> "#FF9800"
            else -> "#000000"
        }
        val notes = edtNotes.text.toString()

        if (categoryName.isEmpty() || allocatedAmount.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val budgetVal = allocatedAmount.toDoubleOrNull()
        if (budgetVal == null || budgetVal <= 0.0) {
            Toast.makeText(this, "Allocated budget must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (isEditMode && existingSubcategory != null) {
                // Update existing subcategory
                lifecycleScope.launch {
                    try {
                        // Update the existing subcategory
                        val updatedSubcategory = existingSubcategory!!.copy(
                            subcategoryName = categoryName,
                            subcategoryAllocation = budgetVal,
                            subcategoryNotes = notes.ifBlank { null }
                        )

                        // TODO: Update in Firebase instead of Room
                        // For now, just show success message

                        Toast.makeText(this@EditCategoryActivity, "Subcategory '$categoryName' updated successfully", Toast.LENGTH_SHORT).show()

                        // Return data
                        val resultIntent = Intent().apply {
                            putExtra("categoryName", categoryName)
                            putExtra("allocatedAmount", "R $allocatedAmount")
                            putExtra("allocatedAmountRaw", allocatedAmount)
                            putExtra("type", type)
                            putExtra("color", color)
                            putExtra("colorHex", color)
                            putExtra("updated", true)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@EditCategoryActivity, "Error updating subcategory: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("EditCategoryActivity", "Error updating subcategory", e)
                    }
                }
            } else {
                // Create new subcategory
                editCategoryViewModel.saveCategory(categoryName, type, budgetVal, color, notes) { success, error ->
                    if (success) {
                        // Show success message
                        Toast.makeText(this, "Subcategory '$categoryName' added to $type", Toast.LENGTH_SHORT).show()

                        // Return data
                        val resultIntent = Intent().apply {
                            putExtra("categoryName", categoryName)
                            putExtra("allocatedAmount", "R $allocatedAmount")
                            putExtra("allocatedAmountRaw", allocatedAmount)
                            putExtra("type", type)
                            putExtra("color", color)
                            putExtra("colorHex", color)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to save subcategory: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving subcategory: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("EditCategoryActivity", "Error saving subcategory", e)
        }
    }

    //MenuDrawer: Drawer Layout/ Menu Code
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
