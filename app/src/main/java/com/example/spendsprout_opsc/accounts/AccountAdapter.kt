package com.example.spendsprout_opsc.accounts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.accounts.model.Account
import com.example.spendsprout_opsc.edit.EditAccountActivity

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
        val amount1TextView: TextView = view.findViewById(R.id.txt_Amount1)
        val amount2TextView: TextView = view.findViewById(R.id.txt_Amount2)
        val amount3TextView: TextView = view.findViewById(R.id.txt_Amount3)
        val colorIndicator1: View = view.findViewById(R.id.color_indicator_1)
        val colorIndicator2: View = view.findViewById(R.id.color_indicator_2)
        val colorIndicator3: View = view.findViewById(R.id.color_indicator_3)
        val editButton: ImageButton = view.findViewById(R.id.btn_Edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_layout, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.nameTextView.text = account.name
        holder.balanceTextView.text = "R ${String.format("%.0f", account.balance)}"
        holder.limitTextView.text = "R ${String.format("%.0f", account.balance * 1.5)}" // Mock limit
        
        // Clear transaction fields since we don't have recent transactions in the Account model
        holder.transaction1TextView.text = ""
        holder.transaction2TextView.text = ""
        holder.transaction3TextView.text = ""
        holder.amount1TextView.text = ""
        holder.amount2TextView.text = ""
        holder.amount3TextView.text = ""
        
        // Set click listener for edit button
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.view.context, EditAccountActivity::class.java)
            intent.putExtra("accountId", account.id)
            intent.putExtra("isEdit", true)
            holder.view.context.startActivity(intent)
        }
        
        // Set click listener for item
        holder.view.setOnClickListener { onItemClick(account) }
    }

    override fun getItemCount(): Int = accounts.size
    
    fun updateData(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}

