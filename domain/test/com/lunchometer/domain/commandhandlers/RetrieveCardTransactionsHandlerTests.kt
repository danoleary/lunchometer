package com.lunchometer.domain.commandhandlers

import com.lunchometer.domain.lunchTransaction
import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val timestamp = LocalDateTime.now()

class RetrieveCardTransactionsHandlerTests {

    @Test
    fun `a retrieve card transactions request results in a card transactions retrieval requested event`() {
        val retrieveCardTransactions = Command.RetrieveCardTransactions("some id")

        val result = handle(listOf(), retrieveCardTransactions)

        assertTrue(result.success)
        assertTrue(result.events.contains(Event.CardTransactionRetrievalRequested("some id", timestamp)))
        assertTrue(result.events.size == 1)
    }

    @Test
    fun `a transaction with the same id can not be added twice`() {
        val addCardTransaction = Command.AddCardTransaction("userId", lunchTransaction)

        val result =
            handle(listOf(Event.CardTransactionAdded("userId", timestamp, lunchTransaction)), addCardTransaction)

        assertFalse(result.success)
        assertTrue(result.events.isEmpty())
    }
}