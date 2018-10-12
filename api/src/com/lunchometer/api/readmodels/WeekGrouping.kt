//package com.lunchometer.api.readmodels
//
//import com.lunchometer.shared.Transaction
//import java.time.DayOfWeek
//import java.time.LocalDate
//import java.time.temporal.TemporalAdjusters
//
//fun groupByWeek(transactions: List<Transaction>): Map<LocalDate, List<Transaction>> {
//    val adjusted: List<WeekWrapper> = transactions
//        .map{ WeekWrapper(it.created.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), it) }
//
//    val groupByWeek: Map<LocalDate, List<Transaction>> = adjusted
//        .groupBy { it.start.toLocalDate() }
//        .mapValues { it.value.map { v -> v.transaction } }
//
//    return groupByWeek
//}