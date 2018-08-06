package com.lunchometer.api

import com.google.gson.Gson
import com.lunchometer.shared.CommandResponse
import com.lunchometer.shared.*
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueStore
import org.junit.Before
import org.junit.Test
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.After
import java.util.*
import kotlin.test.assertEquals

class TopologyTests {

    private var testDriver: TopologyTestDriver? = null
    private var store: KeyValueStore<String, String>? = null

    private val recordFactory = ConsumerRecordFactory<String, String>(StringSerializer(), StringSerializer())

    private val key = "myKey"

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

        store = (testDriver as TopologyTestDriver).getKeyValueStore<String, String>(CommandResponseStore)
    }

    @Test
    fun `command response with no existing state should be stored`() {

        val commandResponse = CommandResponse(UUID.randomUUID(), true)

        publishCommandResponse(key, commandResponse)

        val storedCommandResponses = deserializeCommandResponseList(store!!.get(key))

        assertEquals(listOf(commandResponse), storedCommandResponses)
    }

    @Test
    fun `command response with existing state should be update state`() {

        val commandResponse = CommandResponse(UUID.randomUUID(), true)
        publishCommandResponse(key, commandResponse)

        val newCommandResponse = CommandResponse(UUID.randomUUID(), false)
        publishCommandResponse(key, newCommandResponse)

        val storedCommandResponses = deserializeCommandResponseList(store!!.get(key))

        assertEquals(listOf(commandResponse, newCommandResponse), storedCommandResponses)
    }

    @After
    fun tearDown() {
        testDriver!!.close()
    }

    private fun publishCommandResponse(key: String, commandResponse: CommandResponse) {
        testDriver!!.pipeInput(
            recordFactory.create(CommandResponsesTopic, key, Gson().toJson(commandResponse)))
    }

}