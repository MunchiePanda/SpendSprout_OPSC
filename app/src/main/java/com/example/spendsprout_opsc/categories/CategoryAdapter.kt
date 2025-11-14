package com.example.spendsprout_opsc.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.categories.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val allocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.nameTextView.text = category.name
        holder.balanceTextView.text = category.spent
        holder.allocationTextView.text = category.allocation
        holder.spentTextView.text = "" // Clear the spent text

        // Set category background color
        try {
            holder.view.setBackgroundColor(android.graphics.Color.parseColor(category.color))
        } catch (e: Exception) {
            // Fallback to default color if parsing fails
            holder.view.setBackgroundColor(android.graphics.Color.parseColor("#2E2F34"))
        }

        // Set amount color based on positive/negative
        if (category.spent.startsWith("+")) {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }

        // Set click listener
        holder.view.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount(): Int = categories.size
    
    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}