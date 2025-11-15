package com.example.spendsprout_opsc

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.spendsprout_opsc.accounts.AccountsActivity
import com.example.spendsprout_opsc.databinding.ActivityMainBinding
import com.example.spendsprout_opsc.login.LoginActivity
import com.example.spendsprout_opsc.overview.OverviewActivity
import com.example.spendsprout_opsc.reports.ReportsActivity
import com.example.spendsprout_opsc.transactions.TransactionsActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if the user is authenticated. If not, send them to the Login screen.
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Important: finish MainActivity so user can't come back without logging in
            return // Stop the rest of onCreate from running
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardTransactions.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        binding.cardAccounts.setOnClickListener {
            startActivity(Intent(this, AccountsActivity::class.java))
        }

        binding.cardOverview.setOnClickListener {
            startActivity(Intent(this, OverviewActivity::class.java))
        }

        binding.cardReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        // Add other listeners for settings, etc. if you have them
        // For example:
        // binding.settingsButton.setOnClickListener {
        //     startActivity(Intent(this, SettingsActivity::class.java))
        // }
    }
}
