package com.SBMH.SpendSprout.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.CategoryWithSubcategories
import com.SBMH.SpendSprout.model.Subcategory

class HierarchicalCategoryAdapter(
    private var items: List<Any>,
    private val onCategoryClick: (Category) -> Unit,
    private val onSubcategoryClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_SUBCATEGORY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Category -> VIEW_TYPE_CATEGORY
            is Subcategory -> VIEW_TYPE_SUBCATEGORY
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.category_layout, parent, false)
                CategoryViewHolder(view)
            }
            VIEW_TYPE_SUBCATEGORY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.subcategory_item_layout, parent, false)
                SubcategoryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> {
                val category = items[position] as Category
                holder.bind(category)
                holder.itemView.setOnClickListener { onCategoryClick(category) }
            }
            is SubcategoryViewHolder -> {
                val subcategory = items[position] as Subcategory
                holder.bind(subcategory)
                holder.itemView.setOnClickListener { onSubcategoryClick(subcategory) }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CategoryWithSubcategories>) {
        val flatList = mutableListOf<Any>()
        newItems.forEach {
            flatList.add(it.category)
            flatList.addAll(it.subcategories)
        }
        this.items = flatList
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.txt_Name)

        fun bind(category: Category) {
            categoryNameTextView.text = category.name
        }
    }

    class SubcategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val subcategoryNameTextView: TextView = itemView.findViewById(R.id.txt_Name)

        fun bind(subcategory: Subcategory) {
            subcategoryNameTextView.text = subcategory.name
        }
    }
}
