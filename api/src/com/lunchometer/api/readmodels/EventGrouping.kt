package com.lunchometer.api.readmodels

import com.lunchometer.shared.Event
import java.util.*

fun groupEventByTransactionId(events: List<Event>): Map<UUID, List<Event>>  {
    val transactionEvents = filterTransactionEvents(events)
    val ids = transactionEvents.map {
        when (it.type) {
            Event.CardTransactionAdded::class.java.simpleName -> {
                Pair((it as Event.CardTransactionAdded).transaction.id, it)
            }
            Event.TransactionMarkedAsLunch::class.java.simpleName -> {
                Pair((it as Event.TransactionMarkedAsLunch).id, it)
            }
            Event.TransactionMarkedAsNotLunch::class.java.simpleName -> {
                Pair((it as Event.TransactionMarkedAsNotLunch).id, it)
            }
            else -> throw Exception()
        }
    }
    val groupedEvents = ids.groupBy { it.first }.mapValues { it.value.map { v -> v.second } }
    return groupedEvents
}