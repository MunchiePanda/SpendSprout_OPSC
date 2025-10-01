package com.example.spendsprout_opsc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Set up the action bar with the navigation controller
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.overviewFragment,
                R.id.categoriesFragment,
                R.id.transactionsFragment,
                R.id.accountsFragment,
                R.id.reportsFragment,
                R.id.settingsFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Handle navigation item selection
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_overview -> {
                    navController.navigate(R.id.overviewFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_categories -> {
                    navController.navigate(R.id.categoriesFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_transactions -> {
                    navController.navigate(R.id.transactionsFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_accounts -> {
                    navController.navigate(R.id.accountsFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_reports -> {
                    navController.navigate(R.id.reportsFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.settingsFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_exit -> {
                    finishAffinity()
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}