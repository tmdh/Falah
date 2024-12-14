package io.github.tmdh.falah.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import io.github.tmdh.falah.model.Prayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun timeAsMillis(time: Pair<Int, Int>) : Long {
    val now = LocalDateTime.now()
    val alarmTimeToday = LocalDateTime.of(
        LocalDate.now(),
        java.time.LocalTime.of(time.first, time.second)
    )
    val alarmTime = if (alarmTimeToday.isBefore(now)) {
        // If the alarm time is earlier than the current time, schedule for tomorrow
        alarmTimeToday.plusDays(1)
    } else {
        alarmTimeToday
    }
    return alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("MissingPermission")
    override fun schedule(name: String, alarmTime: Pair<Int, Int>?) {
        alarmTime?.let {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", name)
                putExtra("PRAYER_HOUR", it.first.toString())
                putExtra("PRAYER_MINUTE", it.second.toString())
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                name.hashCode(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeAsMillis(it),
                pendingIntent
            )
        } ?: run {
            cancel(name)
        }
    }

    override fun cancel(name: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                name.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}

