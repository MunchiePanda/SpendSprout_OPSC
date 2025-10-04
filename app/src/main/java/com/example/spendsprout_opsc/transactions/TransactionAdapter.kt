package com.example.spendsprout_opsc.transactions

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.edit.EditTransactionActivity
import com.example.spendsprout_opsc.transactions.model.Transaction

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.txt_Date)
        val descriptionTextView: TextView = view.findViewById(R.id.txt_Description)
        val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
        val colorIndicator: View = view.findViewById(R.id.color_indicator)
        val editButton: ImageButton = view.findViewById(R.id.btn_Edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        
        // Format date
        val date = java.util.Date(transaction.date)
        val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
        holder.dateTextView.text = formatter.format(date)
        
        holder.descriptionTextView.text = transaction.name
        
        // Format amount with sign
        val sign = if (transaction.type.name == "Income") "+" else "-"
        holder.amountTextView.text = "$sign R ${String.format("%.0f", transaction.amount)}"
        
        // Set color indicator (mock color based on subcategory ID)
        val colors = listOf(android.graphics.Color.parseColor("#FF6B6B"), 
                           android.graphics.Color.parseColor("#FFB6C1"), 
                           android.graphics.Color.parseColor("#9370DB"), 
                           android.graphics.Color.parseColor("#4ECDC4"), 
                           android.graphics.Color.parseColor("#45B7D1"))
        holder.colorIndicator.setBackgroundColor(colors[transaction.subcategoryId % colors.size])
        
        // Set amount color based on positive/negative
        if (transaction.type.name == "Income") {
            holder.amountTextView.setTextColor(android.graphics.Color.parseColor("#77B950"))
        } else {
            holder.amountTextView.setTextColor(android.graphics.Color.parseColor("#E94444"))
        }
        
        // Set click listener for edit button
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.view.context, EditTransactionActivity::class.java)
            intent.putExtra("transactionId", transaction.id)
            intent.putExtra("isEdit", true)
            holder.view.context.startActivity(intent)
        }
        
        // Set click listener for item
        holder.view.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size
    
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}

