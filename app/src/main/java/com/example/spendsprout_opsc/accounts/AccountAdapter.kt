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
    private val accounts: List<Account>,
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
        holder.balanceTextView.text = account.balance
        holder.limitTextView.text = account.limit
        
        // Set recent transactions with proper formatting
        account.recentTransactions.forEachIndexed { index, transaction ->
            when (index) {
                0 -> {
                    holder.transaction1TextView.text = transaction.description
                    holder.amount1TextView.text = transaction.amount
                    holder.amount1TextView.setTextColor(android.graphics.Color.parseColor(transaction.color))
                    holder.colorIndicator1.setBackgroundColor(android.graphics.Color.parseColor(transaction.color))
                }
                1 -> {
                    holder.transaction2TextView.text = transaction.description
                    holder.amount2TextView.text = transaction.amount
                    holder.amount2TextView.setTextColor(android.graphics.Color.parseColor(transaction.color))
                    holder.colorIndicator2.setBackgroundColor(android.graphics.Color.parseColor(transaction.color))
                }
                2 -> {
                    holder.transaction3TextView.text = transaction.description
                    holder.amount3TextView.text = transaction.amount
                    holder.amount3TextView.setTextColor(android.graphics.Color.parseColor(transaction.color))
                    holder.colorIndicator3.setBackgroundColor(android.graphics.Color.parseColor(transaction.color))
                }
            }
        }
        
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
}

