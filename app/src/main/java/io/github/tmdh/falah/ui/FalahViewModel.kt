package io.github.tmdh.falah.ui

import androidx.lifecycle.ViewModel
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import io.github.tmdh.falah.model.Prayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class FalahViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FalahUiState())
    val uiState: StateFlow<FalahUiState> = _uiState.asStateFlow()
    private val madhab = Madhab.HANAFI
    private val coordinates = Coordinates(22.3752, 91.8349)
    private val date: DateComponents = DateComponents.from(Date())
    private val prayerTimes = PrayerTimes(coordinates, date, calculationMethod());

    fun reloadPrayers() {
        val allPrayers = listOf<Prayer>(
            Prayer("Fajr", prayerTimes.fajr, prayerTimes.sunrise.addMinutes(-1)),
            Prayer("Dhuhr", prayerTimes.dhuhr, prayerTimes.asr.addMinutes(-1)),
            Prayer("Asr", prayerTimes.asr, prayerTimes.maghrib.addMinutes(-4)),
            Prayer("Maghrib", prayerTimes.maghrib, prayerTimes.isha.addMinutes(-1)),
            Prayer("Isha", prayerTimes.isha, prayerTimes.fajr.addMinutes(-5))
        )
        _uiState.value = FalahUiState(allPrayers)
    }
    init {
        reloadPrayers()
    }
    fun calculationMethod(): CalculationParameters {
        return CalculationMethod.KARACHI.parameters.apply { this.madhab = madhab }
    }
}

fun Date.addMinutes(minutes: Long) : Date {
    val time = this.time + minutes * 60 * 1000;
    return Date(time)
}
