package com.example.spendsprout_opsc.edit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.model.Account
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Transaction
import com.example.spendsprout_opsc.repository.AccountRepository
import com.example.spendsprout_opsc.repository.CategoryRepository
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class EditTransactionActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    companion object {
        const val EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID"
    }

    private val editTransactionViewModel: EditTransactionViewModel by viewModels()
    private var existingTransaction: Transaction? = null

    @Inject
    lateinit var accountRepository: AccountRepository

    @Inject
    lateinit var categoryRepository: CategoryRepository

    private var accounts: List<Account> = emptyList()
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        val transactionId = intent.getStringExtra(EXTRA_TRANSACTION_ID)

        //MENU DRAWER SETUP
        //MenuDrawer: Drawer Layout/ Menu Code and connections
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
                    startActivity(Intent(this, com.example.spendsprout_opsc.overview.OverviewActivity::class.java))
                }
                R.id.nav_categories -> {
                    Toast.makeText(this, "Navigating to Categories", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, com.example.spendsprout_opsc.CategoryOverviewActivity::class.java))
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

        setupUI()
        loadTransactionIfNeeded(transactionId)

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveTransaction)
            .setOnClickListener { saveTransaction() }
    }

    private fun setupUI() {
        setupSpinners()
        setupDateButton()
        setupButtons()
    }

    private fun loadTransactionIfNeeded(transactionId: String?) {
        if (transactionId != null) {
            editTransactionViewModel.getTransaction(transactionId) { transaction ->
                if (transaction != null) {
                    existingTransaction = transaction
                    populateFields()
                } else {
                    Toast.makeText(this, "Error loading transaction data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinners() {
        lifecycleScope.launch {
            launch {
                accountRepository.getAllAccounts().collect { accountList ->
                    accounts = accountList
                    val accountNames = accounts.map { it.accountName }
                    val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
                    val adapter = ArrayAdapter(this@EditTransactionActivity, android.R.layout.simple_spinner_item, accountNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAccount.adapter = adapter
                    populateFields() // repopulate in case transaction loaded before accounts
                }
            }
            launch {
                categoryRepository.getAllCategories().collect { categoryList ->
                    categories = categoryList
                    val categoryNames = categories.map { it.name }
                    val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
                    val adapter = ArrayAdapter(this@EditTransactionActivity, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                    populateFields() // repopulate in case transaction loaded before categories
                }
            }
        }
    }

    private fun setupDateButton() {
        val btnDate = findViewById<Button>(R.id.btn_Date)
        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    btnDate.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(selectedDate.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun populateFields() {
        existingTransaction?.let {
            findViewById<EditText>(R.id.edt_Description).setText(it.description)
            findViewById<EditText>(R.id.edt_Amount).setText(String.format("%.2f", it.amount))
            findViewById<Button>(R.id.btn_Date).text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(it.date))

            val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
            val accountPosition = accounts.indexOfFirst { acc -> acc.accountId == it.accountId }
            if (accountPosition >= 0) {
                spinnerAccount.setSelection(accountPosition)
            }

            val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
            val categoryPosition = categories.indexOfFirst { cat -> cat.categoryId == it.categoryId }
            if (categoryPosition >= 0) {
                spinnerCategory.setSelection(categoryPosition)
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
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val edtDescription = findViewById<EditText>(R.id.edt_Description)
        val edtAmount = findViewById<EditText>(R.id.edt_Amount)
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
        val btnDate = findViewById<Button>(R.id.btn_Date)
        val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)

        val description = edtDescription.text.toString()
        val amount = edtAmount.text.toString()

        if (description.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amountVal = amount.toDoubleOrNull()
        if (amountVal == null) {
            Toast.makeText(this, "Amount must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedAccount = accounts.getOrNull(spinnerAccount.selectedItemPosition)
        val selectedCategory = categories.getOrNull(spinnerCategory.selectedItemPosition)

        if (selectedAccount == null || selectedCategory == null) {
            Toast.makeText(this, "Please select an account and a category", Toast.LENGTH_SHORT).show()
            return
        }

        val date = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).parse(btnDate.text.toString())?.time ?: System.currentTimeMillis()

        if (existingTransaction != null) {
            val updatedTransaction = existingTransaction!!.copy(
                description = description,
                amount = amountVal,
                categoryId = selectedCategory.categoryId,
                date = date,
                accountId = selectedAccount.accountId
            )
            editTransactionViewModel.updateTransaction(updatedTransaction)
            Toast.makeText(this@EditTransactionActivity, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            editTransactionViewModel.saveTransaction(
                description,
                amountVal,
                selectedCategory.categoryId,
                date,
                selectedAccount.accountId
            )
            Toast.makeText(this@EditTransactionActivity, "Transaction created successfully", Toast.LENGTH_SHORT).show()
        }

        setResult(RESULT_OK)
        finish()
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
