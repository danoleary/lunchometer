package com.lunchometer.starlingintegration

import com.google.gson.Gson
import com.lunchometer.shared.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.json.JSONObject
import java.util.*


private const val bootstrapServers = "kafka1:9092"
private val streamsConfiguration = Properties()

fun main(args: Array<String>) {

    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "starling-integration")
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "starling-integration")
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000)
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)

    val builder = StreamsBuilder()

        builder
        .stream<String, String>(EventTopic)
        .filter{ _, v -> filterEvents(v) }
        .mapValues { _, v -> mapEvent(v) }
        .flatMapValues { _, v -> fetchTransactions(v as Event.CardTransactionRetrievalRequested) }
        .mapValues { _, v -> Gson().toJson(v) }
        .to(CommandsTopic)

    val streams = KafkaStreams(builder.build(), streamsConfiguration)

    streams.cleanUp()

    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}

private fun filterEvents(json: String): Boolean {
    val type = JSONObject(json).getString("type")
    return type == Event.CardTransactionRetrievalRequested::class.java.simpleName
}

private fun mapEvent(json: String): Event {
    val event = Gson().fromJson(json, Event.CardTransactionRetrievalRequested::class.java)
    return event
}

fun fetchTransactions(event: Event.CardTransactionRetrievalRequested): List<Command> {
    val starlingTransactions = getAllTransactions()
    return starlingTransactions.map { Command.AddCardTransaction(
        userId = event.userId,
        transaction = Transaction(
            id = it.id,
            amount = it.amount,
            created = it.created,
            retailer = it.narrative
        )
    ) }
}