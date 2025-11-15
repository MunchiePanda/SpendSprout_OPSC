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
    private var categories: List<Pair<Category, List<Subcategory>>>,
    private val onCategoryClick: (Category) -> Unit,
    private val onSubcategoryClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<HierarchicalCategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val categoryNameTextView: TextView = view.findViewById(R.id.txt_Name)
        val subcategoriesRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView_Subcategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_with_subcategories_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val (category, subcategories) = categories[position]

        // Set category data
        holder.categoryNameTextView.text = category.name
        
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
    
    fun updateData(newCategories: List<Pair<Category, List<Subcategory>>>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
