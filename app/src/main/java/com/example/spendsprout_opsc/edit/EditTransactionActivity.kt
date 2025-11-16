package com.example.spendsprout_opsc.edit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Date
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.BudgetApp
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class EditTransactionActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private lateinit var editTransactionViewModel: EditTransactionViewModel
    private var editingTransactionId: Long? = null
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                contentResolver.takePersistableUriPermission(selectedImageUri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

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
                    startActivity(Intent(this, ReportsActivity::class.java))
                }
                R.id.nav_sprout -> {
                    startActivity(Intent(this, com.example.spendsprout_opsc.sprout.SproutActivity::class.java))
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
        editTransactionViewModel = EditTransactionViewModel()

        setupUI()
        prefillIfEditing()

        // Save FAB triggers same save method
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_SaveTransaction)
            .setOnClickListener { saveTransaction() }
    }

    private fun setupUI() {
        setupCategorySpinner()
        setupAccountSpinner()
        setupRepeatSpinner()
        setupDateButton()
        setupButtons()
        setupImagePicker()
    }

    private fun setupCategorySpinner() {
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)

        // launch a coroutine to fetch data safely
        lifecycleScope.launch {
            // run the database query on a background thread
            val categories = withContext(Dispatchers.IO) {
                BudgetApp.db.subcategoryDao().getAll()
            }

            // extract subcategory names
            val categoryNames = categories.map { it.subcategoryName }.toTypedArray()

            // update UI on the main thread
            val adapter = ArrayAdapter(
                this@EditTransactionActivity,          // replace with your activity name
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }
    }


    private fun setupAccountSpinner() {
        val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
        val accounts = arrayOf("FNB Next Transact", "Cash", "Bank", "Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccount.adapter = adapter
    }

    private fun setupRepeatSpinner() {
        val spinnerRepeat = findViewById<Spinner>(R.id.spinner_Repeat)
        val repeats = arrayOf("None", "Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repeats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRepeat.adapter = adapter
    }

    private fun setupDateButton() {
        val btnDate = findViewById<Button>(R.id.btn_Date)
        btnDate.text = "12 September 2025"
        
        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    btnDate.text = "$dayOfMonth ${getMonthName(month)} $year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun getMonthName(month: Int): String {
        val months = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        return months[month]
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

    private fun prefillIfEditing() {
        val idStr = intent.getStringExtra("transactionId")
        val id = idStr?.toLongOrNull() ?: return
        editingTransactionId = id
        // Load from DB
        editTransactionViewModel.loadTransactionById(id) { expense ->
            if (expense != null) {
                val edtDescription = findViewById<EditText>(R.id.edt_Description)
                val edtAmount = findViewById<EditText>(R.id.edt_Amount)
                val btnDate = findViewById<Button>(R.id.btn_Date)
                val edtNotes = findViewById<EditText>(R.id.edt_Notes)
                edtDescription.setText(expense.expenseName)
                edtAmount.setText(expense.expenseAmount.toString())
                btnDate.setText(SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(expense.expenseDate)))
                edtNotes.setText(expense.expenseNotes ?: "")
                selectedImageUri = expense.expenseImage?.let { android.net.Uri.parse(it) }

                // Show image preview at bottom if exists
                val container = findViewById<android.widget.LinearLayout>(R.id.bottomPreviewContainer)
                val img = findViewById<ImageView>(R.id.img_EditPreview)
                if (expense.expenseImage != null) {
                    img.visibility = View.VISIBLE
                    img.setImageURI(android.net.Uri.parse(expense.expenseImage))
                    container.visibility = View.VISIBLE
                } else {
                    img.visibility = View.GONE
                    container.visibility = View.GONE
                }
            }
        }
    }

    private fun saveTransaction() {
        val edtDescription = findViewById<EditText>(R.id.edt_Description)
        val edtAmount = findViewById<EditText>(R.id.edt_Amount)
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
        val btnDate = findViewById<Button>(R.id.btn_Date)
        val spinnerRepeat = findViewById<Spinner>(R.id.spinner_Repeat)
        val checkBoxOweOwed = findViewById<CheckBox>(R.id.switch_OweOwed)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val description = edtDescription.text.toString()
        val amount = edtAmount.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val dateString = btnDate.text.toString()
        val repeat = spinnerRepeat.selectedItem.toString()
        val oweOwed = checkBoxOweOwed.isChecked
        val notes = edtNotes.text.toString()
        
        // Convert date string to Long
        val date = editTransactionViewModel.parseUiDateToMillis(dateString)

        if (description.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amountVal = amount.toDoubleOrNull()
        if (amountVal == null || amountVal <= 0.0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Determine if the amount is positive or negative
        val formattedAmount = if (oweOwed) "+ R $amount" else "- R $amount"

        try {
            if (editingTransactionId != null) {
                // Update existing transaction
                editTransactionViewModel.updateTransaction(
                    editingTransactionId!!, description, amountVal, category, date, repeat, oweOwed, notes,
                    imagePath = selectedImageUri?.toString()
                )
                Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Create new transaction
                editTransactionViewModel.saveTransaction(
                    description, amountVal, category, date, repeat, oweOwed, notes,
                    imagePath = selectedImageUri?.toString()
                )
                Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
            }

            // Return data
            val resultIntent = Intent().apply {
                putExtra("description", description)
                putExtra("amount", formattedAmount)
                putExtra("category", category)
                putExtra("date", date)
                putExtra("updated", editingTransactionId != null)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving transaction: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("EditTransactionActivity", "Error saving transaction", e)
        }
    }

    private fun setupImagePicker() {
        val btnAddImage = findViewById<Button>(R.id.btn_AddImage)
        btnAddImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
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