package com.example.spendsprout_opsc.edit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.R
import java.util.Calendar

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var editTransactionViewModel: EditTransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Transaction"

        // Initialize ViewModel
        editTransactionViewModel = EditTransactionViewModel()

        setupUI()
    }

    private fun setupUI() {
        setupCategorySpinner()
        setupAccountSpinner()
        setupRepeatSpinner()
        setupDateButton()
        setupButtons()
    }

    private fun setupCategorySpinner() {
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
        val categories = arrayOf("Groceries", "Needs", "Wants", "Savings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
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

    private fun saveTransaction() {
        val edtDescription = findViewById<EditText>(R.id.edt_Description)
        val edtAmount = findViewById<EditText>(R.id.edt_Amount)
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
        val btnDate = findViewById<Button>(R.id.btn_Date)
        val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
        val spinnerRepeat = findViewById<Spinner>(R.id.spinner_Repeat)
        val switchOweOwed = findViewById<Switch>(R.id.switch_OweOwed)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val description = edtDescription.text.toString()
        val amount = edtAmount.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val date = btnDate.text.toString()
        val account = spinnerAccount.selectedItem.toString()
        val repeat = spinnerRepeat.selectedItem.toString()
        val oweOwed = switchOweOwed.isChecked
        val notes = edtNotes.text.toString()

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
        val formattedAmount = if (oweOwed) "- R $amount" else "+ R $amount"

        // Save transaction using ViewModel
        editTransactionViewModel.saveTransaction(description, amountVal, category, date, account, repeat, oweOwed, notes)

        // Return data
        val resultIntent = Intent().apply {
            putExtra("description", description)
            putExtra("amount", formattedAmount)
            putExtra("category", category)
            putExtra("date", date)
        }
        setResult(RESULT_OK, resultIntent)
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

