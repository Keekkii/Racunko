package com.example.racunko.ui

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.racunko.R
import com.example.racunko.model.Transaction
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.animation.ObjectAnimator
import android.view.animation.AnimationUtils



class SubscriptionFragment : Fragment() {

    private lateinit var tvBudget: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var btnSetBudget: Button
    private lateinit var viewModel: TransactionViewModel
    private lateinit var circularProgressBudget: CircularProgressIndicator
    private lateinit var tvBudgetStatus: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvBudget = view.findViewById(R.id.tv_budget)
        tvExpenses = view.findViewById(R.id.tv_expenses)
        tvRemaining = view.findViewById(R.id.tv_remaining)
        progressBudget = view.findViewById(R.id.progress_budget)
        btnSetBudget = view.findViewById(R.id.btn_set_budget)
        circularProgressBudget = view.findViewById(R.id.circular_progress_budget)
        tvBudgetStatus = view.findViewById(R.id.tv_budget_status)

        val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
        val budget = prefs.getFloat("budget", 0.0f)
        tvBudget.text = "Monthly Budget: $$budget"

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateBudgetProgress(transactions)
        }

        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }
    }

    private fun animateProgress(targetProgress: Int) {
        val animator = ObjectAnimator.ofInt(circularProgressBudget, "progress", circularProgressBudget.progress, targetProgress)
        animator.duration = 800 // Animation duration in ms
        animator.start()
    }


    private fun updateBudgetProgress(transactions: List<Transaction>) {
        val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
        val budget = prefs.getFloat("budget", 0.0f)

        val totalExpenses = transactions
            .filter { it.isExpense }
            .sumOf { it.amount }

        val remaining = budget - totalExpenses

        tvBudget.text = "Monthly Budget: $$budget"
        tvExpenses.text = "Total Expenses: $$totalExpenses"
        tvRemaining.text = "Remaining: $$remaining"

        if (budget > 0) {
            val percentage = ((totalExpenses / budget) * 100).toInt().coerceAtMost(100)
            animateProgress(percentage)

            // Color and status based on budget usage
            when {
                percentage < 80 -> {
                    circularProgressBudget.setIndicatorColor(requireContext().getColor(android.R.color.holo_green_dark))
                    tvBudgetStatus.text = "You're doing great! ✅"
                    tvBudgetStatus.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
                    tvRemaining.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
                }
                percentage in 80..99 -> {
                    circularProgressBudget.setIndicatorColor(requireContext().getColor(android.R.color.holo_orange_dark))
                    tvBudgetStatus.text = "Careful, almost there! ⚠️"
                    tvBudgetStatus.setTextColor(requireContext().getColor(android.R.color.holo_orange_dark))
                    tvRemaining.setTextColor(requireContext().getColor(android.R.color.holo_orange_dark))
                }
                else -> {
                    circularProgressBudget.setIndicatorColor(requireContext().getColor(android.R.color.holo_red_dark))
                    tvBudgetStatus.text = "Budget exceeded! 🔥"
                    tvBudgetStatus.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
                    tvRemaining.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
                }
            }

        } else {
            circularProgressBudget.progress = 0
            tvBudgetStatus.text = "Set your budget to start tracking."
        }
    }


    private fun showSetBudgetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Set Monthly Budget")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            try {
                val newBudget = input.text.toString().toFloat()
                val prefs = requireActivity().getSharedPreferences("finance", MODE_PRIVATE)
                prefs.edit().putFloat("budget", newBudget).apply()
                tvBudget.text = "Monthly Budget: $$newBudget"
                Toast.makeText(context, "Budget updated", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
