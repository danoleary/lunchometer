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

private val apiUrl = "https://api.starlingbank.com/api/v1"
private val token = ""


fun getAllTransactions(): List<StarlingTransaction> {
    val account = getAccount()
    val startDate = account.createdAt
    val transactions = getTransactions(startDate, LocalDateTime.now())
    return transactions.sortedBy { it.created }
}

private fun get(path: String) =
    khttp.get(
        url = "$apiUrl/$path",
        headers = mapOf("Authorization" to "Bearer $token"))

private fun getTransactions(from: LocalDateTime, to: LocalDateTime): List<StarlingTransaction> {
    val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
    val result = get("transactions/mastercard?from=${from.format(formatter)}&to=${to.format(formatter)}")
    val content = gson.fromJson(result.text, StarlingTransactionResponse::class.java)
    return content._embedded.transactions
}

private fun getAccount(): StarlingAccount {
    val result = get("accounts")
    val content = gson.fromJson(result.text, StarlingAccount::class.java)
    return content
}