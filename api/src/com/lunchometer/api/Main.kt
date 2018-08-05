package com.lunchometer.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lunchometer.api.commandhandlers.handle
import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier

import org.apache.kafka.streams.state.Stores
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import java.util.Properties

private const val bootstrapServers = "localhost:9092"
private val streamsConfiguration = Properties()

fun main(args: Array<String>) {

    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "lunchometer-api")
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "lunchometer-api")
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000)
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)

    val topologyBuilder = buildTopology()

    val streams = KafkaStreams(topologyBuilder, streamsConfiguration)

    streams.cleanUp()

    streams.start()

    startServer(streams)

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}


