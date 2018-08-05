package com.lunchometer.api.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.api.EventStore
import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import com.lunchometer.shared.InternalCommandResponse
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.processor.MockProcessorContext
import org.apache.kafka.streams.state.Stores
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.expect

private const val key = "userid"

class CommandHandlerProcessorTests {

    @Test
    fun `stores new events and forwards internal command response`() {
        val processorUnderTest = CommandHandlerProcessor()
        val context = MockProcessorContext()

        val store =
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(EventStore),
                Serdes.String(),
                Serdes.String())
                .withLoggingDisabled() // Changelog is not supported by MockProcessorContext.
                .build()
        store.init(context, store)
        context.register(store) { _, _ -> Unit}
        processorUnderTest.init(context)

        val command = Command.RetrieveCardTransactions(key)

        processorUnderTest.process(key, Gson().toJson(command))

        val expectedEvents = listOf(Event.CardTransactionRetrievalRequested(key))

        val forwarded = context.forwarded().iterator()
        val forwardedMessage = forwarded.next().keyValue()
        assertEquals(forwardedMessage.key, key)
        assertEquals(forwardedMessage.value,
            Gson().toJson(InternalCommandResponse(command.id, true, expectedEvents)))
        assertFalse(forwarded.hasNext())

        val storedEvents = store[key]
        assertEquals(Gson().toJson(expectedEvents), storedEvents)
    }
}