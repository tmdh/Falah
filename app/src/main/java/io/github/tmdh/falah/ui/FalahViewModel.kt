package io.github.tmdh.falah.ui

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.tencent.mmkv.MMKV
import io.github.tmdh.falah.model.Prayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.Date

class FalahViewModel(private val alarmScheduler: AlarmScheduler) : ViewModel() {
    private val _uiState = MutableStateFlow(FalahUiState())
    val uiState: StateFlow<FalahUiState> = _uiState.asStateFlow()
    private val userMadhab = Madhab.HANAFI
    private val coordinates = Coordinates(22.3752, 91.8349)
    private val date: DateComponents = DateComponents.from(Date())
    private val prayerTimes = PrayerTimes(coordinates, date, calculationMethod())
    private val kv = MMKV.defaultMMKV()

    init {
        loadPrayers()
    }

    fun loadPrayers() {
        val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
        val allPrayers = listOf(
            Prayer(
                prayerNames[0],
                prayerTimes.fajr,
                prayerTimes.sunrise.addMinutes(-1),
                getTimeStored(prayerNames[0])
            ),
            Prayer(
                prayerNames[1],
                prayerTimes.dhuhr,
                prayerTimes.asr.addMinutes(-1),
                getTimeStored(prayerNames[1])
            ),
            Prayer(
                prayerNames[2],
                prayerTimes.asr,
                prayerTimes.maghrib.addMinutes(-1),
                getTimeStored(prayerNames[2])
            ),
            Prayer(
                prayerNames[3],
                prayerTimes.maghrib.addMinutes(3),
                prayerTimes.isha.addMinutes(-1),
                getTimeStored(prayerNames[3])
            ),
            Prayer(
                prayerNames[4],
                prayerTimes.isha,
                prayerTimes.fajr.addMinutes(-5),
                getTimeStored(prayerNames[4])
            )
        )
        _uiState.value = FalahUiState(allPrayers)
    }

    fun setAlarms() {
        _uiState.value.allPrayers.forEach { prayer ->
            val alarmTime = prayer.alarmTime
            if (getTimeStored(prayer.name) != alarmTime) {
                if (alarmTime != null) {
                    kv.encode(prayer.name, "${alarmTime.first}:${alarmTime.second}")
                    alarmScheduler.schedule(prayer.name, alarmTime)
                } else {
                    kv.encode(prayer.name, null as String?)
                    alarmScheduler.cancel(prayer.name)
                }
            }
        }
    }

    fun getTimeStored(prayerName: String): Pair<Int, Int>? {
        val timeString = kv.decodeString(prayerName);
        return timeString?.let {
            val hm = timeString.split(':').map { it.toInt() }
            return Pair(hm[0], hm[1])
        }
    }

    fun calculationMethod(): CalculationParameters {
        return CalculationMethod.KARACHI.parameters.apply { this.madhab = userMadhab }
    }

    fun updateAlarmTime(prayer: Prayer, time: Pair<Int, Int>?) {
        _uiState.update { currentState ->
            currentState.copy(
                allPrayers = currentState.allPrayers.map {
                    if (it == prayer) {
                        it.copy(alarmTime = time)
                    } else {
                        it
                    }
                }
            )
        }
        setAlarms()
    }
}

fun Date.addMinutes(minutes: Long): Date {
    val time = this.time + minutes * 60 * 1000
    return Date(time)
}
