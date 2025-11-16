package com.example.spendsprout_opsc.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.view.ViewGroup.LayoutParams
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.model.Transaction

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.txt_Date)
        val nameTextView: TextView = view.findViewById(R.id.txt_Name)
        val amountTextView: TextView = view.findViewById(R.id.txt_Amount)
        val spentTextView: TextView = view.findViewById(R.id.txt_Spent)
        val receiptImageView: ImageView = view.findViewById(R.id.img_Receipt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_layout, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateTextView.text = transaction.date.toString()
        holder.nameTextView.text = transaction.description
        holder.amountTextView.text = transaction.amount.toString()
        holder.spentTextView.text = ""

        // Show receipt image if present (from file path/URI string)
        if (!transaction.photo.isNullOrBlank()) {
            try {
                val bmp = BitmapFactory.decodeFile(transaction.photo)
                if (bmp != null) {
                    holder.receiptImageView.setImageBitmap(bmp)
                    holder.receiptImageView.visibility = View.VISIBLE
                    holder.receiptImageView.layoutParams.height = LayoutParams.WRAP_CONTENT
                } else {
                    holder.receiptImageView.visibility = View.GONE
                }
            } catch (e: Exception) {
                holder.receiptImageView.visibility = View.GONE
            }
        } else {
            holder.receiptImageView.visibility = View.GONE
        }
        
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
    
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}

