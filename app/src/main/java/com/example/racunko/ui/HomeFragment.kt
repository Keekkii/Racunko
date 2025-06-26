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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModelProvider
import android.widget.RadioGroup
import android.widget.Spinner


class HomeFragment : Fragment() {

    private lateinit var tvBudgetAmount: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var btnSetBudget: Button
    private lateinit var tvIncome: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var btnViewAll: Button
    private lateinit var viewModel: TransactionViewModel
    private lateinit var btnAddTransaction: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun showAddTransactionDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.transaction_input_dialog, null)

        val etAmount = dialogView.findViewById<EditText>(R.id.et_amount)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_description)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val rgType = dialogView.findViewById<RadioGroup>(R.id.rg_type)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        // Set up category spinner
        val categories = resources.getStringArray(R.array.categories_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnConfirm.setOnClickListener {
            val amountText = etAmount.text.toString()
            val description = etDescription.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val isExpense = rgType.checkedRadioButtonId == R.id.rb_expense

            if (amountText.isNotBlank() && description.isNotBlank()) {
                val amount = amountText.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    viewModel.insert(
                        Transaction(
                            amount = amount,
                            description = description,
                            category = category,
                            isExpense = isExpense
                        )
                    )
                    Toast.makeText(context, "Transaction Added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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

        // Set up RecyclerView
        rvTransactions.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            rvTransactions.adapter = TransactionAdapter(list.take(3)) { transaction ->
                Toast.makeText(context, "Clicked: ${transaction.description}", Toast.LENGTH_SHORT).show()
            }

            updateBudgetProgress(list)
            updateFinancialSummary(list)
        }


        // Update income and expense totals

        // Set up buttons
        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        btnViewAll.setOnClickListener {
            // Navigate to transactions list fragment (implement later)
            Toast.makeText(context, "View all transactions", Toast.LENGTH_SHORT).show()
        }

        btnAddTransaction = view.findViewById(R.id.btn_add_transaction)

        btnAddTransaction.setOnClickListener {
            showAddTransactionDialog()
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

    private fun updateBudgetProgress(transactions: List<Transaction>) {
        val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
        val budget = prefs.getFloat("budget", 0.0f)

        val totalExpenses = transactions
            .filter { it.isExpense }
            .sumOf { it.amount }

        val remaining = budget - totalExpenses
        tvRemaining.text = "Remaining: $${remaining}"

        if (budget > 0) {
            val percentage = ((totalExpenses / budget) * 100).toInt()
            progressBudget.progress = percentage.coerceAtMost(100)
        } else {
            progressBudget.progress = 0
        }
    }

    private fun updateFinancialSummary(transactions: List<Transaction>) {
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
