package com.example.spendsprout_opsc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // Define a data class to represent a transaction
    data class Transaction(
        val date: String,
        val name: String,
        val amount: Double,
        val category: String
    )

    // ViewHolder class to hold references to views
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txt_Date)
        val txtName: TextView = itemView.findViewById(R.id.txt_Name)
        val txtAmount: TextView = itemView.findViewById(R.id.txt_Amount)
        val imgCategory: ImageView = itemView.findViewById(R.id.img_Category)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout, parent, false)
        return TransactionViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set text values
        holder.txtDate.text = transaction.date
        holder.txtName.text = transaction.name
        holder.txtAmount.text = "R${"%.2f".format(transaction.amount)}"

        // Change amount color based on its sign
        val amountColor = if (transaction.amount >= 0) {
            R.color.PositiveBalanceColor
        } else {
            R.color.NegativeBalanceColor
        }
        holder.txtAmount.setTextColor(ContextCompat.getColor(holder.itemView.context, amountColor))

        // Tint category image based on the category
        val categoryColor = when (transaction.category) {
            "Needs" -> R.color.NeedsCategoryColor
            "Wants" -> R.color.WantsCategoryColor
            "Savings" -> R.color.SavingsCategoryColor
            else -> R.color.IconColor // Default color
        }
        holder.imgCategory.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, categoryColor)
        )
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = transactions.size
}
