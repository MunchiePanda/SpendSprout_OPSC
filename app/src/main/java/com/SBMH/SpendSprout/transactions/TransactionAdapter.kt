package com.SBMH.SpendSprout.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Expense

class TransactionAdapter(
    private var transactions: List<Expense>,
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
        holder.itemView.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Expense>) {
        this.transactions = newTransactions
        notifyDataSetChanged()
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val notesTextView: TextView = itemView.findViewById(R.id.txt_notes)
        private val amountTextView: TextView = itemView.findViewById(R.id.txt_amount)
        private val categoryTextView: TextView = itemView.findViewById(R.id.txt_category)

        fun bind(expense: Expense) {
            notesTextView.text = expense.notes
            amountTextView.text = expense.amount.toString()
            categoryTextView.text = expense.category
        }
    }
}
