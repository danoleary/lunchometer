package com.lunchometer.api.readmodels

import com.lunchometer.shared.Event

fun filterTransactionEvents(events: List<Event>) =
    events.filter {
        it.type != Event.CardTransactionRetrievalRequested::class.java.simpleName
    }