package com.example.spendsprout_opsc

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendsprout_opsc.categories.CategoriesViewModel
import com.example.spendsprout_opsc.categories.HierarchicalCategoryAdapter
import com.example.spendsprout_opsc.databinding.ActivityCategoryOverviewBinding
import com.example.spendsprout_opsc.edit.EditCategoryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryOverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryOverviewBinding
    private val categoryViewModel: CategoriesViewModel by viewModels()
    private lateinit var categoryAdapter: HierarchicalCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeCategories()

        binding.fabAddCategory.setOnClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = HierarchicalCategoryAdapter(
            emptyList(),
            onCategoryClick = { category ->
                val intent = Intent(this, EditCategoryActivity::class.java).apply {
                    putExtra(EditCategoryActivity.EXTRA_CATEGORY_ID, category.categoryId)
                }
                startActivity(intent)
            },
            onSubcategoryClick = { category, subcategory ->
                val intent = Intent(this, EditCategoryActivity::class.java).apply {
                    putExtra(EditCategoryActivity.EXTRA_CATEGORY_ID, category.categoryId)
                    putExtra(EditCategoryActivity.EXTRA_SUBCATEGORY_ID, subcategory.subcategoryId)
                }
                startActivity(intent)
            }
        )

        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CategoryOverviewActivity)
            adapter = categoryAdapter
        }
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            categoryViewModel.categoriesWithSubcategories.collect { categoriesWithSubcategories ->
                categoryAdapter.updateData(categoriesWithSubcategories)
            }
        }
    }
}
