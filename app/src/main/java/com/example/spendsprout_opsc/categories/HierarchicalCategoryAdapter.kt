package com.example.spendsprout_opsc.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.categories.model.Category

/**
 * HierarchicalCategoryAdapter - Handles both main categories and subcategories
 * 
 * This is like Unity's UI List that can show both parent and child items
 * Similar to Unity's hierarchical UI system or nested prefabs
 */
class HierarchicalCategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MAIN_CATEGORY = 0
        private const val TYPE_SUBCATEGORY = 1
    }

    // Flatten the hierarchical data into a list for RecyclerView
    private val flatItems = mutableListOf<Any>()

    init {
        // Like Unity's flattening hierarchical data for UI display
        categories.forEach { category ->
            flatItems.add(category) // Add main category
            // Note: subcategories are now separate entities, not nested in Category
        }
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_MAIN_CATEGORY // All items are main categories now
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MAIN_CATEGORY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_layout, parent, false)
                MainCategoryViewHolder(view)
            }
            TYPE_SUBCATEGORY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.subcategory_item_layout, parent, false)
                SubcategoryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = flatItems[position] as Category
        
        when (holder) {
            is MainCategoryViewHolder -> {
                // Like Unity's UI Text updates for main categories
                holder.nameTextView.text = item.name
                holder.spentTextView.text = "R ${String.format("%.0f", item.balance)}"
                holder.allocatedTextView.text = "R ${String.format("%.0f", item.allocation)}"
                
                // Set color indicator - like Unity's color changes
                holder.colorIndicator.setBackgroundColor(item.color)
                
                // Set spent amount color based on overspending
                if (item.balance > item.allocation) {
                    holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
                } else {
                    holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
                }
                
                // Set click listener - like Unity's Button.onClick
                holder.itemView.setOnClickListener { onItemClick(item) }
            }
        }
    }

    override fun getItemCount(): Int = flatItems.size

    // Main Category ViewHolder - like Unity's main UI component
    class MainCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.txt_Name)
        val spentTextView: TextView = itemView.findViewById(R.id.txt_Spent)
        val allocatedTextView: TextView = itemView.findViewById(R.id.txt_Allocated)
        val colorIndicator: View = itemView.findViewById(R.id.color_indicator)
    }

    // Subcategory ViewHolder - like Unity's child UI component
    class SubcategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.txt_Name)
        val spentTextView: TextView = itemView.findViewById(R.id.txt_Spent)
        val colorIndicator: View = itemView.findViewById(R.id.color_indicator)
    }
}
