package com.example.racunko

import com.example.racunko.model.Transaction
import java.util.Date

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        description = this.description,
        category = this.category,
        isExpense = this.isExpense,
        date = Date(this.timestamp)
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        amount = this.amount,
        description = this.description,
        category = this.category,
        isExpense = this.isExpense,
        timestamp = this.date.time
    )
}
