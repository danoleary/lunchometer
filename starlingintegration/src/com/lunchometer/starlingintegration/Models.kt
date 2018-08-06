package com.lunchometer.starlingintegration

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class StarlingTransactionResponse(val _embedded: Embedded)

data class Embedded(val transactions: List<StarlingTransaction>)

data class StarlingTransaction(val id: UUID, val currency: String, val amount: BigDecimal, val direction: String,
                       val created: LocalDateTime, val narrative: String, val source: String)

data class StarlingAccount(val accountUid: UUID, val defaultCategory: UUID, val currency: String, val createdAt: LocalDateTime)