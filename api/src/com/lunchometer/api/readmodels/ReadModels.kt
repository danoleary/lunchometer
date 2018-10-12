//package com.lunchometer.api.readmodels
//
//import com.lunchometer.shared.Transaction
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.util.*
//
//data class WeekWrapper(val start: LocalDateTime, val transaction: Transaction)
//
//fun groupByWeek(events: List<Event>): List<TransactionWeekDto> {
//    val groupedByTransactionId: Map<UUID, List<Event>> = groupEventByTransactionId(events)
//    val lunchTransactions: List<Transaction> = groupedByTransactionId
//        .filter { isLunchTransaction(it.component2()) }
//        .mapValues { getTransaction(it.value)  }
//        .values
//        .toList()
//    val groupedByWeek: Map<LocalDate, List<Transaction>> = groupByWeek(lunchTransactions)
//    val transactionWeeks = groupedByWeek.map { toTransactionWeek(it) }
//    return transactionWeeks.sortedByDescending { it.startDate }
//}
//
//private fun getTransaction(events: List<Event>): Transaction =
//    events
//        .filter { it.type == Event.CardTransactionAdded::class.java.simpleName }
//        .map { it as Event.CardTransactionAdded }
//        .first()
//        .transaction
