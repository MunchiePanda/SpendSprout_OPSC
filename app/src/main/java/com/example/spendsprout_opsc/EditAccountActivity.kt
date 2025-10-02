package com.example.spendsprout_opsc

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

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

        // Return data to AccountsActivity
        val resultIntent = Intent().apply {
            putExtra("accountName", accountName)
            putExtra("accountType", accountType)
            putExtra("balance", balance)
            putExtra("notes", notes)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
