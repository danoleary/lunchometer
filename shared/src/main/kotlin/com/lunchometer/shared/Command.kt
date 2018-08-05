package com.lunchometer.shared

import java.util.*

sealed class Command(val type: String, val id: UUID) {
    data class RetrieveCardTransactions(val userId: String): Command(Command.RetrieveCardTransactions::class.java.simpleName, UUID.randomUUID())
    data class AddCardTransaction(val userId: String, val transaction: Transaction): Command(Command.AddCardTransaction::class.java.simpleName, UUID.randomUUID())
    data class MarkTransactionAsLunch(val userId: String, val transactionId: UUID): Command(Command.MarkTransactionAsLunch::class.java.simpleName, UUID.randomUUID())
    data class MarkTransactionAsNotLunch(val userId: String, val transactionId: UUID): Command(Command.MarkTransactionAsNotLunch::class.java.simpleName, UUID.randomUUID())
    data class AddCustomTransaction(val userId: String, val transaction: Transaction): Command(Command.AddCustomTransaction::class.java.simpleName, UUID.randomUUID())
    data class RemoveCustomTransaction(val userId: String, val transactionId: UUID): Command(Command.RemoveCustomTransaction::class.java.simpleName, UUID.randomUUID())
}