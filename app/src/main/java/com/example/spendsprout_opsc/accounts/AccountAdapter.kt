package com.example.spendsprout_opsc.accounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.databinding.ItemAccountBinding
import com.example.spendsprout_opsc.model.Account

class AccountAdapter(
    private var accounts: List<Account>,
    private val onAccountClick: (Account) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.bind(account)
    }

    override fun getItemCount(): Int = accounts.size

    fun updateData(newAccounts: List<Account>) {
        this.accounts = newAccounts
        notifyDataSetChanged()
    }

    inner class AccountViewHolder(private val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(account: Account) {
            binding.account = account
            binding.accountBalance.text = String.format("R %.2f", account.accountBalance) // Example format
            binding.root.setOnClickListener {
                onAccountClick(account)
            }
            binding.executePendingBindings()
        }
    }
}
