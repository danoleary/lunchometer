package com.lunchometer.domain.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.shared.CommandResponse
import com.lunchometer.shared.deserializeInternalCommandResponse
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
        val newCommandResponse = CommandResponse(internalCommandResponse.commandId, internalCommandResponse.success)
        context!!.forward(key, Gson().toJson(newCommandResponse))
    }

    override fun close() {
    }
}