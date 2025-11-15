package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R

class EditBudgetActivity : AppCompatActivity() {

    private lateinit var viewModel: EditBudgetViewModel
    private lateinit var budgetNameEditText: EditText
    private lateinit var budgetAmountEditText: EditText
    private lateinit var saveButton: Button
    private var budgetId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        viewModel = ViewModelProvider(this).get(EditBudgetViewModel::class.java)

        budgetNameEditText = findViewById(R.id.edt_BudgetName)
        budgetAmountEditText = findViewById(R.id.edt_OpeningBalance)
        saveButton = findViewById(R.id.btn_Save)

        budgetId = intent.getStringExtra("budgetId")

        if (budgetId != null) {
            viewModel.getBudget(budgetId!!).observe(this) { budget ->
                budgetNameEditText.setText(budget.name)
                budgetAmountEditText.setText(budget.amount.toString())
            }
        }

        saveButton.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val budgetName = budgetNameEditText.text.toString()
        val budgetAmount = budgetAmountEditText.text.toString().toDoubleOrNull() ?: 0.0

        if (budgetName.isBlank()) {
            Toast.makeText(this, "Budget name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (budgetId == null) {
            viewModel.saveBudget(budgetName, budgetAmount, "", 0, 0)
        } else {
            viewModel.updateBudget(budgetId!!, budgetName, budgetAmount, "", 0, 0)
        }

        finish()
    }
}
