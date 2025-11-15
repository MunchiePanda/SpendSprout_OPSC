package com.example.spendsprout_opsc.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.databinding.ItemCategoryBinding
import com.example.spendsprout_opsc.databinding.ItemSubcategoryBinding
import com.example.spendsprout_opsc.model.Category
import com.example.spendsprout_opsc.model.Subcategory

class HierarchicalCategoryAdapter(
    private var categories: List<CategoriesViewModel.CategoryWithSubcategories>,
    private val onCategoryClick: (Category) -> Unit,
    private val onSubcategoryClick: (Category, Subcategory) -> Unit
) : RecyclerView.Adapter<HierarchicalCategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<CategoriesViewModel.CategoryWithSubcategories>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryWithSubcategories: CategoriesViewModel.CategoryWithSubcategories) {
            binding.categoryName.text = categoryWithSubcategories.category.name

            binding.root.setOnClickListener {
                onCategoryClick(categoryWithSubcategories.category)
            }

            val subcategoryAdapter = SubcategoryAdapter(categoryWithSubcategories.subcategories) { subcategory ->
                onSubcategoryClick(categoryWithSubcategories.category, subcategory)
            }
            binding.subcategoriesRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = subcategoryAdapter
            }
        }
    }
}

class SubcategoryAdapter(
    private var subcategories: List<Subcategory>,
    private val onSubcategoryClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val binding = ItemSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubcategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.bind(subcategories[position])
    }

    override fun getItemCount(): Int = subcategories.size

    inner class SubcategoryViewHolder(private val binding: ItemSubcategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subcategory: Subcategory) {
            binding.subcategoryName.text = subcategory.name
            binding.root.setOnClickListener {
                onSubcategoryClick(subcategory)
            }
        }
    }
}