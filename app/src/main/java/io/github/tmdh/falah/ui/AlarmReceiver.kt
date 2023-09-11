package io.github.tmdh.falah.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra("PRAYER_NAME")
        println(prayerName)
    }
}