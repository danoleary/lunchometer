package com.lunchometer.api.commandhandlers

import com.lunchometer.api.lunchTransaction
import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RetrieveCardTransactionsHandlerTests {

    @Test
    fun `a retrieve card transactions request results in a card transactions retrieval requested event`() {
        val retrieveCardTransactions = Command.RetrieveCardTransactions("some id")

        val result = handle(listOf(), retrieveCardTransactions)

        assertTrue(result.success)
        assertTrue(result.events.contains(Event.CardTransactionRetrievalRequested("some id")))
        assertTrue(result.events.size == 1)
    }

    @Test
    fun `a transaction with the same id can not be added twice`() {
        val addCardTransaction = Command.AddCardTransaction("userId", lunchTransaction)

        val result =
            handle(listOf(Event.CardTransactionAdded("userId", lunchTransaction)), addCardTransaction)

        assertFalse(result.success)
        assertTrue(result.events.isEmpty())
    }
}