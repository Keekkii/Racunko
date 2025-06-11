package com.example.racunko.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.racunko.R
import com.example.racunko.adapter.TransactionAdapter
import com.example.racunko.model.Transaction
import android.content.Context.MODE_PRIVATE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var tvBudgetAmount: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var btnSetBudget: Button
    private lateinit var tvIncome: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var btnViewAll: Button

    // Sample data (in a real app, you'd use Room database)
    private val transactions = mutableListOf<Transaction>(
        Transaction(1, 45.75, "Grocery Shopping", "Food", true, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2025-05-10")!!),
        Transaction(2, 120.00, "Electricity Bill", "Utilities", true, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2025-05-05")!!),
        Transaction(3, 1000.00, "Salary", "Income", false, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2025-05-01")!!)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        tvBudgetAmount = view.findViewById(R.id.tv_budget_amount)
        tvRemaining = view.findViewById(R.id.tv_remaining)
        progressBudget = view.findViewById(R.id.progress_budget)
        btnSetBudget = view.findViewById(R.id.btn_set_budget)
        tvIncome = view.findViewById(R.id.tv_income)
        tvExpenses = view.findViewById(R.id.tv_expenses)
        rvTransactions = view.findViewById(R.id.rv_transactions)
        btnViewAll = view.findViewById(R.id.btn_view_all)

        // Load saved budget from SharedPreferences
        val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
        val budget = prefs.getFloat("budget", 0.0f)

        // Set budget display
        tvBudgetAmount.text = "$${budget}"
        updateBudgetProgress()

        // Set up RecyclerView
        rvTransactions.layoutManager = LinearLayoutManager(context)
        rvTransactions.adapter = TransactionAdapter(transactions.take(3)) { transaction ->
            // Handle transaction click
            Toast.makeText(context, "Clicked: ${transaction.description}", Toast.LENGTH_SHORT).show()
        }

        // Update income and expense totals
        updateFinancialSummary()

        // Set up buttons
        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        btnViewAll.setOnClickListener {
            // Navigate to transactions list fragment (implement later)
            Toast.makeText(context, "View all transactions", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSetBudgetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Set Monthly Budget")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
        val currentBudget = prefs.getFloat("budget", 0.0f)
        input.setText(currentBudget.toString())

        builder.setPositiveButton("Save") { _, _ ->
            try {
                val newBudget = input.text.toString().toFloat()
                prefs.edit().putFloat("budget", newBudget).apply()
                tvBudgetAmount.text = "$$newBudget"
                updateBudgetProgress()
                Toast.makeText(context, "Budget updated", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updateBudgetProgress() {
        val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
        val budget = prefs.getFloat("budget", 0.0f)

        // Calculate total expenses (only those marked as expenses)
        val totalExpenses = transactions
            .filter { it.isExpense }
            .sumOf { it.amount }

        val remaining = budget - totalExpenses
        tvRemaining.text = "Remaining: $${remaining}"

        // Update progress bar
        if (budget > 0) {
            val percentage = ((totalExpenses / budget) * 100).toInt()
            progressBudget.progress = percentage.coerceAtMost(100)
        } else {
            progressBudget.progress = 0
        }
    }

    private fun updateFinancialSummary() {
        val totalIncome = transactions
            .filter { !it.isExpense }
            .sumOf { it.amount }

        val totalExpenses = transactions
            .filter { it.isExpense }
            .sumOf { it.amount }

        tvIncome.text = "$$totalIncome"
        tvExpenses.text = "$$totalExpenses"
    }
}
