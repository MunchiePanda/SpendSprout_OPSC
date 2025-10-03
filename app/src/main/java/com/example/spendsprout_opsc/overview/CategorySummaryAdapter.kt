package com.example.spendsprout_opsc.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.model.CategorySummary

/**
 * CategorySummaryAdapter - Simple Category List Management Script
 * 
 * This is like Unity's UI List management for simple category display.
 * Similar to Unity's UI List with basic prefabs for category information.
 * 
 * Responsibilities:
 * - Create category list items (like Unity's Instantiate() for UI elements)
 * - Bind category data to UI components (like Unity's UI Text updates)
 * - Handle category clicks (like Unity's Button.onClick events)
 * - Manage simple category display (like Unity's basic UI List)
 */
class CategorySummaryAdapter(
    private val categories: List<CategorySummary>,
    private val onItemClick: (CategorySummary) -> Unit
) : RecyclerView.Adapter<CategorySummaryAdapter.CategorySummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_summary_layout, parent, false)
        return CategorySummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategorySummaryViewHolder, position: Int) {
        val category = categories[position]
        
        // Like Unity's UI Text updates for category information
        holder.nameTextView.text = category.name
        holder.spentTextView.text = category.spent
        holder.allocatedTextView.text = category.allocated
        
        // Set color indicator - like Unity's color changes
        holder.colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(category.color))
        
        // Set spent amount color based on positive/negative
        if (category.spent.startsWith("-")) {
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        } else {
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        }
        
        // Set click listener - like Unity's Button.onClick
        holder.itemView.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount(): Int = categories.size

    // Category Summary ViewHolder - like Unity's simple UI component
    class CategorySummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.txt_Name)
        val spentTextView: TextView = itemView.findViewById(R.id.txt_Spent)
        val allocatedTextView: TextView = itemView.findViewById(R.id.txt_Allocated)
        val colorIndicator: View = itemView.findViewById(R.id.color_indicator)
    }
}
