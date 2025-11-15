package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R

class EditAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: EditAccountViewModel
    private lateinit var accountNameEditText: EditText
    private lateinit var accountBalanceEditText: EditText
    private lateinit var saveButton: Button
    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        viewModel = ViewModelProvider(this).get(EditAccountViewModel::class.java)

        accountNameEditText = findViewById(R.id.edt_AccountName)
        accountBalanceEditText = findViewById(R.id.edt_Balance)
        saveButton = findViewById(R.id.btn_Save)

        accountId = intent.getStringExtra("accountId")

        if (accountId != null) {
            viewModel.getAccount(accountId!!).observe(this) { account ->
                accountNameEditText.setText(account.accountName)
                accountBalanceEditText.setText(account.accountBalance.toString())
            }
        }

        saveButton.setOnClickListener {
            saveAccount()
        }
    }

    private fun saveAccount() {
        val accountName = accountNameEditText.text.toString()
        val accountBalance = accountBalanceEditText.text.toString().toDoubleOrNull() ?: 0.0

        if (accountName.isBlank()) {
            Toast.makeText(this, "Account name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (accountId == null) {
            viewModel.saveAccount(accountName, accountBalance)
        } else {
            viewModel.updateAccount(accountId!!, accountName, accountBalance)
        }

        finish()
    }
}
