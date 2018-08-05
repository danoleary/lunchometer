package com.lunchometer.api.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.api.EventStore
import com.lunchometer.api.commandhandlers.handle
import com.lunchometer.api.deserializeCommand
import com.lunchometer.api.deserializeInternalCommandResponse
import com.lunchometer.shared.Event
import org.apache.kafka.streams.processor.*
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

class EventPublishingProcessor : Processor<String, String> {
    override fun punctuate(timestamp: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var context: ProcessorContext? = null

    override fun init(context: ProcessorContext) {
        this.context = context
    }

    override fun process(key: String, value: String) {
        val internalCommandResponse = deserializeInternalCommandResponse(value)
        val events = internalCommandResponse.events
        events.forEach { context!!.forward(key, Gson().toJson(it)) }
    }

    override fun close() {
        // nothing to do
    }
}