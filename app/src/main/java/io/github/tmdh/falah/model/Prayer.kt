package io.github.tmdh.falah.model

import java.time.LocalDateTime
import java.util.Date

data class Prayer(
    val name: String,
    val startTime: Date,
    val endTime: Date,
    var alarmTime: Pair<Int,Int>? = null
)
