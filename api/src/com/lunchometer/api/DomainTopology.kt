package com.lunchometer.api

import com.lunchometer.api.kafkaProcessors.CommandHandlerProcessor
import com.lunchometer.api.kafkaProcessors.CommandResponsePublishingProcessor
import com.lunchometer.api.kafkaProcessors.EventPublishingProcessor
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
const val CommandResponseStore = "command-response-store"

private const val commandSource = "command-source"
private const val commandHandlerProcessor = "command-handler"
private const val commandResponseSink = "CommandResponseSink"
private const val eventSink = "EventSink"
private const val commandResponsePublishingProcessor = "command-response-publisher"
private const val eventPublishingProcessor = "event-publisher"

fun buildTopology(): Topology {

    val topologyBuilder = Topology()

    topologyBuilder
        .addSource(commandSource, CommandsTopic)
        .addProcessor(commandHandlerProcessor, ProcessorSupplier { CommandHandlerProcessor() }, commandSource)
        .addStateStore(storeBuilder(EventStore), commandHandlerProcessor)
        .addProcessor(commandResponsePublishingProcessor, ProcessorSupplier { CommandResponsePublishingProcessor() }, commandHandlerProcessor)
        .addStateStore(storeBuilder(CommandResponseStore), commandResponsePublishingProcessor)
        .addProcessor(eventPublishingProcessor, ProcessorSupplier { EventPublishingProcessor() }, commandHandlerProcessor)
        .addSink(commandResponseSink, CommandResponsesTopic, commandResponsePublishingProcessor)
        .addSink(eventSink, EventTopic, eventPublishingProcessor)

    return topologyBuilder
}

private fun storeBuilder(name: String): StoreBuilder<KeyValueStore<String, String>>? {
    return Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(name),
        Serdes.String(),
        Serdes.String())
}
