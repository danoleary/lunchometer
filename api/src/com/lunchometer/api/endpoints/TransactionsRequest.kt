package com.lunchometer.api.endpoints

import com.lunchometer.api.*
import com.lunchometer.api.utils.produce
import com.lunchometer.api.utils.retryIO
import com.lunchometer.shared.Command
import com.lunchometer.shared.CommandResponse
import com.lunchometer.shared.deserializeCommandResponseList
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*

suspend fun transactionsRequest(streams: KafkaStreams, call: ApplicationCall) {
    logger.info { "Starting fetchTransactions post request" }

    val userId = call.parameters["userId"]
    if(userId == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@transactionsRequest
    }

    val command = Command.RetrieveCardTransactions(userId)

    logger.info { "Producing command with id ${command.id}" }
    produce("commands", userId, command)
    logger.info { "Produced command with id ${command.id}" }

    val response = retryIO { getCommandResponse(streams, userId, command.id) }
    when(response) {
        null ->
            call.respond(HttpStatusCode.InternalServerError, "Command wasn't handled in time")
        else -> {
            if(response.success) {
                logger.info { "Command response was successful, returning ok" }
                call.respond(HttpStatusCode.OK)
            } else {
                logger.info { "Command failed, returning internal server error" }
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
    logger.info { "Finished fetchTransactions post request" }
}

private fun getCommandResponse(streams: KafkaStreams, key: String, commandId: UUID): CommandResponse? {
    val store: ReadOnlyKeyValueStore<String, String> =
        streams.store(CommandResponseStore, QueryableStoreTypes.keyValueStore())
    val responses = store.get(key)
    return when(responses) {
        null -> null
        else -> {
            val deserialized = deserializeCommandResponseList(responses)
            return deserialized.singleOrNull{ it.commandId == commandId }
        }
    }
}
