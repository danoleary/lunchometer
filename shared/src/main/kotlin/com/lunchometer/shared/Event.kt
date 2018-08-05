package com.lunchometer.shared

import java.util.*

sealed class Event(val type: String) {
    data class CardTransactionRetrievalRequested(val userId: String): Event(Event.CardTransactionRetrievalRequested::class.java.simpleName)
    data class CardTransactionAdded(val userId: String, val transaction: Transaction): Event(Event.CardTransactionAdded::class.java.simpleName)
    data class TransactionMarkedAsLunch(val userId: String, val id: UUID): Event(Event.TransactionMarkedAsLunch::class.java.simpleName)
    data class TransactionMarkedAsNotLunch(val userId: String, val id: UUID): Event(Event.TransactionMarkedAsNotLunch::class.java.simpleName)
    data class CustomTransactionAdded(val userId: String, val transaction: Transaction): Event(Event.CustomTransactionAdded::class.java.simpleName)
    data class CustomTransactionRemoved(val userId: String, val id: UUID): Event(Event.CustomTransactionRemoved::class.java.simpleName)
}