package com.lunchometer.api

import com.google.gson.Gson
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Materialized

import com.lunchometer.shared.CommandResponsesTopic
import com.lunchometer.shared.deserializeCommandResponse
import com.lunchometer.shared.deserializeCommandResponseList
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.state.KeyValueStore

const val CommandResponseStore = "command-response-store"

fun buildTopology(): Topology {

    val builder = StreamsBuilder()

    builder
        .stream<String, String>(CommandResponsesTopic)
        .groupByKey()
        .aggregate(
            { "[]" },
            { _, v, agg -> reduceCommands(v, agg) },
            Materialized.`as`<String, String, KeyValueStore<Bytes, ByteArray>>(CommandResponseStore))

    return builder.build()
}

private fun reduceCommands(commandResponse: String, existing: String): String {
    val existingCommandResponses = deserializeCommandResponseList(existing)
    val new = deserializeCommandResponse(commandResponse)
    return Gson().toJson(existingCommandResponses.plus(new))
}



