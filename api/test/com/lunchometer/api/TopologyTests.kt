package com.lunchometer.api

import com.google.gson.Gson
import com.lunchometer.api.commandhandlers.CommandResponse
import com.lunchometer.shared.*
import org.apache.kafka.common.serialization.Serdes
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
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TopologyTests {

    private var testDriver: TopologyTestDriver? = null
    private var store: KeyValueStore<String, String>? = null

    private val stringDeserializer = StringDeserializer()
    private val recordFactory = ConsumerRecordFactory<String, String>(StringSerializer(), StringSerializer())

    @Before
    fun setup() {
        val topology = buildTopology()

        // setup test driver
        val props = Properties()
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation")
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
        testDriver = TopologyTestDriver(topology, props)

        store = (testDriver as TopologyTestDriver).getKeyValueStore<String, String>(EventStore)

    }

    @Test
    fun `single new events should be published to events topic`() {

        val key = "dan"
        publishRetrieveCardTransactions(key)

        val expectedNewEvent = Event.CardTransactionRetrievalRequested(key)

        OutputVerifier
            .compareKeyValue((testDriver as TopologyTestDriver)
                .readOutput(EventTopic, stringDeserializer, stringDeserializer), key, Gson().toJson(expectedNewEvent))

        assertNull((testDriver as TopologyTestDriver).readOutput(EventTopic, stringDeserializer, stringDeserializer))
    }

    @Test
    fun `single command response should be published to command response topic`() {

        val key = "dan"
        val command = Command.RetrieveCardTransactions(key)

        publishCommand(key, command)

        val expectedNewEvent = CommandResponse(command.id,true)

        OutputVerifier
            .compareKeyValue((testDriver as TopologyTestDriver)
                .readOutput(CommandResponsesTopic, stringDeserializer, stringDeserializer), key, Gson().toJson(expectedNewEvent))

        assertNull((testDriver as TopologyTestDriver).readOutput(CommandResponsesTopic, stringDeserializer, stringDeserializer))
    }

    @Test
    fun `multiple new events should be published to events topic`() {
        val key = "dan"
        val command = Command.AddCardTransaction(key, lunchTransaction)

        publishCommand(key, command)

        val expectedEvent1 = Event.CardTransactionAdded(key, lunchTransaction)
        val expectedEvent2 = Event.TransactionMarkedAsLunch(key, lunchTransaction.id)

        OutputVerifier
            .compareKeyValue((testDriver as TopologyTestDriver)
                .readOutput(EventTopic, stringDeserializer, stringDeserializer), key, Gson().toJson(expectedEvent1))

        OutputVerifier
            .compareKeyValue((testDriver as TopologyTestDriver)
                .readOutput(EventTopic, stringDeserializer, stringDeserializer), key, Gson().toJson(expectedEvent2))
    }

    @Test
    fun `new events should be persisted to event store along with existing events`() {

        val key = "dan"
        publishRetrieveCardTransactions(key)
        publishAddCardTransaction(key)

        val storedEvents = deserializeEventList(store!!.get(key))

        val expectedEvents = listOf(
            Event.CardTransactionRetrievalRequested(key),
            Event.CardTransactionAdded(key, lunchTransaction),
            Event.TransactionMarkedAsLunch(key, lunchTransaction.id)
        )

        assertEquals(expectedEvents, storedEvents)
    }

    @After
    fun tearDown() {
        testDriver!!.close()
    }

    private fun publishRetrieveCardTransactions(key: String) {
        val command = Command.RetrieveCardTransactions(key)
        publishCommand(key, command)
    }

    private fun publishAddCardTransaction(key: String) {
        val command = Command.AddCardTransaction(key, lunchTransaction)
        publishCommand(key, command)
    }

    private fun publishCommand(key: String, command: Command) {
        testDriver!!.pipeInput(recordFactory.create(CommandsTopic, key, Gson().toJson(command)))
    }

}