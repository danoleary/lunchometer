package com.lunchometer.domain.commandhandlers

import com.lunchometer.avro.CardTransactionRetrievalRequested
import com.lunchometer.avro.CommandHeader
import com.lunchometer.avro.EventHeader
import com.lunchometer.avro.RetrieveCardTransactions
import handle
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val userId = "aUserId"
private val timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()

class RetrieveCardTransactionsHandlerTests {

    @Test
    fun `a retrieve card transactions request results in a card transactions retrieval requested event`() {
        val retrieveCardTransactions = RetrieveCardTransactions(commandHeader())

        val result = handle(listOf(), retrieveCardTransactions)

        assertTrue(result.lastCommandResponse.success)
        assertEquals(retrieveCardTransactions.header.id, result.lastCommandResponse.commandId)
        assertTrue(result.newEvents.single() is CardTransactionRetrievalRequested)
        assertTrue(result.allEvents.single() is CardTransactionRetrievalRequested)
    }

    @Test
    fun `cannot retrieve transactions for the same account twice`() {
        val retrieveCardTransactions = RetrieveCardTransactions(commandHeader())

        val result =
            handle(listOf(CardTransactionRetrievalRequested(eventHeader())), retrieveCardTransactions)

        assertFalse(result.lastCommandResponse.success)
        assertEquals(retrieveCardTransactions.header.id, result.lastCommandResponse.commandId)
        assertTrue(result.newEvents.isEmpty())
        assertTrue(result.allEvents.single() is CardTransactionRetrievalRequested)
    }

    private fun commandHeader() =
        CommandHeader(
            UUID.randomUUID().toString(),
            userId,
            timestamp
        )

    private fun eventHeader() =
        EventHeader(
            UUID.randomUUID().toString(),
            userId,
            timestamp
        )
}