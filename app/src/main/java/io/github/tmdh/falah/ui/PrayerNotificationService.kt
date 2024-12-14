package io.github.tmdh.falah.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import io.github.tmdh.falah.MainActivity
import io.github.tmdh.falah.R

class PrayerNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(prayer: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(context, 1, activityIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, PRAYER_CHANNEL_ID)
            .setContentTitle("It's time for $prayer prayer.")
            .setContentText("27x bonus for $prayer prayer!")
            .setContentIntent(activityPendingIntent)
            .setSmallIcon(R.drawable.prayer_times_24px)
            .build()
        notificationManager.notify(1, notification)
    }

    companion object {
        const val PRAYER_CHANNEL_ID = "prayer_channel"
    }
}