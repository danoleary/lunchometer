package com.lunchometer.domain.commandhandlers

import com.lunchometer.avro.AddCardTransaction
import com.lunchometer.avro.CardTransactionAdded
import com.lunchometer.avro.CardTransactionRetrievalRequested
import com.lunchometer.avro.CommandHeader
import com.lunchometer.avro.EventHeader
import com.lunchometer.avro.TransactionMarkedAsLunch
import com.lunchometer.avro.TransactionMarkedAsNotLunch
import com.lunchometer.domain.lunchTransaction
import com.lunchometer.domain.weekDayLunchtime
import com.lunchometer.domain.weekdayAfterLunch
import com.lunchometer.domain.weekdayBeforeLunch
import com.lunchometer.domain.weekendLunchtime
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

class AddCardTransactionHandlerTests {

    @Test
    fun `a transaction cannot be added before transactions are requested`() {
        val addCardTransaction = AddCardTransaction(commandHeader(), lunchTransaction())

        val result = handle(listOf(), addCardTransaction)

        assertFalse(result.lastCommandResponse.success)
        assertEquals(addCardTransaction.header.id, result.lastCommandResponse.commandId)
        assertTrue(result.newEvents.isEmpty())
        assertTrue(result.allEvents.isEmpty())
    }

    @Test
    fun `a transaction created on on a weekend at lunchtime creates a transaction added and a transaction marked as not lunch`() {

        val transaction = lunchTransaction(weekendLunchtime)

        val addCardTransaction = AddCardTransaction(commandHeader(), transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.lastCommandResponse.success)
        assertEquals(addCardTransaction.header.id, result.lastCommandResponse.commandId)

        assertTrue(result.newEvents.any { it is CardTransactionAdded })
        assertTrue(result.newEvents.any { it is TransactionMarkedAsNotLunch })
        assertEquals(2, result.newEvents.size)

        assertTrue(result.allEvents.any { it is CardTransactionAdded })
        assertTrue(result.allEvents.any { it is TransactionMarkedAsNotLunch })
        assertTrue(result.allEvents.any { it is CardTransactionRetrievalRequested })
        assertEquals(3, result.allEvents.size)
    }

    @Test
    fun `a transaction created on on a weekday before lunchtime creates a transaction added and a transaction marked as not lunch`() {

        val transaction = lunchTransaction(weekdayBeforeLunch)

        val addCardTransaction = AddCardTransaction(commandHeader(), transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.lastCommandResponse.success)
        assertEquals(addCardTransaction.header.id, result.lastCommandResponse.commandId)

        assertTrue(result.newEvents.any { it is CardTransactionAdded })
        assertTrue(result.newEvents.any { it is TransactionMarkedAsNotLunch })
        assertEquals(2, result.newEvents.size)

        assertTrue(result.allEvents.any { it is CardTransactionAdded })
        assertTrue(result.allEvents.any { it is TransactionMarkedAsNotLunch })
        assertTrue(result.allEvents.any { it is CardTransactionRetrievalRequested })
        assertEquals(3, result.allEvents.size)
    }

    @Test
    fun `a transaction created on on a weekday after lunchtime creates a transaction added and a transaction marked as not lunch`() {

        val transaction = lunchTransaction(weekdayAfterLunch)

        val addCardTransaction = AddCardTransaction(commandHeader(), transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.lastCommandResponse.success)
        assertEquals(addCardTransaction.header.id, result.lastCommandResponse.commandId)

        assertTrue(result.newEvents.any { it is CardTransactionAdded })
        assertTrue(result.newEvents.any { it is TransactionMarkedAsNotLunch })
        assertEquals(2, result.newEvents.size)

        assertTrue(result.allEvents.any { it is CardTransactionAdded })
        assertTrue(result.allEvents.any { it is TransactionMarkedAsNotLunch })
        assertTrue(result.allEvents.any { it is CardTransactionRetrievalRequested })
        assertEquals(3, result.allEvents.size)
    }

    @Test
    fun `a transaction created on on a weekday at lunchtime creates a transaction added and a transaction marked as lunch`() {

        val transaction = lunchTransaction(weekDayLunchtime)

        val addCardTransaction = AddCardTransaction(commandHeader(), transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.lastCommandResponse.success)
        assertEquals(addCardTransaction.header.id, result.lastCommandResponse.commandId)

        assertTrue(result.newEvents.any { it is CardTransactionAdded })
        assertTrue(result.newEvents.any { it is TransactionMarkedAsLunch })
        assertEquals(2, result.newEvents.size)

        assertTrue(result.allEvents.any { it is CardTransactionAdded })
        assertTrue(result.allEvents.any { it is TransactionMarkedAsLunch })
        assertTrue(result.allEvents.any { it is CardTransactionRetrievalRequested })
        assertEquals(3, result.allEvents.size)
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

    private val cardTransactionsRequested =
        listOf(CardTransactionRetrievalRequested(eventHeader()))
}