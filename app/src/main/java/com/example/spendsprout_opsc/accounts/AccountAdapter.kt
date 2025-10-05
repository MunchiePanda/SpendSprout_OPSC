package com.example.spendsprout_opsc.accounts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.model.Account

class AccountAdapter(
    private var accounts: List<Account>,
    private val onItemClick: (Account) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    class AccountViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_AccountName)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val limitTextView: TextView = view.findViewById(R.id.txt_Limit)
        val transaction1TextView: TextView = view.findViewById(R.id.txt_Transaction1)
        val transaction2TextView: TextView = view.findViewById(R.id.txt_Transaction2)
        val transaction3TextView: TextView = view.findViewById(R.id.txt_Transaction3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_layout, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.nameTextView.text = account.name
        holder.balanceTextView.text = account.balance
        holder.limitTextView.text = account.limit
        
        // Set recent transactions
        account.recentTransactions.forEachIndexed { index, transaction ->
            when (index) {
                0 -> {
                    holder.transaction1TextView.text = "${transaction.description} ${transaction.amount}"
                    holder.transaction1TextView.setTextColor(android.graphics.Color.parseColor(transaction.color))
                }
                1 -> {
                    holder.transaction2TextView.text = "${transaction.description} ${transaction.amount}"
                    holder.transaction2TextView.setTextColor(android.graphics.Color.parseColor(transaction.color))
                }
                2 -> {
                    holder.transaction3TextView.text = "${transaction.description} ${transaction.amount}"
                    holder.transaction3TextView.setTextColor(android.graphics.Color.parseColor(transaction.color))
                }
            }
        }
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(account) }
    }

    override fun getItemCount(): Int = accounts.size
    
    fun updateData(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}

