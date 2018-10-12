package com.lunchometer.api

import com.lunchometer.shared.getKafkaConfig
import org.apache.kafka.streams.KafkaStreams

fun main(args: Array<String>) {

    val streamsConfiguration = getKafkaConfig("lunchometer-api")

    val topologyBuilder = buildTopology()

    val streams = KafkaStreams(topologyBuilder, streamsConfiguration)

    streams.cleanUp()

    streams.start()

    startServer(streams)

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}


