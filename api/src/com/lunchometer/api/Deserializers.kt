package com.lunchometer.api

import com.google.gson.Gson
import com.lunchometer.api.commandhandlers.InternalCommandResponse
import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import org.json.JSONObject
import org.json.simple.parser.JSONParser
import java.util.*

fun deserializeEventList(json: String): List<Event> {
    val jsonArray = org.json.JSONArray(json)
    return jsonArray.map { mapToEvent(it as JSONObject) }
}

fun deserializeCommand(json: String): Command {
    val parsed = JSONParser().parse(json) as org.json.simple.JSONObject
    val type = parsed.get("type") as String
    return when(type) {
        Command.AddCardTransaction::class.java.simpleName -> Gson().fromJson(json, Command.AddCardTransaction::class.java)
        Command.RetrieveCardTransactions::class.java.simpleName -> Gson().fromJson(json, Command.RetrieveCardTransactions::class.java)
        else -> throw Exception()
    }
}

fun deserializeInternalCommandResponse(json: String): InternalCommandResponse {
    val parsed = JSONObject(json)
    val eventList = parsed.get("events").toString()
    val events = deserializeEventList(eventList)
    return InternalCommandResponse(
        UUID.fromString(parsed.getString("commandId")),
        parsed.getBoolean("success"),
        events)
}

private fun mapToEvent(jobject: JSONObject): Event {
    val type = jobject.getString("type")
    return when(type) {
        Event.CardTransactionRetrievalRequested::class.java.simpleName -> Gson().fromJson(jobject.toString(), Event.CardTransactionRetrievalRequested::class.java)
        Event.CardTransactionAdded::class.java.simpleName -> Gson().fromJson(jobject.toString(), Event.CardTransactionAdded::class.java)
        Event.TransactionMarkedAsLunch::class.java.simpleName -> Gson().fromJson(jobject.toString(), Event.TransactionMarkedAsLunch::class.java)
        else -> throw Exception()
    }
}