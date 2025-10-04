package com.example.spendsprout_opsc.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.overview.model.Transaction

/**
 * TransactionAdapter - UI List Management Script
 * 
 * This is like Unity's UI List management or custom prefab instantiation system.
 * Similar to Unity's UI List with custom prefabs or object pooling.
 * 
 * Responsibilities:
 * - Create transaction list items (like Unity's Instantiate() for UI elements)
 * - Bind data to UI components (like Unity's UI Text updates)
 * - Handle item clicks (like Unity's Button.onClick events)
 * - Manage list scrolling (like Unity's ScrollView or UI List)
 */
class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.txt_Date)
        val descriptionTextView: TextView = view.findViewById(R.id.txt_Description)
        val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
        val colorIndicator: View = view.findViewById(R.id.color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateTextView.text = transaction.date
        holder.descriptionTextView.text = transaction.description
        holder.amountTextView.text = transaction.amount
        
        // Set color indicator
        holder.colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(transaction.color))
        
        // Set click listener
        holder.view.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size
    
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}

