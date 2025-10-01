package com.example.spendsprout_opsc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendsprout_opsc.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding
    private lateinit var viewModel: OverviewViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)

        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupUI() {
        // Set up transactions RecyclerView
        transactionAdapter = TransactionAdapter()
        binding.recyclerViewRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }

        // Set up category cards click listeners
        binding.cardNeeds.setOnClickListener {
            val bundle = Bundle().apply {
                putString("categoryName", "Needs")
            }
            findNavController().navigate(R.id.wantsCategoryFragment, bundle)
        }

        binding.cardWants.setOnClickListener {
            val bundle = Bundle().apply {
                putString("categoryName", "Wants")
            }
            findNavController().navigate(R.id.wantsCategoryFragment, bundle)
        }

        binding.cardSavings.setOnClickListener {
            val bundle = Bundle().apply {
                putString("categoryName", "Savings")
            }
            findNavController().navigate(R.id.wantsCategoryFragment, bundle)
        }
    }

    private fun setupObservers() {
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.textTotalBalance.text = "R ${"%.2f".format(balance)}"
        }

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
        }

        viewModel.categorySummaries.observe(viewLifecycleOwner) { summaries ->
            // Update category cards with data
            binding.cardNeedsTitle.text = summaries["Needs"]?.name ?: "Needs"
            binding.cardNeedsAmount.text =
                "R${"%.2f".format(summaries["Needs"]?.current ?: 0)} / R${"%.2f".format(summaries["Needs"]?.limit ?: 0)}"

            binding.cardWantsTitle.text = summaries["Wants"]?.name ?: "Wants"
            binding.cardWantsAmount.text =
                "R${"%.2f".format(summaries["Wants"]?.current ?: 0)} / R${"%.2f".format(summaries["Wants"]?.limit ?: 0)}"

            binding.cardSavingsTitle.text = summaries["Savings"]?.name ?: "Savings"
            binding.cardSavingsAmount.text =
                "R${"%.2f".format(summaries["Savings"]?.current ?: 0)} / R${"%.2f".format(summaries["Savings"]?.limit ?: 0)}"
        }

        viewModel.accountSummaries.observe(viewLifecycleOwner) { accounts ->
            // Update account cards with data
            if (accounts.isNotEmpty()) {
                binding.cardAccount1Title.text = accounts[0].name
                binding.cardAccount1Amount.text = "R${"%.2f".format(accounts[0].balance)} / R${"%.2f".format(accounts[0].limit)}"
            }
            if (accounts.size > 1) {
                binding.cardAccount2Title.text = accounts[1].name
                binding.cardAccount2Amount.text = "R${"%.2f".format(accounts[1].balance)} / R${"%.2f".format(accounts[1].limit)}"
            }
        }
    }
}
