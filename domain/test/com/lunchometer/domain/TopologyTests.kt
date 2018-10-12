package com.lunchometer.domain

import com.google.gson.Gson
import com.lunchometer.avro.AddCardTransaction
import com.lunchometer.avro.CardTransactionAdded
import com.lunchometer.avro.CardTransactionRetrievalRequested
import com.lunchometer.avro.CommandHeader
import com.lunchometer.avro.EventHeader
import com.lunchometer.avro.RetrieveCardTransactions
import com.lunchometer.avro.TransactionMarkedAsLunch
import com.lunchometer.shared.*
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueStore
import org.junit.Before
import org.junit.Test
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.OutputVerifier
import org.junit.After
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
private const val userId = "dan"

class TopologyTests {

    private var testDriver: TopologyTestDriver? = null
    private var store: KeyValueStore<String, SpecificRecord>? = null

    private val stringDeserializer = StringDeserializer()
    private val recordFactory =
        ConsumerRecordFactory<String, SpecificRecord>(StringSerializer(), SpecificAvroSerializer())

//    @Before
//    fun setup() {
//        val topology = buildTopology()
//
//        // setup test driver
//        val props = getKafkaConfig("testApp")
//        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
//
//        testDriver = TopologyTestDriver(topology, props)
//
//        store = (testDriver as TopologyTestDriver)
//            .getKeyValueStore<String, SpecificRecord>("event-store")
//
//    }
//
//    @Test
//    fun `single new events should be published to events topic`() {
//
//        publishRetrieveCardTransactions()
//
//        val expectedNewEvent = CardTransactionRetrievalRequested(eventHeader())
//
//        OutputVerifier
//            .compareKeyValue((testDriver as TopologyTestDriver)
//                .readOutput(EventTopic, stringDeserializer, SpecificAvroDeserializer<SpecificRecord>()), userId, expectedNewEvent)
//
//        assertNull((testDriver as TopologyTestDriver).readOutput(EventTopic, stringDeserializer, SpecificAvroDeserializer<SpecificRecord>()))
//    }

//    @Test
//    fun `single command response should be published to command response topic`() {
//
//        val key = "dan"
//        val command = RetrieveCardTransactions(key)
//
//        publishCommand(key, command)
//
//        val expectedNewEvent = CommandResponse(command.id, true)
//
//        OutputVerifier
//            .compareKeyValue((testDriver as TopologyTestDriver)
//                .readOutput(CommandResponsesTopic, stringDeserializer, stringDeserializer), key, Gson().toJson(expectedNewEvent))
//
//        assertNull((testDriver as TopologyTestDriver).readOutput(CommandResponsesTopic, stringDeserializer, stringDeserializer))
//    }

//    @Test
//    fun `multiple new events should be published to events topic`() {
//
//        publishRetrieveCardTransactions(userId)
//
//        val addTransactionCommand = AddCardTransaction(commandHeader(), lunchTransaction())
//        publishCommand(userId, addTransactionCommand)
//
//        val expectedEvent1 = CardTransactionRetrievalRequested(eventHeader())
//        val expectedEvent2 = CardTransactionAdded(eventHeader(), lunchTransaction())
//        val expectedEvent3 = TransactionMarkedAsLunch(eventHeader(), lunchTransaction().id)
//
//        OutputVerifier
//            .compareKeyValue((testDriver as TopologyTestDriver)
//                .readOutput(EventTopic, stringDeserializer, stringDeserializer), userId, Gson().toJson(expectedEvent1))
//
//        OutputVerifier
//            .compareKeyValue((testDriver as TopologyTestDriver)
//                .readOutput(EventTopic, stringDeserializer, stringDeserializer), userId, Gson().toJson(expectedEvent2))
//
//        OutputVerifier
//            .compareKeyValue((testDriver as TopologyTestDriver)
//                .readOutput(EventTopic, stringDeserializer, stringDeserializer), userId, Gson().toJson(expectedEvent3))
//    }
//
//    @Test
//    fun `new events should be persisted to event store along with existing events`() {
//
//        publishRetrieveCardTransactions(userId)
//        publishAddCardTransaction(userId)
//
//        val storedEvents = store!!.get(userId)
//
//        val expectedEvents = listOf(
//            CardTransactionRetrievalRequested(eventHeader()),
//            CardTransactionAdded(eventHeader(), lunchTransaction()),
//            TransactionMarkedAsLunch(eventHeader(), lunchTransaction().id)
//        )
//
//        assertEquals(expectedEvents, storedEvents)
//    }

    @After
    fun tearDown() {
        testDriver!!.close()
    }

    private fun publishRetrieveCardTransactions() {
        val command = RetrieveCardTransactions(commandHeader())
        publishCommand(command)
    }

    private fun publishAddCardTransaction() {
        val command = AddCardTransaction(commandHeader(), lunchTransaction())
        publishCommand(command)
    }

    private fun publishCommand(command: SpecificRecord) {
        testDriver!!.pipeInput(recordFactory.create(CommandsTopic, userId, command))
    }
}

private fun commandHeader() =
    CommandHeader(
        UUID.randomUUID().toString(),
        userId,
        timestamp
    )

private fun eventHeader() =
    EventHeader(
        UUID.randomUUID().toString(),
        userId,
        timestamp
    )