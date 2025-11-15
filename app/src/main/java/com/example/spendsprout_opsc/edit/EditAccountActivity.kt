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
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditAccountActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private val editAccountViewModel: EditAccountViewModel by viewModels()
    private var existingAccount: Account? = null
    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        accountId = intent.getStringExtra("accountId")

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

        setupUI()

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveAccount)
            .setOnClickListener { saveAccount() }
    }

    private fun setupUI() {
        setupAccountTypeSpinner()
        loadAccountIfNeeded()
        setupButtons()
    }

    private fun loadAccountIfNeeded() {
        if (accountId != null) {
            val database = FirebaseDatabase.getInstance()
            val accountsRef = database.getReference("accounts")
            val userId = editAccountViewModel.currentUser?.uid
            if (userId != null) {
                accountsRef.child(userId).child(accountId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        existingAccount = snapshot.getValue(Account::class.java)
                        populateFields()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("EditAccountActivity", "Error loading account: ${error.message}", error.toException())
                        Toast.makeText(this@EditAccountActivity, "Error loading account data", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            populateFields()
        }
    }

    private fun setupAccountTypeSpinner() {
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val accountTypes = arrayOf("Cash", "Bank", "Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountType.adapter = adapter
    }

    private fun populateFields() {
        existingAccount?.let {
            findViewById<EditText>(R.id.edt_AccountName).setText(it.accountName)
            findViewById<EditText>(R.id.edt_Balance).setText(String.format("%.2f", it.accountBalance))
            it.accountNotes?.let { notes ->
                findViewById<EditText>(R.id.edt_Notes).setText(notes)
            }
            val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
            val accountTypeString = it.accountType.name
            val adapter = spinnerAccountType.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(accountTypeString)
            if (position >= 0) {
                spinnerAccountType.setSelection(position)
            }
        }
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveAccount()
        }
    }

    private fun saveAccount() {
        val accountName = findViewById<EditText>(R.id.edt_AccountName).text.toString()
        val accountType = findViewById<Spinner>(R.id.spinner_AccountType).selectedItem.toString()
        val balance = findViewById<EditText>(R.id.edt_Balance).text.toString()
        val notes = findViewById<EditText>(R.id.edt_Notes).text.toString()

        if (accountName.isEmpty() || balance.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val balanceVal = balance.toDoubleOrNull()
        if (balanceVal == null) {
            Toast.makeText(this, "Balance must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        if (existingAccount != null) {
            editAccountViewModel.updateAccount(accountId!!, accountName, accountType, balanceVal, notes)
            Toast.makeText(this, "Account updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            editAccountViewModel.saveAccount(accountName, accountType, balanceVal, notes)
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
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
