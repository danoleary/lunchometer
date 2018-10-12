package com.lunchometer.domain

import com.lunchometer.avro.CommandResponse
import com.lunchometer.avro.DomainAggregate
import handle
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Predicate
import org.apache.kafka.streams.state.KeyValueStore

fun buildTopology(): Topology {
    val builder = StreamsBuilder()

    val commandStream = builder.stream<String, SpecificRecord>("commands")

    val eventStore: KTable<String, DomainAggregate> = commandStream
        .groupByKey()
        .aggregate(
            { DomainAggregate(listOf<SpecificRecord>(), listOf<SpecificRecord>(), CommandResponse("", true)) },
            { _, v, agg -> handle(agg.allEvents, v) },
            Materialized.`as`<String, DomainAggregate, KeyValueStore<Bytes, ByteArray>>("event-store")
        )

    val branches = eventStore.toStream()
        .branch(
            Predicate { _, _ -> true },
            Predicate { _, _ -> true }
        )

    branches[0]
        .flatMapValues { _, v -> v.newEvents }
        .to("events")

    branches[1]
        .mapValues { _, v -> v.lastCommandResponse }
        .to("command-responses")

    return builder.build()
}