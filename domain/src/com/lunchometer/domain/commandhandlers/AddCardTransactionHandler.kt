package com.lunchometer.domain.commandhandlers

import com.lunchometer.avro.AddCardTransaction
import com.lunchometer.avro.CardTransactionAdded
import com.lunchometer.avro.CardTransactionRetrievalRequested
import com.lunchometer.avro.CommandResponse
import com.lunchometer.avro.DomainAggregate
import com.lunchometer.avro.EventHeader
import com.lunchometer.avro.TransactionMarkedAsLunch
import com.lunchometer.avro.TransactionMarkedAsNotLunch
import com.lunchometer.shared.localDatetimeToMillis
import com.lunchometer.shared.millisToLocalDateTime
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

fun handleAddCardTransaction(events: List<Any>, command: AddCardTransaction): DomainAggregate {

    val timestamp = localDatetimeToMillis(LocalDateTime.now())

    return when(transactionsHaveBeenRequested(events)) {
        false -> DomainAggregate(events, listOf(), CommandResponse(command.header.id, false))
        true -> {
            val transactionAdded = CardTransactionAdded(
                EventHeader(
                    UUID.randomUUID().toString(),
                    command.header.userId,
                    timestamp
                ),
                command.transaction)
            val markedAs = if (millisToLocalDateTime(command.transaction.created).isWeekdayLunchtime())
                TransactionMarkedAsLunch(
                    EventHeader(
                        UUID.randomUUID().toString(),
                        command.header.userId,
                        timestamp
                    ),
                    command.transaction.id
                )
                else TransactionMarkedAsNotLunch(
                    EventHeader(
                        UUID.randomUUID().toString(),
                        command.header.userId,
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
                    ),
                    command.transaction.id
                )

            return DomainAggregate(
                events.plus(transactionAdded).plus(markedAs),
                listOf(transactionAdded, markedAs),
                CommandResponse(command.header.id, true)
            )
        }
    }
}

private fun transactionsHaveBeenRequested(events: List<Any>): Boolean =
    events.any { it is CardTransactionRetrievalRequested }

private fun LocalDateTime.isWeekdayLunchtime(): Boolean {
    val zone = ZoneId.of("Europe/London")
    val zonedDateTime = ZonedDateTime.of(this, zone)
    return zonedDateTime.dayOfWeek.isWeekday() && zonedDateTime.toLocalTime().isLunchTime()
}


private fun DayOfWeek.isWeekday(): Boolean =
    when(this) {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> true
        else -> false
    }

private fun LocalTime.isLunchTime(): Boolean =
    this.isAfter(LocalTime.of(11,59,59)) && this.isBefore(LocalTime.of(14, 0, 0))
