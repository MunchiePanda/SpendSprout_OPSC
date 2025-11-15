package com.example.spendsprout_opsc.accounts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Account
import com.example.spendsprout_opsc.edit.EditAccountActivity

class AccountAdapter(
    private var accounts: List<Account>,
    private val onItemClick: (Account) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    class AccountViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txt_AccountName)
        val balanceTextView: TextView = view.findViewById(R.id.txt_Balance)
        val editButton: ImageButton = view.findViewById(R.id.btn_Edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_layout, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.nameTextView.text = account.accountName
        holder.balanceTextView.text = "R ${String.format("%.2f", account.accountBalance)}"

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.view.context, EditAccountActivity::class.java)
            intent.putExtra("accountId", account.id)
            holder.view.context.startActivity(intent)
        }

        holder.view.setOnClickListener { onItemClick(account) }
    }

    override fun getItemCount(): Int = accounts.size

    fun updateData(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}
