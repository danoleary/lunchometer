package com.lunchometer.domain.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.shared.deserializeInternalCommandResponse
import org.apache.kafka.streams.processor.*

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