package io.github.tmdh.falah.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import io.github.tmdh.falah.model.Prayer

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val prayerName = intent?.getStringExtra("PRAYER_NAME")
        val hour = intent?.getStringExtra("PRAYER_HOUR")
        val minute = intent?.getStringExtra("PRAYER_MINUTE")
        val notificationService = context?.let { PrayerNotificationService(it) }
        if (prayerName != null && hour != null && minute != null && notificationService != null) {
            notificationService.showNotification(prayerName)
            val scheduler = AndroidAlarmScheduler(context)
            val hourInt = hour.toInt()
            val minuteInt = minute.toInt()
            scheduler.schedule(prayerName, Pair(hourInt, minuteInt))
        }
    }
}