package com.lunchometer.domain.commandhandlers

import com.lunchometer.domain.lunchTransaction
import com.lunchometer.domain.weekdayAfterLunch
import com.lunchometer.domain.weekdayBeforeLunch
import com.lunchometer.domain.weekendLunchtime
import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val userId = "aUserId"

class AddCardTransactionHandlerTests {

    @Test
    fun `a transaction cannot be added before transactions are requested`() {
        val addCardTransaction = Command.AddCardTransaction(userId, lunchTransaction)

        val result = handle(listOf(), addCardTransaction)

        assertFalse(result.success)
        assertTrue(result.events.isEmpty())
    }

    @Test
    fun `a transaction created between 12 and 2 on a weekday creates a transaction added and a transaction marked as lunch`() {

        val addCardTransaction = Command.AddCardTransaction(userId, lunchTransaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.success)
        assertTrue(result.events.contains(Event.CardTransactionAdded(userId, lunchTransaction)))
        assertTrue(result.events.contains(Event.TransactionMarkedAsLunch(userId, lunchTransaction.id)))
        assertTrue(result.events.size == 2)
    }

    @Test
    fun `a transaction created on on a weekend at lunchtime creates a transaction added and a transaction marked as not lunch`() {

        val transaction = lunchTransaction.copy(created = weekendLunchtime)

        val addCardTransaction =
            Command.AddCardTransaction(userId, transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.success)
        assertTrue(result.events.contains(Event.CardTransactionAdded(userId, transaction)))
        assertTrue(result.events.contains(Event.TransactionMarkedAsNotLunch(userId, transaction.id)))
        assertTrue(result.events.size == 2)
    }

    @Test
    fun `a transaction created on on a weekday before lunchtime creates a transaction added and a transaction marked as not lunch`() {

        val transaction = lunchTransaction.copy(created = weekdayBeforeLunch)

        val addCardTransaction =
            Command.AddCardTransaction(userId, transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.success)
        assertTrue(result.events.contains(Event.CardTransactionAdded(userId, transaction)))
        assertTrue(result.events.contains(Event.TransactionMarkedAsNotLunch(userId, transaction.id)))
        assertTrue(result.events.size == 2)
    }

    @Test
    fun `a transaction created on on a weekday after lunchtime creates a transaction added and a transaction marked as not lunch`() {

        val transaction = lunchTransaction.copy(created = weekdayAfterLunch)

        val addCardTransaction =
            Command.AddCardTransaction(userId, transaction)

        val result = handle(cardTransactionsRequested, addCardTransaction)

        assertTrue(result.success)
        assertTrue(result.events.contains(Event.CardTransactionAdded(userId, transaction)))
        assertTrue(result.events.contains(Event.TransactionMarkedAsNotLunch(userId, transaction.id)))
        assertTrue(result.events.size == 2)
    }

    private val cardTransactionsRequested =
        listOf(Event.CardTransactionRetrievalRequested(userId))
}