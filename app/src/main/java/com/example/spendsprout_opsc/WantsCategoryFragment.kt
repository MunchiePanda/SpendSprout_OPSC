package com.example.spendsprout_opsc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spendsprout_opsc.databinding.FragmentWantsCategoryBinding

class WantsCategoryFragment : Fragment() {

    private lateinit var binding: FragmentWantsCategoryBinding
    private lateinit var viewModel: WantsCategoryViewModel
    private val args: WantsCategoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWantsCategoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WantsCategoryViewModel::class.java)

        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupUI() {
        // Set the category name in the toolbar
        binding.toolbar.title = args.categoryName

        // Set up add button
        binding.buttonAddSubcategory.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mainCategory", args.categoryName)
            }
            findNavController().navigate(R.id.editCategoryFragment, bundle)
        }
    }

    private fun setupObservers() {
        viewModel.category.observe(viewLifecycleOwner) { category ->
            binding.textCurrentAmount.text = "Current: R${"%.2f".format(category.current)}"

            // Update subcategories
            // This would be implemented with a RecyclerView in a real app
            // For now we'll just show a toast with the first subcategory
            if (category.subCategories.isNotEmpty()) {
                val firstSub = category.subCategories[0]
                binding.textSubcategoryExample.text = "${firstSub.name}: R${"%.2f".format(firstSub.amount)}"

                binding.textSubcategoryExample.setOnClickListener {
                    Toast.makeText(
                        requireContext(),
                        "Subcategory: ${firstSub.name}\nAmount: R${"%.2f".format(firstSub.amount)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.loadCategory(args.categoryName)
    }
}
