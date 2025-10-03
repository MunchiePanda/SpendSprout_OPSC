package com.example.spendsprout_opsc.edit

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.R

class EditAccountActivity : AppCompatActivity() {

    private lateinit var editAccountViewModel: EditAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Account"

        // Initialize ViewModel
        editAccountViewModel = EditAccountViewModel()

        setupUI()
    }

    private fun setupUI() {
        setupAccountTypeSpinner()
        setupButtons()
    }

    private fun setupAccountTypeSpinner() {
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val accountTypes = arrayOf("Cash", "Bank", "Card")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountType.adapter = adapter
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
        val edtAccountName = findViewById<EditText>(R.id.edt_AccountName)
        val spinnerAccountType = findViewById<Spinner>(R.id.spinner_AccountType)
        val edtBalance = findViewById<EditText>(R.id.edt_Balance)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val accountName = edtAccountName.text.toString()
        val accountType = spinnerAccountType.selectedItem.toString()
        val balance = edtBalance.text.toString()
        val notes = edtNotes.text.toString()

        if (accountName.isEmpty() || balance.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val balanceVal = balance.toDoubleOrNull()
        if (balanceVal == null) {
            Toast.makeText(this, "Balance must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        // Save account using ViewModel
        editAccountViewModel.saveAccount(accountName, accountType, balanceVal, notes)

        // Return data
        val resultIntent = Intent().apply {
            putExtra("accountName", accountName)
            putExtra("accountType", accountType)
            putExtra("balance", balance)
            putExtra("notes", notes)
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

