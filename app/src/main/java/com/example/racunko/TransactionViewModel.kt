package com.example.racunko.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import androidx.lifecycle.Observer
import com.example.racunko.AppDatabase
import com.example.racunko.toEntity
import com.example.racunko.toTransaction
import com.example.racunko.model.Transaction
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).transactionDao()

    // Live list of transactions from the database, automatically updating the UI
    val transactions: LiveData<List<Transaction>> = dao.getAllTransactions()
        .map { list -> list.map { it.toTransaction() } }
        .asLiveData()

    // Insert a new transaction into the database
    fun insert(transaction: Transaction) {
        viewModelScope.launch {
            dao.insertTransaction(transaction.toEntity())
        }
    }
}
