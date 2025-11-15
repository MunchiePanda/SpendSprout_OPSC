package com.SBMH.SpendSprout.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Account

class AccountSummaryAdapter(
    private var accounts: List<Account>,
    private val onItemClick: (Account) -> Unit
) : RecyclerView.Adapter<AccountSummaryAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_layout, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.bind(account)
        holder.itemView.setOnClickListener { onItemClick(account) }
    }

    override fun getItemCount(): Int = accounts.size

    fun updateData(newAccounts: List<Account>) {
        this.accounts = newAccounts
        notifyDataSetChanged()
    }

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val accountNameTextView: TextView = itemView.findViewById(R.id.txt_AccountName)
        private val accountBalanceTextView: TextView = itemView.findViewById(R.id.txt_Balance)

        fun bind(account: Account) {
            accountNameTextView.text = account.accountName
            accountBalanceTextView.text = account.accountBalance.toString()
        }
    }
}
