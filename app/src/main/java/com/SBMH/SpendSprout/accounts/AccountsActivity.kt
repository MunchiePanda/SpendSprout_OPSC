
package com.SBMH.SpendSprout.accounts

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.CategoryOverviewActivity
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.edit.EditAccountActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class AccountsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: AccountsViewModel
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        setupDrawer()

        viewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)

        setupRecyclerView()
        setupFab()

        observeViewModel()
    }

    private fun setupDrawer() {
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Accounts"

        val btnMenu = findViewById<android.widget.ImageButton>(R.id.btn_Menu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navView.getHeaderView(0)
        val txtUsername = headerView.findViewById<TextView>(R.id.txt_Username)
        val currentUsername = sharedPreferences.getString("username", "User")
        txtUsername.text = currentUsername

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> Toast.makeText(this, "Overview coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_categories -> startActivity(Intent(this, CategoryOverviewActivity::class.java))
                R.id.nav_transactions -> Toast.makeText(this, "Transactions coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_accounts -> { /* already here */ }
                R.id.nav_reports -> Toast.makeText(this, "Reports coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Accounts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        accountAdapter = AccountAdapter(emptyList()) { account ->
            val intent = Intent(this, EditAccountActivity::class.java)
            intent.putExtra("accountId", account.id)
            startActivity(intent)
        }
        recyclerView.adapter = accountAdapter
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_AddAccount)
        fab.setOnClickListener {
            val intent = Intent(this, EditAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.accounts.observe(this) { accounts ->
            accountAdapter.updateData(accounts)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
