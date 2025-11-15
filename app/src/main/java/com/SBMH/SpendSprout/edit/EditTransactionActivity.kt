package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Account
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Expense
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var viewModel: EditTransactionViewModel
    private lateinit var amountEditText: TextInputEditText
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var accountAutoComplete: AutoCompleteTextView
    private lateinit var dateButton: MaterialButton
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton

    private var transactionId: String? = null
    private var transactionDate: Long? = null

    private var accounts: List<Account> = emptyList()
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        viewModel = ViewModelProvider(this).get(EditTransactionViewModel::class.java)

        amountEditText = findViewById(R.id.edt_Amount)
        categoryAutoComplete = findViewById(R.id.act_Category)
        accountAutoComplete = findViewById(R.id.act_Account)
        dateButton = findViewById(R.id.btn_Date)
        descriptionEditText = findViewById(R.id.edt_Description)
        saveButton = findViewById(R.id.btn_SaveTransaction)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        transactionId = intent.getStringExtra("transactionId")

        if (transactionId != null) {
            supportActionBar?.title = "Edit Transaction"
            loadTransactionDetails(transactionId!!)
        } else {
            supportActionBar?.title = "Add Transaction"
            transactionDate = System.currentTimeMillis()
            updateDateButton()
        }

        observeViewModel()

        dateButton.setOnClickListener {
            showDatePicker()
        }

        saveButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun observeViewModel() {
        viewModel.loadAccounts()
        viewModel.loadCategories()

        viewModel.accounts.observe(this) { accountList ->
            accounts = accountList
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, accounts.map { it.accountName })
            accountAutoComplete.setAdapter(adapter)
        }

        viewModel.categories.observe(this) { categoryList ->
            categories = categoryList
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
            categoryAutoComplete.setAdapter(adapter)
        }
    }

    private fun loadTransactionDetails(transactionId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val transactionRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/expenses/$transactionId")
            transactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expense = snapshot.getValue(Expense::class.java)
                    if (expense != null) {
                        amountEditText.setText(expense.amount.toString())
                        descriptionEditText.setText(expense.description)
                        transactionDate = expense.date
                        updateDateButton()

                        // Set category and account spinners
                        val category = categories.find { it.id == expense.categoryId }
                        if (category != null) {
                            categoryAutoComplete.setText(category.name, false)
                        }

                        val account = accounts.find { it.id == expense.accountId }
                        if (account != null) {
                            accountAutoComplete.setText(account.accountName, false)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditTransactionActivity, "Failed to load transaction details", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(transactionDate)
            .build()

        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            transactionDate = selection
            updateDateButton()
        }
    }

    private fun updateDateButton() {
        if (transactionDate != null) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateButton.text = dateFormat.format(Date(transactionDate!!))
        }
    }

    private fun saveTransaction() {
        val amount = amountEditText.text.toString().toDoubleOrNull()
        val categoryName = categoryAutoComplete.text.toString()
        val accountName = accountAutoComplete.text.toString()
        val description = descriptionEditText.text.toString()

        if (amount != null && categoryName.isNotBlank() && accountName.isNotBlank() && transactionDate != null) {
            val category = categories.find { it.name == categoryName }
            val account = accounts.find { it.accountName == accountName }

            if (category != null && account != null) {
                if (transactionId != null) {
                    viewModel.updateTransaction(transactionId!!, amount, category.id, account.id, transactionDate!!, description)
                    Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.saveTransaction(amount, category.id, account.id, transactionDate!!, description)
                    Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
                }
                finish()
            } else {
                Toast.makeText(this, "Invalid category or account", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
