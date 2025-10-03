package com.example.spendsprout_opsc.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.model.AccountSummary

/**
 * AccountSummaryAdapter - Simple Account List Management Script
 * 
 * This is like Unity's UI List management for simple account display.
 * Similar to Unity's UI List with basic prefabs for account information.
 * 
 * Responsibilities:
 * - Create account list items (like Unity's Instantiate() for UI elements)
 * - Bind account data to UI components (like Unity's UI Text updates)
 * - Handle account clicks (like Unity's Button.onClick events)
 * - Manage simple account display (like Unity's basic UI List)
 */
class AccountSummaryAdapter(
    private val accounts: List<AccountSummary>,
    private val onItemClick: (AccountSummary) -> Unit
) : RecyclerView.Adapter<AccountSummaryAdapter.AccountSummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_summary_layout, parent, false)
        return AccountSummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountSummaryViewHolder, position: Int) {
        val account = accounts[position]
        
        // Like Unity's UI Text updates for account information
        holder.nameTextView.text = account.name
        holder.balanceTextView.text = account.balance
        holder.limitTextView.text = account.limit
        
        // Set click listener - like Unity's Button.onClick
        holder.itemView.setOnClickListener { onItemClick(account) }
    }

    override fun getItemCount(): Int = accounts.size

    // Account Summary ViewHolder - like Unity's simple UI component
    class AccountSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.txt_AccountName)
        val balanceTextView: TextView = itemView.findViewById(R.id.txt_Balance)
        val limitTextView: TextView = itemView.findViewById(R.id.txt_Limit)
    }
}
