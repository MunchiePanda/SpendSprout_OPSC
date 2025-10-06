package com.example.spendsprout_opsc.wants

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.wants.model.Subcategory
import com.example.spendsprout_opsc.edit.EditSubcategoryActivity

class SubcategoryAdapter(
    private val subcategories: List<Subcategory>
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    class SubcategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
        //val allocatedTextView: TextView = view.findViewById(R.id.txt_Allocated)
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
        //holder.allocatedTextView.text = subcategory.allocated
        
        // Set color indicator
        //holder.colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(subcategory.color))
        
        // Set spent amount color based on negative values (expenses)
        val spentValue = parseMoney(subcategory.spent)
        val allocationValue = parseMoney(subcategory.allocation)
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
        
        // Set click listener to open EditSubcategoryActivity
        holder.view.setOnClickListener {
            val intent = Intent(holder.view.context, EditSubcategoryActivity::class.java)
            // Convert UI model to database entity
            // Note: We need to get the actual Subcategory_Entity from database
            // For now, we'll pass the ID and let the activity load the full entity
            intent.putExtra("subcategoryId", subcategory.id.toIntOrNull() ?: 0)
            intent.putExtra("isEdit", true)
            holder.view.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = subcategories.size

    private fun parseMoney(text: String): Double {
        return text.replace("R", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
    }
}

