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

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var editCategoryViewModel: EditCategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Category"

        // Initialize ViewModel
        editCategoryViewModel = EditCategoryViewModel()

        setupUI()
    }

    private fun setupUI() {
        setupTypeSpinner()
        setupColorSpinner()
        setupButtons()
    }

    private fun setupTypeSpinner() {
        val spinnerType = findViewById<Spinner>(R.id.spinner_Type)
        val types = arrayOf("Needs", "Wants", "Savings")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
    }

    private fun setupColorSpinner() {
        val spinnerColor = findViewById<Spinner>(R.id.spinner_Color)
        val colors = arrayOf("None", "Red", "Blue", "Green", "Purple", "Orange")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = adapter
    }

    private fun setupButtons() {
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnSave = findViewById<Button>(R.id.btn_Save)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveCategory()
        }
    }

    private fun saveCategory() {
        val edtCategoryName = findViewById<EditText>(R.id.edt_CategoryName)
        val spinnerType = findViewById<Spinner>(R.id.spinner_Type)
        val edtAllocatedAmount = findViewById<EditText>(R.id.edt_AllocatedAmount)
        val spinnerColor = findViewById<Spinner>(R.id.spinner_Color)
        val edtNotes = findViewById<EditText>(R.id.edt_Notes)

        val categoryName = edtCategoryName.text.toString()
        val type = spinnerType.selectedItem.toString()
        val allocatedAmount = edtAllocatedAmount.text.toString()
        val color = when (spinnerColor.selectedItem.toString()) {
            "Red" -> "#F44336"
            "Blue" -> "#2196F3"
            "Green" -> "#4CAF50"
            "Purple" -> "#9C27B0"
            "Orange" -> "#FF9800"
            else -> "#000000"
        }
        val notes = edtNotes.text.toString()

        if (categoryName.isEmpty() || allocatedAmount.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val budgetVal = allocatedAmount.toDoubleOrNull()
        if (budgetVal == null || budgetVal <= 0.0) {
            Toast.makeText(this, "Allocated budget must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Save category using ViewModel
        editCategoryViewModel.saveCategory(categoryName, type, budgetVal, color, notes)

        // Return data
        val resultIntent = Intent().apply {
            putExtra("categoryName", categoryName)
            putExtra("allocatedAmount", "R $allocatedAmount")
            putExtra("allocatedAmountRaw", allocatedAmount)
            putExtra("type", type)
            putExtra("color", color)
            putExtra("colorHex", color)
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

