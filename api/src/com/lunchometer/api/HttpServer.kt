package com.lunchometer.api

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.lunchometer.shared.Command
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
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.io.*
import java.text.DateFormat
import java.util.concurrent.TimeUnit

fun startServer(streams: KafkaStreams) {

    val config = HoconApplicationConfig(ConfigFactory.load())
    val port = config.property("ktor.deployment.port").getString().toInt()
    val domain = config.property("jwt.domain").getString()
    val audience = config.property("jwt.audience").getString()

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
                    val userId = "dan"
                    val command = Command.RetrieveCardTransactions(userId)
                    produce("commands", userId, command)
                    call.respond(HttpStatusCode.OK)
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

private fun makeJwkProvider(issuer: String): JwkProvider = JwkProviderBuilder(issuer)
    .cached(10, 24, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()