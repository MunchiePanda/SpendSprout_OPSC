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
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
        val allocatedTextView: TextView = view.findViewById(R.id.txt_Allocated)
        val colorIndicator: View = view.findViewById(R.id.color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.nameTextView.text = category.name
        holder.spentTextView.text = "R ${String.format("%.0f", category.balance)}"
        holder.allocatedTextView.text = "R ${String.format("%.0f", category.allocation)}"
        
        // Set color indicator
        holder.colorIndicator.setBackgroundColor(category.color)
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount(): Int = categories.size
    
    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}

