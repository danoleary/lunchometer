//package com.lunchometer.api
//
//import com.lunchometer.shared.Transaction
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.LocalTime
//import java.util.*
//
//val weekday = LocalDate.of(2018, 8, 3)
//val lunchTime = LocalTime.of(12, 15)
//val weekDayLunchtime = LocalDateTime.of(weekday, lunchTime)
//
//val weekendDay = LocalDate.of(2018, 8, 4)
//val weekendLunchtime = LocalDateTime.of(weekendDay, lunchTime)
//
//val beforeLunchTime =  LocalTime.of(11, 59)
//val weekdayBeforeLunch = LocalDateTime.of(weekday, beforeLunchTime)
//
//val afterLunchTime =  LocalTime.of(14, 0)
//val weekdayAfterLunch = LocalDateTime.of(weekday, afterLunchTime)
//
//val lunchTransaction =
//    Transaction(UUID.randomUUID(), 5.49.toBigDecimal(), weekDayLunchtime, "a cafe")