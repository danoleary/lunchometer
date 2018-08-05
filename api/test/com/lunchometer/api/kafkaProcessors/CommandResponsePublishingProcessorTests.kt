package com.lunchometer.api.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.api.CommandResponseStore
import com.lunchometer.api.EventStore
import com.lunchometer.shared.Command
import com.lunchometer.shared.CommandResponse
import com.lunchometer.shared.Event
import com.lunchometer.shared.InternalCommandResponse
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.processor.MockProcessorContext
import org.apache.kafka.streams.state.Stores
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private const val key = "userid"

class CommandResponsePublishingProcessorTests {

    @Test
    fun `stores and forwards command response`() {
        val processorUnderTest = CommandResponsePublishingProcessor()
        val context = MockProcessorContext()

        val store =
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(CommandResponseStore),
                Serdes.String(),
                Serdes.String())
                .withLoggingDisabled() // Changelog is not supported by MockProcessorContext.
                .build()
        store.init(context, store)
        context.register(store) { _, _ -> Unit}
        processorUnderTest.init(context)

        val internalCommandResponse =
            InternalCommandResponse(UUID.randomUUID(), true, listOf(Event.CardTransactionRetrievalRequested(key)))

        processorUnderTest.process(key, Gson().toJson(internalCommandResponse))

        val expectedCommandResponse = CommandResponse(internalCommandResponse.commandId, true)

        val forwarded = context.forwarded().iterator()
        val forwardedMessage = forwarded.next().keyValue()
        assertEquals(forwardedMessage.key, key)
        assertEquals(forwardedMessage.value,
            Gson().toJson(expectedCommandResponse))
        assertFalse(forwarded.hasNext())

        val storedCommandResponse = store[key]
        assertEquals(Gson().toJson(listOf(expectedCommandResponse)), storedCommandResponse)
    }
}