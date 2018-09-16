package com.lunchometer.api.readmodels

import com.lunchometer.shared.Event
import com.lunchometer.shared.Transaction
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class EventGroupingTests {

    private val userId = UUID.randomUUID().toString()
    private val timestamp = LocalDateTime.now()

    @Test
    fun `events are correctly grouped by transaction id`() {
        val tranId1 = UUID.randomUUID()
        val transactionAdded1 = Event.CardTransactionAdded(userId, timestamp, transaction(tranId1))

        val tranId2 = UUID.randomUUID()
        val transactionAdded2 = Event.CardTransactionAdded(userId, timestamp, transaction(tranId2))

        val grouped = groupEventByTransactionId(listOf(transactionAdded1, transactionAdded2))

        assertEquals(2, grouped.size)
    }
}

private fun transaction(id: UUID) = Transaction(
    id = id,
    amount = 4.5.toBigDecimal(),
    created = LocalDateTime.now(),
    retailer = "some place"
)