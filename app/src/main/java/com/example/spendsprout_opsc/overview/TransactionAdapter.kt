package com.example.spendsprout_opsc.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.model.Transaction

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.txt_Date)
        val descriptionTextView: TextView = view.findViewById(R.id.txt_Description)
        val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
        val colorIndicator: View = view.findViewById(R.id.color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateTextView.text = transaction.date
        holder.descriptionTextView.text = transaction.description
        holder.amountTextView.text = transaction.amount
        
        // Set color indicator
        holder.colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(transaction.color))
    }

    override fun getItemCount(): Int = transactions.size
}

