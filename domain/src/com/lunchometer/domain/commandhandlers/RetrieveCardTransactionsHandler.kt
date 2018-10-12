package com.lunchometer.domain.commandhandlers

import com.lunchometer.avro.CardTransactionRetrievalRequested
import com.lunchometer.avro.CommandResponse
import com.lunchometer.avro.DomainAggregate
import com.lunchometer.avro.EventHeader
import com.lunchometer.avro.RetrieveCardTransactions
import org.apache.avro.specific.SpecificRecord
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun handleRetrieveCardTransactions(events: List<Any>, command: RetrieveCardTransactions): DomainAggregate =
    if(events.any{it is CardTransactionRetrievalRequested}) {
        DomainAggregate(
            events,
            listOf(),
            CommandResponse(command.header.id, false)
        )
    } else {
        val newEvent = CardTransactionRetrievalRequested(
            EventHeader(
                UUID.randomUUID().toString(),
                command.header.userId,
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        )
        DomainAggregate(
            events.plus(newEvent),
            listOf(newEvent),
            CommandResponse(command.header.id, true)
        )
    }
