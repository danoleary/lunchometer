package com.lunchometer.shared

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun millisToLocalDateTime(milis: Long): LocalDateTime {
    val i = Instant.ofEpochMilli(milis)
    val z = ZonedDateTime.ofInstant(i, ZoneId.of("UTC"))
    return z.toLocalDateTime()
}

fun localDatetimeToMillis(dateTime: LocalDateTime): Long  {
    return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
}