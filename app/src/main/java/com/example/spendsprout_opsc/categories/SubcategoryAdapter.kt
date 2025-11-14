package com.example.spendsprout_opsc.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.categories.model.Subcategory

class SubcategoryAdapter(
    private var subcategories: List<Subcategory>,
    private val onItemClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    class SubcategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val allocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
        val imageView: ImageView = view.findViewById(R.id.img_Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        val subcategory = subcategories[position]
        holder.nameTextView.text = subcategory.name
        holder.balanceTextView.text = subcategory.spent
        holder.allocationTextView.text = subcategory.allocation
        holder.spentTextView.text = "" // Clear spent text

        // Set subcategory background color
        try {
            holder.view.setBackgroundColor(android.graphics.Color.parseColor(subcategory.color))
        } catch (e: Exception) {
            // Fallback to default color if parsing fails
            holder.view.setBackgroundColor(android.graphics.Color.parseColor("#2E2F34"))
        }

        // Set subcategory icon color
        holder.imageView.setColorFilter(android.graphics.Color.parseColor(subcategory.color))

        // Set amount color based on positive/negative
        if (subcategory.spent.startsWith("+") || !subcategory.spent.startsWith("-")) {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }

        // Set click listener
        holder.view.setOnClickListener { onItemClick(subcategory) }
    }

    override fun getItemCount(): Int = subcategories.size
    
    fun updateData(newSubcategories: List<Subcategory>) {
        subcategories = newSubcategories
        notifyDataSetChanged()
    }
}
