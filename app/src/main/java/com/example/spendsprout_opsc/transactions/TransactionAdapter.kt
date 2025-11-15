package com.example.spendsprout_opsc.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var transactions: List<Expense>,
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.txt_Date)
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateTextView.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(transaction.date))
        holder.nameTextView.text = transaction.notes
        holder.amountTextView.text = String.format("R %.2f", transaction.amount)

        // Set amount color based on positive/negative
        if (transaction.amount >= 0) {
            holder.amountTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.amountTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size
    
    fun submitList(newTransactions: List<Expense>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
