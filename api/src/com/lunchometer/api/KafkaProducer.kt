package com.lunchometer.api

import com.google.gson.Gson
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties


fun <T> produce(topic: String, key: String, value: T) {
    val properties = Properties()

    // kafka bootstrap server
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://kafka1:9092")
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

    // producer acks
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all") // strongest producing guarantee
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3")
    properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1")
    // leverage idempotent producer from Kafka 0.11 !
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true") // ensure we don't push duplicates

    val producer = KafkaProducer<String, String>(properties)

    val jsonCommand = Gson().toJson(value)

    val record = ProducerRecord(topic, key, jsonCommand)

    producer.send(record)

    producer.close()
}