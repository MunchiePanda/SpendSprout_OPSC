package com.example.spendsprout_opsc

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * CategoryAdapter: An adapter for a RecyclerView that uses CategoryView.
 * This is similar to a Unity script that manages a list of prefabs.
 */
class CategoryAdapter(
    private val categories: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    /**
     * Data class to represent a category.
     * Similar to a Unity class to hold data.
     */
    data class Category(
        val name: String,
        val balance: Double,
        val categoryType: String
    )

    /**
     * ViewHolder class to hold references to views.
     * Similar to a Unity script attached to a prefab.
     */
    class CategoryViewHolder(val categoryView: CategoryView) : RecyclerView.ViewHolder(categoryView)

    /**
     * Create new views (like instantiating a prefab in Unity).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val categoryView = CategoryView(parent.context)  // Create a new CategoryView
        return CategoryViewHolder(categoryView)  // Return a ViewHolder containing the CategoryView
    }

    /**
     * Replace the contents of a view (like updating a prefab instance in Unity).
     */
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]  // Get the category data
        holder.categoryView.setName(category.name)  // Set the name
        holder.categoryView.setBalance(category.balance)  // Set the balance and its color
        holder.categoryView.setCategory(category.categoryType)  // Set the category and tint the image
    }

    /**
     * Return the size of your dataset (like returning the number of items in a list in Unity).
     */
    override fun getItemCount() = categories.size
}
