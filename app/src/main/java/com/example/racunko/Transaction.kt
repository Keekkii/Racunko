package com.example.racunko.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val isExpense: Boolean = true,
    val date: Date = Date()
)
