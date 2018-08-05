package com.lunchometer.api.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.api.commandhandlers.CommandResponse
import com.lunchometer.api.deserializeInternalCommandResponse
import org.apache.kafka.streams.processor.*

class CommandResponsePublishingProcessor : Processor<String, String> {
    override fun punctuate(timestamp: Long) {
        TODO("not implemented")
    }

    private var context: ProcessorContext? = null

    override fun init(context: ProcessorContext) {
        this.context = context
    }

    override fun process(key: String, value: String) {
        val internalCommandResponse = deserializeInternalCommandResponse(value)
        context!!.forward(key, Gson().toJson(
            CommandResponse(internalCommandResponse.commandId, internalCommandResponse.success)))
    }

    override fun close() {
    }
}