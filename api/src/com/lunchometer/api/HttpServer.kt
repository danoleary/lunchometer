package com.lunchometer.api

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.lunchometer.shared.Command
import com.lunchometer.shared.CommandResponse
import com.lunchometer.shared.deserializeCommandResponseList
import com.lunchometer.shared.deserializeEventList
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.config.HoconApplicationConfig
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.experimental.delay
import mu.KotlinLogging
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.io.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun startServer(streams: KafkaStreams) {

    val config = HoconApplicationConfig(ConfigFactory.load())
    val port = config.property("ktor.deployment.port").getString().toInt()
    val domain = config.property("jwt.domain").getString()
    val audience = config.property("jwt.audience").getString()

    val logger = KotlinLogging.logger {}

    val server = embeddedServer(Netty, port = port) {

        install(Authentication) {
            val jwkProvider = makeJwkProvider(domain)
            jwt {
                verifier(jwkProvider)
                validate { credential ->
                    if (credential.payload.audience.contains(audience))
                        JWTPrincipal(credential.payload)
                    else
                        null
                }
            }
        }
        install(CORS) {
            anyHost()
            allowCredentials = true
            header(HttpHeaders.Authorization)
        }
        install(DefaultHeaders)
        install(Compression)
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                setDateFormat(DateFormat.LONG)
            }
        }
        routing {
            authenticate {
                get("/api") {
                    val store: ReadOnlyKeyValueStore<String, String> =
                        streams.store(CommandResponseStore, QueryableStoreTypes.keyValueStore())
                    val json = store.get("dan")
                    val events = deserializeEventList(json)
                    val groupedTransactions = groupByWeek(events)
                    call.respond(groupedTransactions)
                }
                post("/fetchTransactions") {
                    logger.info { "Starting fetchTransactions post request" }
                    val userId = "dan"
                    val command = Command.RetrieveCardTransactions(userId)
                    logger.info { "Producing comman with id ${command.id}" }
                    produce("commands", userId, command)
                    val response = retryIO { getCommandResponse(streams, userId, command.id) }
                    when(response) {
                        null ->
                            call.respond(HttpStatusCode.InternalServerError, "Command wasn't handled in time")
                        else -> {
                            if(response.success) {
                                logger.info { "Command response was successful, returning ok" }
                                call.respond(HttpStatusCode.OK)
                            } else {
                                logger.info { "Command failed, returning bad request" }
                                call.respond(HttpStatusCode.BadRequest)
                            }
                        }
                    }
                    logger.info { "Finished fetchTransactions post request" }
                }
            }
            static("static") {
                staticRootFolder = File("webapp/build/static")
                static("css") {
                    files("css")
                }
                static("js") {
                    files("js")
                }
            }
            static("/") {
                staticRootFolder = File("webapp/build")
                default("index.html")
            }
            static("callback") {
                staticRootFolder = File("webapp/build")
                default("index.html")
            }
        }
    }
    server.start(wait = true)
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

suspend fun <T> retryIO(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = 100, // 0.1 second
    maxDelay: Long = 10000,    // 1 second
    factor: Double = 2.0,
    block: suspend () -> T): T
{
    var currentDelay = initialDelay
    repeat(times - 1) {
        val response = block()
        if(response != null) {
           return response
        } else {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
    return block()
}

private fun makeJwkProvider(issuer: String): JwkProvider = JwkProviderBuilder(issuer)
    .cached(10, 24, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()