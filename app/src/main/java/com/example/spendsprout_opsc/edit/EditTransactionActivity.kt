package com.example.spendsprout_opsc.edit

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
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
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.firebase.FirebaseRepositoryProvider
import com.example.spendsprout_opsc.settings.SettingsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    //Drawer Layout/ Menu variables
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var btnCloseMenu: ImageButton

    private lateinit var editTransactionViewModel: EditTransactionViewModel
    private var editingTransactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        // Initialize ViewModel
        editTransactionViewModel = EditTransactionViewModel()

        // Check if we're editing an existing transaction
        editingTransactionId = intent.getStringExtra("transactionId")

        setupUI()
        if (editingTransactionId != null) {
            prefillIfEditing()
        }

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
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Load categories from Firebase
        lifecycleScope.launch {
            try {
                val subcategories = withContext(Dispatchers.IO) {
                    FirebaseRepositoryProvider.subcategoryRepository.getAllSubcategories().first()
                }
                val categoryNames = subcategories.map { it.subcategoryName }.toTypedArray()
                
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@EditTransactionActivity,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                }
            } catch (e: Exception) {
                android.util.Log.e("EditTransactionActivity", "Error loading categories: ${e.message}", e)
                // Fallback to default list
                withContext(Dispatchers.Main) {
                    val categoryNames = arrayOf("Groceries", "Transport", "Utilities", "Rent", "Entertainment", "Dining Out", "Shopping", "Emergency Fund", "Investment")
                    val adapter = ArrayAdapter(
                        this@EditTransactionActivity,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                }
            }
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
        btnDate.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date())

        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    btnDate.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(selectedDate.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
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

    private fun prefillIfEditing() {
        if (editingTransactionId == null) return

        editTransactionViewModel.loadTransactionById(editingTransactionId!!) { expense ->
            if (expense != null) {
                val edtDescription = findViewById<EditText>(R.id.edt_Description)
                val edtAmount = findViewById<EditText>(R.id.edt_Amount)
                val btnDate = findViewById<Button>(R.id.btn_Date)
                val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
                val edtNotes = findViewById<EditText>(R.id.edt_Notes)

                edtDescription.setText(expense.expenseName)
                edtAmount.setText(expense.expenseAmount.toString())
                btnDate.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(expense.expenseDate))
                edtNotes.setText(expense.expenseNotes ?: "")

                // Try to select the correct category in the spinner
                val adapter = spinnerCategory.adapter as ArrayAdapter<String>
                val position = adapter.getPosition(expense.expenseCategory)
                if (position >= 0) {
                    spinnerCategory.setSelection(position)
                }

                selectedImageUri = expense.expenseImage?.let { Uri.parse(it) }

                // Show image preview at bottom if exists
                val container = findViewById<android.widget.LinearLayout>(R.id.bottomPreviewContainer)
                val img = findViewById<ImageView>(R.id.img_EditPreview)
                if (expense.expenseImage != null) {
                    img.visibility = View.VISIBLE
                    img.setImageURI(Uri.parse(expense.expenseImage))
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
        val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
        val spinnerRepeat = findViewById<Spinner>(R.id.spinner_Repeat)
        val checkBoxOweOwed = findViewById<CheckBox>(R.id.switch_OweOwed)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val description = edtDescription.text.toString()
        val amount = edtAmount.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val dateString = btnDate.text.toString()
        val account = spinnerAccount.selectedItem.toString()
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
                    editingTransactionId!!, description, amountVal, category, date, account, repeat, oweOwed, notes,
                    imagePath = selectedImageUri?.toString()
                ) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                        finishWithResult(description, formattedAmount, category, date, true)
                    } else {
                        Toast.makeText(this, "Failed to update transaction: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // Create new transaction
                editTransactionViewModel.saveTransaction(
                    description, amountVal, category, date, account, repeat, oweOwed, notes,
                    imagePath = selectedImageUri?.toString()
                ) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                        finishWithResult(description, formattedAmount, category, date, false)
                    } else {
                        Toast.makeText(this, "Failed to save transaction: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving transaction: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("EditTransactionActivity", "Error saving transaction", e)
        }
    }

    private fun finishWithResult(description: String, amount: String, category: String, date: Long, updated: Boolean) {
        // Return data
        val resultIntent = Intent().apply {
            putExtra("description", description)
            putExtra("amount", amount)
            putExtra("category", category)
            putExtra("date", date)
            putExtra("updated", updated)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private val pickImageRequestCode = 5011
    private var selectedImageUri: Uri? = null

    private fun setupImagePicker() {
        val btnAddImage = findViewById<Button>(R.id.btn_AddImage)
        btnAddImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, pickImageRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequestCode && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                contentResolver.takePersistableUriPermission(selectedImageUri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Menu handling
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
