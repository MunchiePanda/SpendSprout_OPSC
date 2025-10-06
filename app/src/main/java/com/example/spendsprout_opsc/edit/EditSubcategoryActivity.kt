package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.graphics.Color
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
import kotlinx.coroutines.launch
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class EditSubcategoryActivity : AppCompatActivity() {
    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private lateinit var editSubcategoryViewModel: EditSubcategoryViewModel
    private var existingSubcategory: com.example.spendsprout_opsc.roomdb.Subcategory_Entity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subcategory)
        
        // Get subcategory data from intent if editing existing subcategory
        existingSubcategory = intent.getSerializableExtra("subcategory") as? com.example.spendsprout_opsc.roomdb.Subcategory_Entity
        
        // If no subcategory object but we have a subcategoryId, we'll load it later
        val subcategoryId = intent.getIntExtra("subcategoryId", -1)
        val isEdit = intent.getBooleanExtra("isEdit", false)

        //MENU DRAWER SETUP
        //MenuDrawer: Drawer Layout/ Menu Code and connections
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        
        // Set up the toolbar from the included layout
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //MenuDrawer: Drawer Layout/ Menu Code and connections
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()  //tell toggle it is ready to be used
        //MenuDrawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //able to open toggle, when it is opened the toggle button moves to back arrow

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
                    startActivity(Intent(this, com.example.spendsprout_opsc.overview.OverviewActivity::class.java))
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
                    Toast.makeText(this, "Navigating to Reports", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ReportsActivity::class.java))
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

        // Initialize ViewModel
        editSubcategoryViewModel = EditSubcategoryViewModel()

        setupUI()

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveCategory)
            .setOnClickListener { saveSubcategory() }
    }

    private fun setupUI() {
        setupSpinners()
        loadSubcategoryIfNeeded()
        setupButtons()
    }
    
    private fun setupSpinners() {
        // Setup category spinner with the 3 main categories
        val categorySpinner = findViewById<Spinner>(R.id.spinner_Category)
        val categories = arrayOf("Needs", "Wants", "Savings")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        
        // Setup color spinner with predefined colors
        val colorSpinner = findViewById<Spinner>(R.id.spinner_Color)
        val colors = arrayOf("Red", "Blue", "Green", "Yellow", "Purple", "Orange", "Pink", "Gray")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = colorAdapter
    }
    
    private fun loadSubcategoryIfNeeded() {
        val subcategoryId = intent.getIntExtra("subcategoryId", -1)
        val isEdit = intent.getBooleanExtra("isEdit", false)
        
        if (existingSubcategory == null && isEdit && subcategoryId != -1) {
            // Load subcategory from database
            lifecycleScope.launch {
                try {
                    existingSubcategory = com.example.spendsprout_opsc.BudgetApp.db.subcategoryDao().getById(subcategoryId)
                    populateFields()
                } catch (e: Exception) {
                    Log.e("EditSubcategoryActivity", "Error loading subcategory: ${e.message}", e)
                    Toast.makeText(this@EditSubcategoryActivity, "Error loading subcategory data", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Either we have the subcategory object or we're creating new
            populateFields()
        }
    }
    
    private fun populateFields() {
        if (existingSubcategory != null) {
            // Populate fields with existing subcategory data
            findViewById<EditText>(R.id.edt_subcategoryName).setText(existingSubcategory!!.subcategoryName)
            findViewById<EditText>(R.id.edt_AllocatedAmount).setText(String.format("%.2f", existingSubcategory!!.subcategoryAllocation))
            existingSubcategory!!.subcategoryNotes?.let { notes ->
                findViewById<EditText>(R.id.edt_Notes).setText(notes)
            }
            
            // Set category spinner selection
            val categorySpinner = findViewById<Spinner>(R.id.spinner_Category)
            val categoryId = existingSubcategory!!.categoryId
            val categoryName = when (categoryId) {
                1 -> "Needs"
                2 -> "Wants" 
                3 -> "Savings"
                else -> "Needs" // Default to Needs
            }
            val categoryAdapter = categorySpinner.adapter as ArrayAdapter<String>
            val categoryPosition = categoryAdapter.getPosition(categoryName)
            if (categoryPosition >= 0) {
                categorySpinner.setSelection(categoryPosition)
            }
            
            // Set color spinner selection
            val colorSpinner = findViewById<Spinner>(R.id.spinner_Color)
            val colorHex = String.format("#%06X", (0xFFFFFF and existingSubcategory!!.subcategoryColor))
            val colorName = mapColorToName(colorHex)
            val colorAdapter = colorSpinner.adapter as ArrayAdapter<String>
            val colorPosition = colorAdapter.getPosition(colorName)
            if (colorPosition >= 0) {
                colorSpinner.setSelection(colorPosition)
            }
        }
    }
    
    private fun mapColorToName(colorHex: String): String {
        return when (colorHex.uppercase()) {
            "#FF0000", "#F44336" -> "Red"
            "#0000FF", "#2196F3" -> "Blue"
            "#00FF00", "#4CAF50" -> "Green"
            "#FFFF00", "#FFEB3B" -> "Yellow"
            "#800080", "#9C27B0" -> "Purple"
            "#FFA500", "#FF9800" -> "Orange"
            "#FFC0CB", "#E91E63" -> "Pink"
            "#808080", "#9E9E9E" -> "Gray"
            else -> "Red" // Default to Red
        }
    }
    
    private fun mapColorNameToInt(colorName: String): Int {
        return when (colorName) {
            "Red" -> Color.parseColor("#FF0000")
            "Blue" -> Color.parseColor("#0000FF")
            "Green" -> Color.parseColor("#00FF00")
            "Yellow" -> Color.parseColor("#FFFF00")
            "Purple" -> Color.parseColor("#800080")
            "Orange" -> Color.parseColor("#FFA500")
            "Pink" -> Color.parseColor("#FFC0CB")
            "Gray" -> Color.parseColor("#808080")
            else -> Color.parseColor("#FF0000") // Default to Red
        }
    }
    
    private fun mapCategoryNameToId(categoryName: String): Int {
        return when (categoryName) {
            "Needs" -> 1
            "Wants" -> 2
            "Savings" -> 3
            else -> 1 // Default to Needs
        }
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveSubcategory()
        }
    }

    private fun saveSubcategory() {
        val edtSubcategoryName = findViewById<EditText>(R.id.edt_subcategoryName)
        val categorySpinner = findViewById<Spinner>(R.id.spinner_Category)
        val edtAllocatedAmount = findViewById<EditText>(R.id.edt_AllocatedAmount)
        val colorSpinner = findViewById<Spinner>(R.id.spinner_Color)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val subcategoryName = edtSubcategoryName.text.toString()
        val categoryName = categorySpinner.selectedItem.toString()
        val allocatedAmount = edtAllocatedAmount.text.toString()
        val colorName = colorSpinner.selectedItem.toString()
        val notes = edtNotes.text.toString()

        if (subcategoryName.isEmpty() || allocatedAmount.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val allocationVal = allocatedAmount.toDoubleOrNull()
        
        if (allocationVal == null) {
            Toast.makeText(this, "Allocated amount must be a valid number", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (allocationVal < 0) {
            Toast.makeText(this, "Allocated amount cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        // Map category name to ID and color name to int
        val categoryId = mapCategoryNameToId(categoryName)
        val colorInt = mapColorNameToInt(colorName)

        // Save subcategory using ViewModel with coroutine to wait for completion
        lifecycleScope.launch {
            try {
                if (existingSubcategory != null) {
                    // Update existing subcategory
                    editSubcategoryViewModel.updateSubcategory(
                        existingSubcategory!!.id, 
                        subcategoryName, 
                        categoryId, 
                        colorInt, 
                        0.0, // Balance starts at 0 for subcategories
                        allocationVal, 
                        notes
                    )
                    Toast.makeText(this@EditSubcategoryActivity, "Subcategory updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new subcategory
                    editSubcategoryViewModel.saveSubcategory(
                        subcategoryName, 
                        categoryId, 
                        colorInt, 
                        0.0, // Balance starts at 0 for subcategories
                        allocationVal, 
                        notes
                    )
                    Toast.makeText(this@EditSubcategoryActivity, "Subcategory created successfully", Toast.LENGTH_SHORT).show()
                }

                // Return data
                val resultIntent = Intent().apply {
                    putExtra("subcategoryName", subcategoryName)
                    putExtra("categoryId", categoryId.toString())
                    putExtra("color", colorName)
                    putExtra("allocation", allocatedAmount)
                    putExtra("notes", notes)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditSubcategoryActivity, "Error saving subcategory: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditSubcategoryActivity", "Error saving subcategory", e)
            }
        }
    }

    //MenuDrawer: Drawer Layout/ Menu Code
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
