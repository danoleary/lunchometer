package com.lunchometer.domain.commandhandlers

import com.lunchometer.shared.Command
import com.lunchometer.shared.Event
import com.lunchometer.shared.InternalCommandResponse

fun handle(events: List<Event>, command: Command.RetrieveCardTransactions): InternalCommandResponse =
    InternalCommandResponse(command.id, true, listOf(Event.CardTransactionRetrievalRequested(command.userId)))