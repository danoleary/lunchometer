package com.lunchometer.domain.commandhandlers

import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import com.lunchometer.shared.InternalCommandResponse

fun handle(events: List<Event>, command: Command): InternalCommandResponse {
    return when(command.type) {
        Command.AddCardTransaction::class.java.simpleName ->
            handle(events, command as Command.AddCardTransaction)
        Command.RetrieveCardTransactions::class.java.simpleName ->
            handle(events, command as Command.RetrieveCardTransactions)
        else -> throw Exception()
    }
}

