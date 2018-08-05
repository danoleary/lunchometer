package com.lunchometer.api.kafkaProcessors

import com.google.gson.Gson
import com.lunchometer.api.EventStore
import com.lunchometer.api.commandhandlers.handle
import com.lunchometer.api.deserializeCommand
import com.lunchometer.api.deserializeEventList
import com.lunchometer.shared.Event
import org.apache.kafka.streams.processor.*
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

class CommandHandlerProcessor : Processor<String, String> {
    override fun punctuate(timestamp: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var context: ProcessorContext? = null
    private var eventStore: KeyValueStore<String, String>? = null

    override fun init(context: ProcessorContext) {
        this.context = context
        eventStore =  context.getStateStore(EventStore) as (KeyValueStore<String, String>)
        Objects.requireNonNull(eventStore, "State store can't be null");
    }

    override fun process(key: String, value: String) {
        val command = deserializeCommand(value)
        val existingEvents =
            if (eventStore!!.get(key) != null) deserializeEventList(eventStore!!.get(key)) else listOf()
        val internalCommandResponse = handle(existingEvents, command)
        eventStore!!.put(key, Gson().toJson(existingEvents.plus(internalCommandResponse.events)))
        context!!.forward(key, Gson().toJson(internalCommandResponse))
    }

    override fun close() {
        // nothing to do
    }
}