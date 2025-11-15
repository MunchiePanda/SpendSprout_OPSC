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
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Subcategory
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView

class EditCategoryActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private val editCategoryViewModel: EditCategoryViewModel by viewModels()

    private var isEditMode = false
    private var subcategoryId: String? = null
    private var existingSubcategory: Subcategory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        //MENU DRAWER SETUP
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val headerView = navigationView.getHeaderView(0)
        btnCloseMenu = headerView.findViewById(R.id.btn_CloseMenu)
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> {
                    startActivity(Intent(this, com.example.spendsprout_opsc.overview.OverviewActivity::class.java))
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
            true
        }

        isEditMode = intent.getBooleanExtra("isEditMode", false)
        subcategoryId = intent.getStringExtra("subcategoryId")

        setupUI()
        if (isEditMode) {
            prefillIfEditing()
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveCategory)
            .setOnClickListener { saveCategory() }
    }

    private fun setupUI() {
        setupTypeSpinner()
        setupButtons()
    }

    private fun setupTypeSpinner() {
        val spinnerType = findViewById<Spinner>(R.id.spinner_Type)
        val types = arrayOf("Needs", "Wants", "Savings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveCategory()
        }
    }

    private fun prefillIfEditing() {
        if (isEditMode && subcategoryId != null) {
            editCategoryViewModel.loadSubcategory(subcategoryId!!) { subcategory ->
                existingSubcategory = subcategory
                existingSubcategory?.let {
                    findViewById<EditText>(R.id.edt_CategoryName).setText(it.name)
                }
            }
        }
    }

    private fun saveCategory() {
        val categoryName = findViewById<EditText>(R.id.edt_CategoryName).text.toString()
        val type = findViewById<Spinner>(R.id.spinner_Type).selectedItem.toString()

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditMode && existingSubcategory != null) {
            // Update existing subcategory
            val updatedSubcategory = existingSubcategory!!.copy(
                name = categoryName,
            )
            editCategoryViewModel.updateSubcategory(updatedSubcategory)
            Toast.makeText(this, "Subcategory '$categoryName' updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            // Create new subcategory
            editCategoryViewModel.saveCategory(categoryName, type)
            Toast.makeText(this, "Subcategory '$categoryName' added to $type", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
