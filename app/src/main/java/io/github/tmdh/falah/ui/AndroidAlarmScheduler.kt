package io.github.tmdh.falah.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.tmdh.falah.model.Prayer
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    override fun schedule(item: Prayer) {
        item.alarmTime?.let {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", item.name)
            }
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                5000,
                86400000,
                PendingIntent.getBroadcast(
                    context,
                    item.name.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            /*
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                it.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                    context,
                    item.name.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )*/
        } ?: run {
            cancel(item)
        }
    }

    override fun cancel(item: Prayer) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.name.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}

