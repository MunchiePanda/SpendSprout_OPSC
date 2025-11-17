package com.example.spendsprout_opsc.wants

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.wants.model.Subcategory

class SubcategoryAdapter(
    private val subcategories: List<Subcategory>,
    private val onItemClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    class SubcategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val allocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
        //val colorIndicator: View = view.findViewById(R.id.color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subcategory_layout, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        val subcategory = subcategories[position]
        holder.nameTextView.text = subcategory.name
        holder.spentTextView.text = subcategory.spent
        holder.allocationTextView.text = subcategory.allocation
        
        // Calculate balance (allocation - spent)
        // Note: spent is already negative in display, so we need to treat it as positive expense
        val spentValue = kotlin.math.abs(parseMoney(subcategory.spent)) // Make spent positive
        val allocationValue = parseMoney(subcategory.allocation)
        val balanceValue = allocationValue - spentValue // Now: allocation - positive_spent
        
        // Debug logging (remove in production)
        android.util.Log.d("SubcategoryAdapter", "Subcategory: ${subcategory.name}")
        android.util.Log.d("SubcategoryAdapter", "Spent: '${subcategory.spent}' -> $spentValue")
        android.util.Log.d("SubcategoryAdapter", "Allocation: '${subcategory.allocation}' -> $allocationValue")
        android.util.Log.d("SubcategoryAdapter", "Balance: $allocationValue - $spentValue = $balanceValue")
        
        // Format balance with proper sign
        val balanceText = if (balanceValue >= 0) {
            "R ${String.format(java.util.Locale.US, "%.2f", balanceValue)}"
        } else {
            "-R ${String.format(java.util.Locale.US, "%.2f", kotlin.math.abs(balanceValue))}"
        }
        holder.balanceTextView.text = balanceText
        
        // Set spent amount color based on negative values (expenses)
        if (subcategory.spent.trim().startsWith("-") || spentValue < 0) {
            // Red for expenses (negative values)
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        } else if (spentValue > allocationValue) {
            // Red for overspending
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        } else {
            // Green for positive values within budget
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        }
        
        // Set balance color
        if (balanceValue < 0) {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        } else {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        }
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(subcategory) }
    }

    override fun getItemCount(): Int = subcategories.size

    private fun parseMoney(text: String): Double {
        // Remove currency symbol and extra spaces
        var cleanedText = text.replace("R", "").replace(" ", "").trim()
        
        // Handle negative values properly
        val isNegative = cleanedText.startsWith("-")
        if (isNegative) {
            cleanedText = cleanedText.substring(1)
        }
        
        // Handle locale-specific decimal separators
        // If it has comma but no dot, assume comma is decimal separator
        if (cleanedText.contains(",") && !cleanedText.contains(".")) {
            cleanedText = cleanedText.replace(",", ".")
        } else {
            // Otherwise, remove commas as thousands separators
            cleanedText = cleanedText.replace(",", "")
        }
        
        val value = cleanedText.toDoubleOrNull() ?: 0.0
        return if (isNegative) -value else value
    }
}

