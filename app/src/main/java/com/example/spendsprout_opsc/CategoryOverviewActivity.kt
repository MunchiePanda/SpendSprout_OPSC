package com.example.spendsprout_opsc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CategoryOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_overview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set sample data for the categories
        setCategoryData()
    }

    /**
     * Set sample data for the categories and update their UI.
     * This method sets the name, balance, and category for each category view.
     */
    private fun setCategoryData() {
        // Get references to the category views
        val categoryView1 = findViewById<CategoryView>(R.id.categoryView1)
        val categoryView2 = findViewById<CategoryView>(R.id.categoryView2)
        val categoryView3 = findViewById<CategoryView>(R.id.categoryView3)

        // Set data for the first category
        categoryView1.setName("Needs")
        categoryView1.setBalance(1500.0)  // Negative balance
        categoryView1.setCategory("Needs")

        // Set data for the second category
        categoryView2.setName("Wants")
        categoryView2.setBalance(500.0)  // Positive balance
        categoryView2.setCategory("Wants")

        // Set data for the third category
        categoryView3.setName("Savings")
        categoryView3.setBalance(2000.0)  // Positive balance
        categoryView3.setCategory("Savings")
    }
}