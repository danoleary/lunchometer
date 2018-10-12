package com.lunchometer.api

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.lunchometer.api.endpoints.transactionsRequest
import com.typesafe.config.ConfigFactory
import io.ktor.application.install
import io.ktor.application.call
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.DefaultHeaders
import io.ktor.features.Compression
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.routing.routing
import io.ktor.routing.post
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import org.apache.kafka.streams.KafkaStreams
import java.text.DateFormat
import java.util.concurrent.TimeUnit

val logger = KotlinLogging.logger {}

fun startServer(streams: KafkaStreams) {

    val config = HoconApplicationConfig(ConfigFactory.load())
    val port = config.property("ktor.deployment.port").getString().toInt()
//    val domain = config.property("jwt.domain").getString()
//    val audience = config.property("jwt.audience").getString()

    val server = embeddedServer(Netty, port = port) {

//        install(Authentication) {
//            val jwkProvider = makeJwkProvider(domain)
//            jwt {
//                verifier(jwkProvider)
//                validate { credential ->
//                    if (credential.payload.audience.contains(audience))
//                        JWTPrincipal(credential.payload)
//                    else
//                        null
//                }
//            }
//        }
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
            //authenticate {
//                get("/transactions/{userId}") {
//                    getTransactions(streams, call)
//                }
                post("/transactions/fetch/{userId}") {
                    transactionsRequest(streams, call)
                }
            //}
        }
    }
    server.start(wait = true)
}

//private fun makeJwkProvider(issuer: String): JwkProvider = JwkProviderBuilder(issuer)
//    .cached(10, 24, TimeUnit.HOURS)
//    .rateLimited(10, 1, TimeUnit.MINUTES)
//    .build()