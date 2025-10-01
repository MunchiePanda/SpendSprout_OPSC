package com.example.spendsprout_opsc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.Repository.Transaction

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<Transaction> = emptyList()

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.textTransactionDate)
        val description: TextView = itemView.findViewById(R.id.textTransactionDescription)
        val amount: TextView = itemView.findViewById(R.id.textTransactionAmount)
        val categoryIndicator: View = itemView.findViewById(R.id.viewCategoryIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.date.text = transaction.date
        holder.description.text = transaction.description

        // Format amount with color
        val amountText = if (transaction.amount >= 0) {
            "+R${"%.2f".format(transaction.amount)}"
        } else {
            "-R${"%.2f".format(-transaction.amount)}"
        }
        holder.amount.text = amountText

        // Set text color based on amount
        val color = if (transaction.amount >= 0) {
            R.color.green
        } else {
            R.color.red
        }
        holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.context, color))

        // Set category indicator color
        val indicatorColor = when (transaction.category) {
            "Needs" -> R.color.orange
            "Wants" -> R.color.pink
            "Savings" -> R.color.purple
            else -> R.color.gray
        }
        holder.categoryIndicator.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, indicatorColor)
        )
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
