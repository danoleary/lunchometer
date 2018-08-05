package com.lunchometer.api.commandhandlers

import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

fun handle(events: List<Event>, command: Command.AddCardTransaction): InternalCommandResponse {

    val transactionAlreadyAdded = events
        .filter { it.type == Event.CardTransactionAdded::class.java.simpleName }
        .any { (it as Event.CardTransactionAdded).transaction.id == command.transaction.id }

    return when(transactionAlreadyAdded) {
        true -> InternalCommandResponse(command.id, false, listOf())
        false -> {
            val transactionAdded = Event.CardTransactionAdded(command.userId, command.transaction)
            val markedAs = if (command.transaction.created.isWeekdayLunchtime())
                Event.TransactionMarkedAsLunch(command.userId, transactionAdded.transaction.id) else
                Event.TransactionMarkedAsNotLunch(command.userId, transactionAdded.transaction.id)
            return InternalCommandResponse(command.id, true, listOf(transactionAdded, markedAs))
        }
    }
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