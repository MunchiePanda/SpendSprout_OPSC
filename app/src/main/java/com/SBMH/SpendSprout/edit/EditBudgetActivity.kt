package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Budget
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

class EditBudgetActivity : AppCompatActivity() {

    private lateinit var viewModel: EditBudgetViewModel
    private lateinit var budgetNameEditText: TextInputEditText
    private lateinit var budgetAmountEditText: TextInputEditText
    private lateinit var budgetCategoryAutoComplete: AutoCompleteTextView
    private lateinit var budgetStartDateButton: MaterialButton
    private lateinit var budgetEndDateButton: MaterialButton
    private lateinit var saveButton: MaterialButton

    private var budgetId: String? = null
    private var budgetStartDate: Long? = null
    private var budgetEndDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        viewModel = ViewModelProvider(this).get(EditBudgetViewModel::class.java)

        budgetNameEditText = findViewById(R.id.edt_BudgetName)
        budgetAmountEditText = findViewById(R.id.edt_BudgetAmount)
        budgetCategoryAutoComplete = findViewById(R.id.act_BudgetCategory)
        budgetStartDateButton = findViewById(R.id.btn_BudgetStartDate)
        budgetEndDateButton = findViewById(R.id.btn_BudgetEndDate)
        saveButton = findViewById(R.id.btn_SaveBudget)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        budgetId = intent.getStringExtra("budgetId")

        if (budgetId != null) {
            supportActionBar?.title = "Edit Budget"
            loadBudgetDetails(budgetId!!)
        } else {
            supportActionBar?.title = "Add Budget"
        }

        setupCategorySpinner()

        budgetStartDateButton.setOnClickListener {
            showDatePicker(true)
        }

        budgetEndDateButton.setOnClickListener {
            showDatePicker(false)
        }

        saveButton.setOnClickListener {
            val budgetName = budgetNameEditText.text.toString()
            val budgetAmount = budgetAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val budgetCategory = budgetCategoryAutoComplete.text.toString()

            if (budgetName.isNotBlank() && budgetCategory.isNotBlank() && budgetStartDate != null && budgetEndDate != null) {
                if (budgetId != null) {
                    viewModel.updateBudget(budgetId!!, budgetName, budgetAmount, budgetCategory, budgetStartDate!!, budgetEndDate!!)
                    Toast.makeText(this, "Budget updated", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.saveBudget(budgetName, budgetAmount, budgetCategory, budgetStartDate!!, budgetEndDate!!)
                    Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show()
                }
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBudgetDetails(budgetId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val budgetRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/budgets/$budgetId")
            budgetRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val budget = snapshot.getValue(Budget::class.java)
                    if (budget != null) {
                        budgetNameEditText.setText(budget.name)
                        budgetAmountEditText.setText(budget.amount.toString())
                        budgetCategoryAutoComplete.setText(budget.category, false)
                        budgetStartDate = budget.startDate
                        budgetEndDate = budget.endDate
                        updateDateButtons()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditBudgetActivity, "Failed to load budget details", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Needs", "Wants", "Savings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        budgetCategoryAutoComplete.setAdapter(adapter)
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(if (isStartDate) "Select Start Date" else "Select End Date")
            .build()

        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            if (isStartDate) {
                budgetStartDate = selection
            } else {
                budgetEndDate = selection
            }
            updateDateButtons()
        }
    }

    private fun updateDateButtons() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        if (budgetStartDate != null) {
            budgetStartDateButton.text = dateFormat.format(Date(budgetStartDate!!))
        }
        if (budgetEndDate != null) {
            budgetEndDateButton.text = dateFormat.format(Date(budgetEndDate!!))
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
