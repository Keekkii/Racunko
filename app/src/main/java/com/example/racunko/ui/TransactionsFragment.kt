package com.example.racunko.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.racunko.R
import com.example.racunko.adapter.TransactionAdapter
import com.example.racunko.model.Transaction

class TransactionsFragment : Fragment() {

    private lateinit var rvAllTransactions: RecyclerView
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAllTransactions = view.findViewById(R.id.rv_all_transactions)
        rvAllTransactions.layoutManager = LinearLayoutManager(context)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            rvAllTransactions.adapter = TransactionAdapter(list) { transaction ->
                Toast.makeText(context, "Clicked: ${transaction.description}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
