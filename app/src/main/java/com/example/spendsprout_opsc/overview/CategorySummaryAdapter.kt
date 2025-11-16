package com.example.spendsprout_opsc.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.model.CategorySummary

class CategorySummaryAdapter(
    private var categories: List<CategorySummary>,
    private val onItemClick: (CategorySummary) -> Unit
) : RecyclerView.Adapter<CategorySummaryAdapter.CategorySummaryViewHolder>() {

    class CategorySummaryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
        val allocatedTextView: TextView = view.findViewById(R.id.txt_Allocation)
        val colorIndicator: ImageView = view.findViewById(R.id.img_Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_layout, parent, false)
        return CategorySummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategorySummaryViewHolder, position: Int) {
        val category = categories[position]
        holder.nameTextView.text = category.name
        holder.spentTextView.text = category.spent
        holder.allocatedTextView.text = category.allocated
        
        // Set color indicator - use setColorFilter instead of setBackgroundColor for ImageView
        holder.colorIndicator.setColorFilter(android.graphics.Color.parseColor(category.color))
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount(): Int = categories.size
    
    fun updateData(newCategories: List<CategorySummary>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}