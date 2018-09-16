package com.lunchometer.domain.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.shared.CommandResponse
import com.lunchometer.shared.Event
import com.lunchometer.shared.InternalCommandResponse
import org.apache.kafka.streams.processor.MockProcessorContext
import org.junit.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private const val key = "userid"

class CommandResponsePublishingProcessorTests {

    @Test
    fun `forwards command response`() {
        val processorUnderTest = CommandResponsePublishingProcessor()
        val context = MockProcessorContext()

        processorUnderTest.init(context)

        val internalCommandResponse =
            InternalCommandResponse(UUID.randomUUID(), true, listOf(Event.CardTransactionRetrievalRequested(key, LocalDateTime.now())))

        processorUnderTest.process(key, Gson().toJson(internalCommandResponse))

        val expectedCommandResponse = CommandResponse(internalCommandResponse.commandId, true)

        val forwarded = context.forwarded().iterator()
        val forwardedMessage = forwarded.next().keyValue()
        assertEquals(forwardedMessage.key, key)
        assertEquals(forwardedMessage.value,
            Gson().toJson(expectedCommandResponse))
        assertFalse(forwarded.hasNext())
    }
}