package com.example.spendsprout_opsc

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.SBMH.SpendSprout.R


//private val MainActivity.menu_drawer_layout: DrawerLayout

class MainActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnMenu: ImageButton
    lateinit var btnCloseMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //MenuDrawer: Drawer Layout/ Menu Code and connections
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu = findViewById(R.id.btn_Menu)

        // MenuDrawer: Access the close button from the navigation view header
        val headerView = navigationView.getHeaderView(0)
        btnCloseMenu = headerView.findViewById(R.id.btn_CloseMenu)
        //MenuDrawer: Drawer Layout/ Menu Code and connections
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()  //tell toggle it is ready to be used
        //MenuDrawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //able to open toggle, when it is opened the toggle button moves to back arrow

        //MenuDrawer: Menu button click listener to open drawer
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        //MenuDrawer: Close menu button click listener to close drawer
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }

        //MenuDrawer: respond to menu item clicks
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_overview
                    -> Toast.makeText(applicationContext, "Overview", Toast.LENGTH_SHORT).show()
                R.id.nav_reports
                    -> Toast.makeText(applicationContext, "Overview", Toast.LENGTH_SHORT).show()
            }
            true
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