package com.lunchometer.api.readmodels

import com.lunchometer.shared.Event

fun isLunchTransaction(events: List<Event>): Boolean {
    if(events.isEmpty()) {
        throw Exception()
    }
    return handleMultipleEvents(events)
}

private fun handleMultipleEvents(events: List<Event>): Boolean {
    val transactionEvents = filterTransactionEvents(events)

    if(!allIdsMatch(transactionEvents)) {
        throw Exception()
    }

    val latestMarkedAsLunchEvent = getLatestMarkedAsLunch(transactionEvents)
    val latestMarkedAsNotLunchEvent = getLatestMarkedAsNotLunch(transactionEvents)

    if(latestMarkedAsLunchEvent == null) {
        return false
    }

    if(latestMarkedAsNotLunchEvent == null) {
        return true
    }

    if(latestMarkedAsLunchEvent.timestamp >= latestMarkedAsNotLunchEvent.timestamp) {
        return true
    }

    return false
}

private fun allIdsMatch(events: List<Event>): Boolean {
    val ids = events.map {
        when (it.type) {
            Event.CardTransactionAdded::class.java.simpleName ->
                (it as Event.CardTransactionAdded).transaction.id
            Event.TransactionMarkedAsLunch::class.java.simpleName ->
                (it as Event.TransactionMarkedAsLunch).id
            Event.TransactionMarkedAsNotLunch::class.java.simpleName ->
                (it as Event.TransactionMarkedAsNotLunch).id
            else -> throw Exception()
        }
    }
    return ids.distinct().size == 1
}

private fun getLatestMarkedAsLunch(events: List<Event>): Event.TransactionMarkedAsLunch? {
    val markedAsLunchEvents = events
        .filter { it.type == Event.TransactionMarkedAsLunch::class.java.simpleName }
        .map { it as Event.TransactionMarkedAsLunch }
    return markedAsLunchEvents.maxBy { it.timestamp }
}

private fun getLatestMarkedAsNotLunch(events: List<Event>): Event.TransactionMarkedAsNotLunch? {
    val markedAsNotLunchEvents = events
        .filter { it.type == Event.TransactionMarkedAsNotLunch::class.java.simpleName }
        .map { it as Event.TransactionMarkedAsNotLunch }
    return markedAsNotLunchEvents.maxBy { it.timestamp }
}
