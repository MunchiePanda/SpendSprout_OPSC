package com.example.spendsprout_opsc

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
import java.util.Calendar

class EditTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Transaction"

        setupCategorySpinner()
        setupAccountSpinner()
        setupRepeatSpinner()
        setupDateButton()
        setupButtons()
    }

    private fun setupCategorySpinner() {
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_Category)
        val categories = arrayOf("Needs", "Wants", "Savings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupAccountSpinner() {
        val spinnerAccount = findViewById<Spinner>(R.id.spinner_Account)
        val accounts = arrayOf("Cash", "Bank", "Card")
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
        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                btnDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)
        val btnAttachImage = findViewById<Button>(R.id.btn_AttachImage)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveTransaction()
        }

        btnAttachImage.setOnClickListener {
            Toast.makeText(this, "Image attachment feature coming soon", Toast.LENGTH_SHORT).show()
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

        // Determine if the amount is positive or negative
        val formattedAmount = if (oweOwed) "- R $amount" else "+ R $amount"

        // Return data to TransactionsActivity
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
