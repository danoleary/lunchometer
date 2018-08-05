package com.lunchometer.api.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.api.CommandResponseStore
import com.lunchometer.api.EventStore
import com.lunchometer.api.deserializeCommandResponseList
import com.lunchometer.shared.CommandResponse
import com.lunchometer.api.deserializeInternalCommandResponse
import org.apache.kafka.streams.processor.*
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

class CommandResponsePublishingProcessor : Processor<String, String> {
    override fun punctuate(timestamp: Long) {
        TODO("not implemented")
    }

    private var context: ProcessorContext? = null
    private var commandResponseStore: KeyValueStore<String, String>? = null

    override fun init(context: ProcessorContext) {
        this.context = context
        commandResponseStore =  context.getStateStore(CommandResponseStore) as (KeyValueStore<String, String>)
        Objects.requireNonNull(commandResponseStore, "State store can't be null")
    }

    override fun process(key: String, value: String) {
        val internalCommandResponse = deserializeInternalCommandResponse(value)
        val existingCommandResponses = commandResponseStore!![key]
        val commandResponses =
            if (existingCommandResponses == null)
                listOf()
            else
                deserializeCommandResponseList(existingCommandResponses)
        val newCommandResponse = CommandResponse(internalCommandResponse.commandId, internalCommandResponse.success)
        commandResponseStore!!.put(key, Gson().toJson(commandResponses.plus(newCommandResponse)))
        context!!.forward(key, Gson().toJson(newCommandResponse))
    }

    override fun close() {
    }
}