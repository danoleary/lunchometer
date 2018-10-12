package com.lunchometer.shared

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsConfig
import java.util.Properties

fun getKafkaConfig(applicationId: String): Properties {
    val streamsConfiguration = Properties()

    streamsConfiguration.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka1:9092")

    streamsConfiguration.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, applicationId)
    streamsConfiguration.setProperty(StreamsConfig.CLIENT_ID_CONFIG, applicationId)

    streamsConfiguration.setProperty(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    streamsConfiguration.setProperty(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer::class.java.name)

    streamsConfiguration.setProperty(
        StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
    streamsConfiguration.setProperty(
        StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde::class.java.name)
    streamsConfiguration.setProperty("schema.registry.url", "http://localhost:8081")
    streamsConfiguration.setProperty(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY,
        io.confluent.kafka.serializers.subject.RecordNameStrategy::class.java.name)

    streamsConfiguration.setProperty(ProducerConfig.ACKS_CONFIG, "all")
    streamsConfiguration.setProperty(ProducerConfig.RETRIES_CONFIG, "3")
    streamsConfiguration.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1")
    streamsConfiguration.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000)
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)

    return streamsConfiguration
}

