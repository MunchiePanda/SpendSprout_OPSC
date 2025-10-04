package com.example.spendsprout_opsc.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.transactions.model.Transaction

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.txt_Date)
        //val descriptionTextView: TextView = view.findViewById(R.id.txt_Description)
        val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
        //val colorIndicator: View = view.findViewById(R.id.color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateTextView.text = transaction.date
        //holder.descriptionTextView.text = transaction.description
        holder.amountTextView.text = transaction.amount
        
        // Set color indicator
        //holder.colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(transaction.color))
        
        // Set amount color based on positive/negative
        if (transaction.amount.startsWith("+")) {
            holder.amountTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.amountTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size
    
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}

