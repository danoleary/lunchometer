package com.lunchometer.shared

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Transaction(
    val id: UUID, val amount: BigDecimal, val created: LocalDateTime, val retailer: String)