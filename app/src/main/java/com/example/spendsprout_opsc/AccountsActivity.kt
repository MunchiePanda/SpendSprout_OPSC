package com.example.spendsprout_opsc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
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

class AccountsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

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
        supportActionBar?.title = "Accounts"

        navView.setNavigationItemSelectedListener(this)

        setupFilterSpinner()
        setupAccountRecyclerView()
        setupFab()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Account saved successfully", Toast.LENGTH_SHORT).show()
            // Refresh the account list
            setupAccountRecyclerView()
        }
    }

    private fun setupFilterSpinner() {
        val spinnerFilter = findViewById<Spinner>(R.id.spinner_Filter)
        val filters = arrayOf("All", "Cash", "Bank")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter
    }

    private fun setupAccountRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_Accounts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mock data for accounts
        val accounts = listOf(
            Account("Cash Wallet", "R 1,200.00", "Groceries: -R 150.00", "Transport: -R 50.00"),
            Account("Savings Account", "R 15,000.00", "Deposit: +R 5,000.00", "Withdrawal: -R 1,000.00"),
            Account("Credit Card", "R -2,500.00", "Amazon: -R 1,200.00", "Takealot: -R 800.00")
        )

        recyclerView.adapter = AccountAdapter(accounts)
    }

    private fun setupFab() {
        val fabAddAccount = findViewById<FloatingActionButton>(R.id.fab_AddAccount)
        fabAddAccount.setOnClickListener {
            startActivityForResult(Intent(this, EditAccountActivity::class.java), 1)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
            }
            R.id.nav_categories -> {
                startActivity(Intent(this, CategoryOverviewActivity::class.java))
            }
            R.id.nav_transactions -> {
                // Placeholder for Transactions Activity
            }
            R.id.nav_accounts -> {
                // Already in Accounts, do nothing
            }
            R.id.nav_reports -> {
                // Placeholder for Reports Activity
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

    data class Account(val name: String, val balance: String, val transaction1: String, val transaction2: String)

    class AccountAdapter(private val accounts: List<Account>) :
        RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

        class AccountViewHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
            val accountNameTextView: android.widget.TextView = view.findViewById(R.id.txt_AccountName)
            val balanceTextView: android.widget.TextView = view.findViewById(R.id.txt_Balance)
            val transaction1TextView: android.widget.TextView = view.findViewById(R.id.txt_Transaction1)
            val transaction2TextView: android.widget.TextView = view.findViewById(R.id.txt_Transaction2)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): AccountViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.account_card_layout, parent, false)
            return AccountViewHolder(view)
        }

        override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
            val account = accounts[position]
            holder.accountNameTextView.text = account.name
            holder.balanceTextView.text = "Balance: ${account.balance}"
            holder.transaction1TextView.text = account.transaction1
            holder.transaction2TextView.text = account.transaction2
        }

        override fun getItemCount(): Int = accounts.size
    }
}
