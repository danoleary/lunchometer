package com.lunchometer.starlingintegration

import com.lunchometer.avro.CardTransactionRetrievalRequested
import com.lunchometer.avro.Transaction
import com.lunchometer.avro.AddCardTransaction
import com.lunchometer.avro.CommandHeader
import com.lunchometer.shared.EventTopic
import com.lunchometer.shared.CommandsTopic
import com.lunchometer.shared.getKafkaConfig
import com.typesafe.config.ConfigFactory
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import java.util.*
import org.apache.avro.specific.SpecificRecord
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset

fun main(args: Array<String>) {

    val config = ConfigFactory.load()
    val starlingToken = config.getString("starling.token")

    val streamsConfiguration = getKafkaConfig("starling-integration")

    val builder = StreamsBuilder()

        builder
        .stream<String, SpecificRecord>(EventTopic)
        .filter{ _, v -> filterEvents(v) }
        .mapValues { _, v -> mapToEvent(v) }
        .flatMapValues { _, v -> fetchTransactions(v, starlingToken) }
        .to(CommandsTopic)

    val streams = KafkaStreams(builder.build(), streamsConfiguration)

    streams.cleanUp()

    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}

private fun filterEvents(record: SpecificRecord): Boolean =
    record.schema.name == CardTransactionRetrievalRequested::class.java.simpleName

private fun mapToEvent(record: SpecificRecord): CardTransactionRetrievalRequested =
    record as CardTransactionRetrievalRequested

private fun fetchTransactions(request: CardTransactionRetrievalRequested, starlingToken: String)
    : List<AddCardTransaction> {
    val transactions = getAllTransactions(starlingToken)
    val events = mutableListOf<AddCardTransaction>()
    for (it in transactions) {
        try{
            it.created.toInstant(ZoneOffset.UTC).toEpochMilli()
        } catch(e: Exception) {
            println("I'm here")
        }
        try {
            val event = AddCardTransaction(
                CommandHeader(
                    UUID.randomUUID().toString(),
                    request.header.userId,
                    LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()),
                Transaction(
                    it.id.toString(),
                    it.amount,
                    0L,
                    it.narrative)
            )
            events.add(event)
        } catch (e: Exception) {
            println(e.message)
        }
    }
    return events
}
//    return transactions
//        .map { AddCardTransaction(
//            CommandHeader(
//                UUID.randomUUID().toString(),
//                event.header.userId,
//                "AddCardTransaction",
//                LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()),
//            Transaction(
//                it.id.toString(),
//                it.amount,
//                it.created.toInstant(ZoneOffset.UTC).toEpochMilli(),
//                it.narrative)
//        )
//        }
