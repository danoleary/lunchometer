package com.lunchometer.api.commandhandlers

import com.lunchometer.shared.Command
import com.lunchometer.shared.Event

fun handle(events: List<Event>, command: Command.RetrieveCardTransactions): InternalCommandResponse =
    InternalCommandResponse(command.id, true, listOf(Event.CardTransactionRetrievalRequested(command.userId)))