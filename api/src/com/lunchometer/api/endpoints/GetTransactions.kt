//package com.lunchometer.api.endpoints
//
//import com.lunchometer.api.*
//import com.lunchometer.api.readmodels.TransactionWeekDto
//import com.lunchometer.api.readmodels.groupByWeek
//import com.lunchometer.api.utils.retryIO
//import com.lunchometer.shared.deserializeEventList
//import io.ktor.application.ApplicationCall
//import io.ktor.http.HttpStatusCode
//import io.ktor.response.respond
//import org.apache.kafka.streams.KafkaStreams
//import org.apache.kafka.streams.state.QueryableStoreTypes
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
//
//suspend fun getTransactions(streams: KafkaStreams, call: ApplicationCall) {
//    logger.info { "Starting get request" }
//
//    val userId = call.parameters["userId"]
//    if(userId == null) {
//        logger.info { "No user id, returning bad request" }
//        call.respond(HttpStatusCode.BadRequest)
//        return
//    }
//
//    logger.info { "Retrieving transactions for $userId" }
//    val groupedTransactions = retryIO { getTransactionsByWeek(streams, userId) }
//
//    call.respond(groupedTransactions)
//    logger.info { "Finishing end request" }
//}
//
//private fun getTransactionsByWeek(streams: KafkaStreams, key: String): List<TransactionWeekDto> {
//    val store: ReadOnlyKeyValueStore<String, String> =
//        streams.store(ApiEventStore, QueryableStoreTypes.keyValueStore())
//    logger.info { "Getting events from store" }
//    val json = store.get(key)
//    logger.info { "Retrieved events from store" }
//    if(json === null) {
//        return listOf()
//    }
//    val events = deserializeEventList(json)
//
//    return groupByWeek(events)
//}