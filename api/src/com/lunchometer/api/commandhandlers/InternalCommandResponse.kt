package com.lunchometer.api.commandhandlers

import com.lunchometer.shared.Event
import java.util.*

data class InternalCommandResponse(val commandId: UUID, val success: Boolean, val events: List<Event>)