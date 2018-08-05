package com.lunchometer.api

import com.lunchometer.shared.Event
import com.lunchometer.shared.Transaction
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*

data class TransactionDto(val id: UUID, val amount: BigDecimal, val date: String, val location: String)

data class TransactionWeekDto(val id: UUID, val startDate: String, val transactions: List<TransactionDto>)

data class WeekWrapper(val start: LocalDateTime, val transaction: Transaction)

fun groupByWeek(events: List<Event>): List<TransactionWeekDto> {

    val lunchEvents = events
        .filter { it.type == Event.CardTransactionAdded::class.java.simpleName }

    val markedAsLunchEvents = events
        .filter { it.type == Event.TransactionMarkedAsLunch::class.java.simpleName }

    val lunchTransactions = lunchEvents
        .filter { l -> markedAsLunchEvents.any{
            m -> (m as Event.TransactionMarkedAsLunch).id == (l as Event.CardTransactionAdded).transaction.id } }
        .map { (it as Event.CardTransactionAdded).transaction }

    val adjusted = lunchTransactions
        .map{ WeekWrapper(it.created.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), it) }


    val groupByWeek = adjusted.groupBy { it.start.toLocalDate() }

    val transactionWeeks = groupByWeek
        .toList()
        .map { TransactionWeekDto(UUID.randomUUID(), it.first.toString(), it.second.map { t -> toTransactionDto(t.transaction) }) }

    return transactionWeeks
}

private fun toTransactionDto(tran: Transaction): TransactionDto {
    return TransactionDto(tran.id, tran.amount, tran.created.toString(), tran.retailer)
}