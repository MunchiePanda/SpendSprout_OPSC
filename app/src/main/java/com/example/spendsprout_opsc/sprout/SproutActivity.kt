package com.example.spendsprout_opsc.sprout

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.categories.CategoriesActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import com.example.spendsprout_opsc.utils.UserDisplayUtils

class SproutActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var sproutViewModel: SproutViewModel
    
    // UI Components
    private lateinit var plantImageView: ImageView
    private lateinit var btnCheckIn: Button
    private lateinit var progressBarOverall: ProgressBar
    private lateinit var progressBarBudget: ProgressBar
    private lateinit var progressBarCheckIn: ProgressBar
    private lateinit var progressBarCategory: ProgressBar
    
    private lateinit var txtOverallProgress: TextView
    private lateinit var txtBudgetProgress: TextView
    private lateinit var txtCheckInProgress: TextView
    private lateinit var txtCategoryProgress: TextView
    
    private lateinit var txtCheckInStreak: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sprout)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar from the included header bar
        val headerBar = findViewById<View>(R.id.header_bar)
        val toolbar: androidx.appcompat.widget.Toolbar = headerBar.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Sprout"
        
        // Set up menu button click listener
        val btnMenu = headerBar.findViewById<ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)
        UserDisplayUtils.bindNavHeader(navView, this)

        // Initialize ViewModel
        sproutViewModel = SproutViewModel(this)

        setupUI()
        loadData()
    }

    private fun setupUI() {
        plantImageView = findViewById(R.id.imageView)
        btnCheckIn = findViewById(R.id.btn_CheckIn)
        progressBarOverall = findViewById(R.id.progressBar)
        progressBarBudget = findViewById(R.id.progressBar1)
        progressBarCheckIn = findViewById(R.id.progressBar2)
        progressBarCategory = findViewById(R.id.progressBar3)
        
        // Find text views for progress percentages
        txtOverallProgress = findViewById(R.id.txt_lowFlower)
        txtBudgetProgress = findViewById(R.id.txt_medFlower)
        txtCheckInStreak = findViewById(R.id.txt_highFlower)
        
        // These text views display the metrics at the top
        // txt_lowFlower = Overall progress percentage
        // txt_medFlower = Budget progress percentage  
        // txt_highFlower = Check-in streak (days)
        
        btnCheckIn.setOnClickListener {
            sproutViewModel.checkIn { success, message ->
                if (success) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    loadData() // Refresh all data after check-in
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData() {
        // Calculate budget adherence
        sproutViewModel.calculateBudgetAdherence { budgetAdherence ->
            progressBarBudget.progress = budgetAdherence
            txtBudgetProgress.text = "$budgetAdherence%"
        }

        // Calculate check-in streak
        sproutViewModel.calculateCheckInStreak { consecutiveDays, progress ->
            progressBarCheckIn.progress = progress
            txtCheckInStreak.text = "$consecutiveDays days"
        }

        // Calculate category adherence
        sproutViewModel.calculateCategoryAdherence { categoryAdherence ->
            progressBarCategory.progress = categoryAdherence
            // Update text view if exists (could add one in layout later)
            // txtCategoryProgress?.text = "$categoryAdherence%"
        }

        // Calculate overall health after all metrics are loaded
        sproutViewModel.calculateBudgetAdherence { budgetAdherence ->
            sproutViewModel.calculateCheckInStreak { consecutiveDays, checkInProgress ->
                sproutViewModel.calculateCategoryAdherence { categoryAdherence ->
                    sproutViewModel.calculateOverallHealth(
                        budgetAdherence,
                        checkInProgress,
                        categoryAdherence
                    ) { overallHealth ->
                        updateOverallProgress(overallHealth)
                        updatePlantImage(overallHealth)
                    }
                }
            }
        }
    }

    private fun updateOverallProgress(healthPercentage: Int) {
        progressBarOverall.progress = healthPercentage
        txtOverallProgress.text = "$healthPercentage%"
    }

    private fun updatePlantImage(healthPercentage: Int) {
        val plantState = sproutViewModel.getPlantState(healthPercentage)
        val drawableResId = when (plantState) {
            SproutViewModel.PlantState.WILTED -> R.drawable.plant_wilted
            SproutViewModel.PlantState.POOR -> R.drawable.plant_poor
            SproutViewModel.PlantState.GOOD -> R.drawable.plant_good
            SproutViewModel.PlantState.FLOURISHING -> R.drawable.plant_flourishing
        }
        
        // If drawables don't exist yet, use placeholder
        try {
            plantImageView.setImageResource(drawableResId)
        } catch (e: Exception) {
            // Use placeholder if plant images don't exist yet
            plantImageView.setImageResource(R.drawable.circle_drawable)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to activity
        loadData()
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
            R.id.nav_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
            }
            R.id.nav_sprout -> {
                // Already in Sprout, do nothing
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

