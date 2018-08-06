package com.lunchometer.domain

import com.lunchometer.domain.kafkaProcessors.CommandHandlerProcessor
import com.lunchometer.domain.kafkaProcessors.CommandResponsePublishingProcessor
import com.lunchometer.domain.kafkaProcessors.EventPublishingProcessor
import com.lunchometer.shared.CommandResponsesTopic
import com.lunchometer.shared.CommandsTopic
import com.lunchometer.shared.EventTopic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores

const val EventStore = "event-store"

private const val commandSource = "command-source"
private const val commandHandlerProcessor = "command-handler"
private const val commandResponseSink = "CommandResponseSink"
private const val eventSink = "EventSink"
private const val commandResponsePublishingProcessor = "command-response-publisher"
private const val eventPublishingProcessor = "event-publisher"

fun buildTopology(): Topology {

    val topologyBuilder = Topology()

    topologyBuilder
        // stream from commands topic
        .addSource(commandSource, CommandsTopic)

        // handle command, store events and forward internal command response
        .addProcessor(commandHandlerProcessor, ProcessorSupplier { CommandHandlerProcessor() }, commandSource)

        // add events state store
        .addStateStore(storeBuilder(EventStore), commandHandlerProcessor)

        // forward command response
        .addProcessor(commandResponsePublishingProcessor, ProcessorSupplier { CommandResponsePublishingProcessor() }, commandHandlerProcessor)

        // forward events
        .addProcessor(eventPublishingProcessor, ProcessorSupplier { EventPublishingProcessor() }, commandHandlerProcessor)

        // publish command responses
        .addSink(commandResponseSink, CommandResponsesTopic, commandResponsePublishingProcessor)

        // publish events
        .addSink(eventSink, EventTopic, eventPublishingProcessor)

    return topologyBuilder
}

private fun storeBuilder(name: String): StoreBuilder<KeyValueStore<String, String>>? {
    return Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(name),
        Serdes.String(),
        Serdes.String())
}
