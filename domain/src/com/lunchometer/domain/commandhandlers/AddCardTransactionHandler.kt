package com.lunchometer.domain.commandhandlers

import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import com.lunchometer.shared.InternalCommandResponse
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

fun handle(events: List<Event>, command: Command.AddCardTransaction): InternalCommandResponse {

    val transactionCanBeAdded =
        transactionsHaveBeenRequested(events)
        && !transactionAlreadyAdded(events, command.transaction.id)

    val timestamp = LocalDateTime.now()

    return when(transactionCanBeAdded) {
        false -> InternalCommandResponse(command.id, false, listOf())
        true -> {
            val transactionAdded = Event.CardTransactionAdded(command.userId, timestamp, command.transaction)
            val markedAs = when (command.transaction.created.isWeekdayLunchtime()) {
                true -> Event.TransactionMarkedAsLunch(command.userId, timestamp, transactionAdded.transaction.id)
                false -> Event.TransactionMarkedAsNotLunch(command.userId, timestamp, transactionAdded.transaction.id)
            }
            return InternalCommandResponse(command.id, true, listOf(transactionAdded, markedAs))
        }
    }
}

private fun transactionAlreadyAdded(events: List<Event>, commandId: UUID): Boolean {
    return events
        .filter { it.type == Event.CardTransactionAdded::class.java.simpleName }
        .any { (it as Event.CardTransactionAdded).transaction.id == commandId }
}

private fun transactionsHaveBeenRequested(events: List<Event>): Boolean {
    return events.any { it.type == Event.CardTransactionRetrievalRequested::class.java.simpleName }
}

private fun LocalDateTime.isWeekdayLunchtime() =
    this.dayOfWeek.isWeekday() && this.toLocalTime().isLunchTime()

private fun DayOfWeek.isWeekday() =
    when(this) {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> true
        else -> false
    }

private fun LocalTime.isLunchTime() =
    this.isAfter(LocalTime.of(12,0)) && this.isBefore(LocalTime.of(14, 0))