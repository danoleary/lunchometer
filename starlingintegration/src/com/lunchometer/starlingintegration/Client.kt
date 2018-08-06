package com.lunchometer.starlingintegration

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer<LocalDateTime> {
    json, _, _ ->
    ZonedDateTime.parse(json.asJsonPrimitive.asString).toLocalDateTime()
}).create()

private val logger = KotlinLogging.logger {}

private const val apiUrl = "https://api.starlingbank.com/api/v1"

fun getAllTransactions(token: String): List<StarlingTransaction> {
    val account = getAccount(token)
    val startDate = account.createdAt
    val transactions = getTransactions(token, startDate, LocalDateTime.now())
    return transactions.sortedBy { it.created }
}

private fun get(token: String, path: String) =
    khttp.get(
        url = "$apiUrl/$path",
        headers = mapOf("Authorization" to "Bearer $token"))

private fun getTransactions(token: String, from: LocalDateTime, to: LocalDateTime): List<StarlingTransaction> {
    val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
    val result = get(token, "transactions/mastercard?from=${from.format(formatter)}&to=${to.format(formatter)}")
    val content = gson.fromJson(result.text, StarlingTransactionResponse::class.java)
    return content._embedded.transactions
}

private fun getAccount(token: String): StarlingAccount {
    val result = get(token, "accounts")
    return gson.fromJson(result.text, StarlingAccount::class.java)
}