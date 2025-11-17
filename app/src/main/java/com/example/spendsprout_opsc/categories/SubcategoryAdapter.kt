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
    private val onItemClick: (Subcategory) -> Unit,
    private val onItemLongClick: ((Subcategory) -> Unit)? = null
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
            .inflate(R.layout.subcategory_layout, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        val subcategory = subcategories[position]
        holder.nameTextView.text = subcategory.name
        holder.allocationTextView.text = subcategory.allocation
        holder.spentTextView.text = subcategory.spent
        
        // Parse spent and allocation to calculate balance
        val spentValue = parseMoney(subcategory.spent)
        val allocationValue = parseMoney(subcategory.allocation)
        val balanceValue = allocationValue - kotlin.math.abs(spentValue) // Balance = allocation - spent
        
        // Format balance with proper sign
        val balanceText = if (balanceValue >= 0) {
            "R ${String.format(java.util.Locale.US, "%.2f", balanceValue)}"
        } else {
            "-R ${String.format(java.util.Locale.US, "%.2f", kotlin.math.abs(balanceValue))}"
        }
        holder.balanceTextView.text = balanceText
        
        // Set subcategory color
        holder.imageView.setColorFilter(android.graphics.Color.parseColor(subcategory.color))
        
        // Set balance color based on positive/negative
        if (balanceValue >= 0) {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.balanceTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }
        
        // Set spent amount color
        if (spentValue < 0 || subcategory.spent.trim().startsWith("-")) {
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        } else {
            holder.spentTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        }
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(subcategory) }
        
        // Set long click listener for deletion
        onItemLongClick?.let { longClick ->
            holder.view.setOnLongClickListener {
                longClick(subcategory)
                true
            }
        }
    }
    
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

    override fun getItemCount(): Int = subcategories.size
    
    fun updateData(newSubcategories: List<Subcategory>) {
        subcategories = newSubcategories
        notifyDataSetChanged()
    }
}
