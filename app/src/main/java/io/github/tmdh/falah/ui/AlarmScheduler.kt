package io.github.tmdh.falah.ui

import io.github.tmdh.falah.model.Prayer

interface AlarmScheduler {
    fun schedule(item: Prayer)
    fun cancel(item: Prayer)
}