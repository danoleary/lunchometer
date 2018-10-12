package com.lunchometer.domain

import com.lunchometer.avro.Transaction
import com.lunchometer.shared.localDatetimeToMillis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

val weekday = LocalDate.of(2018, 8, 3)
val lunchTime = LocalTime.of(12, 0, 0)
val weekDayLunchtime = LocalDateTime.of(weekday, lunchTime)

val weekendDay = LocalDate.of(2018, 8, 4)
val weekendLunchtime = LocalDateTime.of(weekendDay, lunchTime)

val beforeLunchTime =  LocalTime.of(11, 59, 59)

val weekdayBeforeLunch = LocalDateTime.of(weekday, beforeLunchTime)

val afterLunchTime =  LocalTime.of(14, 0, 0)
val weekdayAfterLunch = LocalDateTime.of(weekday, afterLunchTime)

fun lunchTransaction(created: LocalDateTime = weekDayLunchtime) =
    Transaction(
        UUID.randomUUID().toString(),
        5.49.toBigDecimal(),
        localDatetimeToMillis(created),
        "a cafe"
    )