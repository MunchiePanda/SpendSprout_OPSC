package com.example.spendsprout_opsc.wants

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.categories.model.Subcategory

class SubcategoryAdapter(
    private val subcategories: List<Subcategory>,
    private val onItemClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    class SubcategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val allocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subcategory_layout, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        val subcategory = subcategories[position]
        holder.nameTextView.text = subcategory.subcategoryName
        holder.balanceTextView.text = "R ${String.format("%.2f", subcategory.subcategoryBalance)}"
        holder.allocationTextView.text = "R ${String.format("%.2f", subcategory.subcategoryAllocation)}"

        holder.view.setOnClickListener { onItemClick(subcategory) }
    }

    override fun getItemCount(): Int = subcategories.size
}
