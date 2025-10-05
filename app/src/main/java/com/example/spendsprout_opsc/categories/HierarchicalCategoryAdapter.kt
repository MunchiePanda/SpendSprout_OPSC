package com.example.spendsprout_opsc.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.categories.model.Category
import com.example.spendsprout_opsc.categories.model.Subcategory

class HierarchicalCategoryAdapter(
    private var categories: List<CategoryWithSubcategories>,
    private val onCategoryClick: (Category) -> Unit,
    private val onSubcategoryClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<HierarchicalCategoryAdapter.CategoryViewHolder>() {

    data class CategoryWithSubcategories(
        val category: Category,
        val subcategories: List<Subcategory>
    )

    class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val categoryNameTextView: TextView = view.findViewById(R.id.txt_Name)
        val categoryBalanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val categoryAllocationTextView: TextView = view.findViewById(R.id.txt_Allocation)
        val categorySpentTextView: TextView = view.findViewById(R.id.txt_Spent)
        val categoryImageView: ImageView = view.findViewById(R.id.img_Category)
        val subcategoriesRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView_Subcategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_with_subcategories_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryWithSubcategories = categories[position]
        val category = categoryWithSubcategories.category
        val subcategories = categoryWithSubcategories.subcategories

        // Set category data
        holder.categoryNameTextView.text = category.name
        holder.categoryBalanceTextView.text = category.spent
        holder.categoryAllocationTextView.text = category.allocation
        holder.categorySpentTextView.text = "" // Clear spent text for main category
        
        // Set category color
        holder.categoryImageView.setColorFilter(android.graphics.Color.parseColor(category.color))
        
        // Set amount color based on positive/negative
        if (category.spent.startsWith("+") || !category.spent.startsWith("-")) {
            holder.categoryBalanceTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.categoryBalanceTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }
        
        // Setup subcategories RecyclerView
        setupSubcategoriesRecyclerView(holder.subcategoriesRecyclerView, subcategories)
        
        // Set click listener for main category
        holder.view.setOnClickListener { onCategoryClick(category) }
    }

    private fun setupSubcategoriesRecyclerView(recyclerView: RecyclerView, subcategories: List<Subcategory>) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        val subcategoryAdapter = SubcategoryAdapter(subcategories) { subcategory ->
            onSubcategoryClick(subcategory)
        }
        recyclerView.adapter = subcategoryAdapter
    }

    override fun getItemCount(): Int = categories.size
    
    fun updateData(newCategories: List<CategoryWithSubcategories>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
