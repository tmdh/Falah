package io.github.tmdh.falah.ui

interface AlarmScheduler {
    fun schedule(name: String, alarmTime: Pair<Int, Int>?)
    fun cancel(name: String)
}