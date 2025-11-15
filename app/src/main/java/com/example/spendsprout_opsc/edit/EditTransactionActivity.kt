package com.example.spendsprout_opsc.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Expense
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var viewModel: EditTransactionViewModel
    private var transactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        viewModel = ViewModelProvider(this).get(EditTransactionViewModel::class.java)
        transactionId = intent.getStringExtra("transactionId")

        setupUI()
        observeViewModel()

        if (transactionId != null) {
            viewModel.loadTransaction(transactionId!!)
        }
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btn_Date).setOnClickListener {
            showDatePicker()
        }

        findViewById<FloatingActionButton>(R.id.fab_SaveTransaction).setOnClickListener {
            saveTransaction()
        }
    }

    private fun observeViewModel() {
        viewModel.loadSpinnerData()

        viewModel.accounts.observe(this) { accounts ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts.map { it.accountName })
            findViewById<Spinner>(R.id.spinner_Account).adapter = adapter
        }

        viewModel.categories.observe(this) { categories ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
            findViewById<Spinner>(R.id.spinner_Category).adapter = adapter
        }

        viewModel.transaction.observe(this) { transaction ->
            if (transaction != null) {
                findViewById<EditText>(R.id.edt_Description).setText(transaction.notes)
                findViewById<EditText>(R.id.edt_Amount).setText(transaction.amount.toString())
                // Set other fields
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                findViewById<Button>(R.id.btn_Date).text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveTransaction() {
        val amount = findViewById<EditText>(R.id.edt_Amount).text.toString().toDoubleOrNull() ?: 0.0
        val dateText = findViewById<Button>(R.id.btn_Date).text.toString()
        val date = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).parse(dateText)?.time ?: System.currentTimeMillis()
        val category = findViewById<Spinner>(R.id.spinner_Category).selectedItem.toString()
        val account = findViewById<Spinner>(R.id.spinner_Account).selectedItem.toString()
        val notes = findViewById<EditText>(R.id.edt_Description).text.toString()


        val expense = Expense(
            id = transactionId ?: "",
            amount = amount,
            date = date,
            category = category,
            account = account,
            notes = notes
        )

        viewModel.saveTransaction(expense)
        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
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
