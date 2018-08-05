package com.lunchometer.shared

import java.util.*

data class InternalCommandResponse(val commandId: UUID, val success: Boolean, val events: List<Event>)