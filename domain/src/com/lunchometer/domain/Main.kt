package com.lunchometer.domain

import com.lunchometer.shared.getKafkaConfig
import org.apache.kafka.streams.KafkaStreams

fun main(args: Array<String>) {

    val streamsConfiguration = getKafkaConfig("lunchometer-domain")

    val topology = buildTopology()

    val streams = KafkaStreams(topology, streamsConfiguration)

    streams.cleanUp()

    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
