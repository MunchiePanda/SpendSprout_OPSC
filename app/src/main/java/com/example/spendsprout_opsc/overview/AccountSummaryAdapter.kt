package com.example.spendsprout_opsc.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.model.AccountSummary

class AccountSummaryAdapter(
    private var accounts: List<AccountSummary>,
    private val onItemClick: (AccountSummary) -> Unit
) : RecyclerView.Adapter<AccountSummaryAdapter.AccountSummaryViewHolder>() {

    class AccountSummaryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_AccountName)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val limitTextView: TextView = view.findViewById(R.id.txt_Limit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_layout, parent, false)
        return AccountSummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountSummaryViewHolder, position: Int) {
        val account = accounts[position]
        holder.nameTextView.text = account.name
        holder.balanceTextView.text = account.balance
        holder.limitTextView.text = account.limit
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(account) }
    }

    override fun getItemCount(): Int = accounts.size
    
    fun updateData(newAccounts: List<AccountSummary>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}