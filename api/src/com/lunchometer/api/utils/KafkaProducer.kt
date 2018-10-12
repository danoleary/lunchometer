package com.lunchometer.api.utils

import com.lunchometer.avro.RetrieveCardTransactions
import com.lunchometer.shared.getKafkaConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

fun produce(topic: String, key: String, value: RetrieveCardTransactions) {

    val properties = getKafkaConfig("lunchometer-api-producer")

    val producer = KafkaProducer<String, RetrieveCardTransactions>(properties)

    val record = ProducerRecord(topic, key, value)

    producer.send(record)

    producer.close()
}