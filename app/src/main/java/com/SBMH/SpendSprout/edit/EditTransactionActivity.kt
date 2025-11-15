package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var viewModel: EditTransactionViewModel
    private lateinit var amountEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var accountSpinner: Spinner
    private lateinit var dateButton: Button
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private var transactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        viewModel = ViewModelProvider(this).get(EditTransactionViewModel::class.java)

        amountEditText = findViewById(R.id.edt_Amount)
        categorySpinner = findViewById(R.id.spinner_Category)
        accountSpinner = findViewById(R.id.spinner_Account)
        dateButton = findViewById(R.id.btn_Date)
        descriptionEditText = findViewById(R.id.edt_Description)
        saveButton = findViewById(R.id.btn_Save)

        transactionId = intent.getStringExtra("transactionId")

        viewModel.categories.observe(this) { categories ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        viewModel.accounts.observe(this) { accounts ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts.map { it.accountName })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            accountSpinner.adapter = adapter
        }

        if (transactionId != null) {
            viewModel.getTransaction(transactionId!!).observe(this) { transaction ->
                amountEditText.setText(transaction.amount.toString())
                descriptionEditText.setText(transaction.notes)

                viewModel.categories.value?.let {
                    val categoryPosition = it.indexOfFirst { c -> c.id == transaction.category }
                    categorySpinner.setSelection(categoryPosition)
                }

                viewModel.accounts.value?.let {
                    val accountPosition = it.indexOfFirst { a -> a.id == transaction.account }
                    accountSpinner.setSelection(accountPosition)
                }
            }
        }

        saveButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val amount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
        val description = descriptionEditText.text.toString()
        val selectedCategory = viewModel.categories.value?.get(categorySpinner.selectedItemPosition)
        val selectedAccount = viewModel.accounts.value?.get(accountSpinner.selectedItemPosition)

        if (description.isBlank()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedAccount == null) {
            Toast.makeText(this, "Please select an account", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Get actual date
        val date = System.currentTimeMillis()

        if (transactionId == null) {
            viewModel.saveTransaction(amount, selectedCategory.id, selectedAccount.id, date, description)
        } else {
            viewModel.updateTransaction(transactionId!!, amount, selectedCategory.id, selectedAccount.id, date, description)
        }

        finish()
    }
}
