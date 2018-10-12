package com.lunchometer.api

import com.google.gson.Gson
import com.lunchometer.shared.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Materialized

import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.state.KeyValueStore

const val CommandResponseStore = "command-response-store"
const val ApiEventStore = "api-event-store"

fun buildTopology(): Topology {

    val builder = StreamsBuilder()

//    builder
//        .stream<String, String>(CommandResponsesTopic)
//        .groupByKey()
//        .aggregate(
//            { "[]" },
//            { _, v, agg -> reduceCommands(v, agg) },
//            Materialized.`as`<String, String, KeyValueStore<Bytes, ByteArray>>(CommandResponseStore))
//
//    builder
//        .stream<String, String>(EventTopic)
//        .groupByKey()
//        .aggregate(
//            { "[]" },
//            { _, v, agg -> reduceEvents(v, agg) },
//            Materialized.`as`<String, String, KeyValueStore<Bytes, ByteArray>>(ApiEventStore))

    return builder.build()
}

//private fun reduceCommands(commandResponse: String, existing: String): String {
//    val existingCommandResponses = deserializeCommandResponseList(existing)
//    val new = deserializeCommandResponse(commandResponse)
//    return Gson().toJson(existingCommandResponses.plus(new))
//}
//
//private fun reduceEvents(event: String, existing: String): String {
//    val existingEvents = deserializeEventList(existing)
//    val new = deserializeEvent(event)
//    return Gson().toJson(existingEvents.plus(new))
//}



