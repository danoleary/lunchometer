package com.lunchometer.api

import com.lunchometer.shared.Transaction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


fun groupByWeek(transactions: List<Transaction>): Map<LocalDate, List<Transaction>> {
    val adjusted = transactions
        .map {it.copy(created = it.created.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) }
    return adjusted.groupBy { it.created.toLocalDate() }
}