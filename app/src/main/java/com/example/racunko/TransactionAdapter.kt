package com.example.racunko.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.racunko.R
import com.example.racunko.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryIcon: ImageView = view.findViewById(R.id.iv_category)
        val description: TextView = view.findViewById(R.id.tv_description)
        val date: TextView = view.findViewById(R.id.tv_date)
        val amount: TextView = view.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.description.text = transaction.description

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.date.text = dateFormat.format(transaction.date)

        val prefix = if (transaction.isExpense) "-$" else "+$"
        holder.amount.text = "$prefix${transaction.amount}"
        holder.amount.setTextColor(
            holder.itemView.context.getColor(
                if (transaction.isExpense) android.R.color.holo_red_dark
                else android.R.color.holo_green_dark
            )
        )

        // Set icon based on category (you can expand this)
        // For now, using the default icon

        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }
    }

    override fun getItemCount() = transactions.size
}
