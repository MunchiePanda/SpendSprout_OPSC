package com.example.spendsprout_opsc.reports

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spendsprout_opsc.databinding.ActivityReportsBinding
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsActivity : AppCompatActivity() {

    private val viewModel: ReportsViewModel by viewModels()
    private lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                updateBarChart(uiState.categoryTotals)
            }
        }
    }

    private fun updateBarChart(categoryTotals: Map<String, Double>) {
        val dataPoints = categoryTotals.entries.map { entry ->
            ChartDataPoint(entry.key, entry.value, 0.0)
        }
        binding.chartView.setData(dataPoints)
    }
}
