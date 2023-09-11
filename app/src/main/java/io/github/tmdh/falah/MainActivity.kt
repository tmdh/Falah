package io.github.tmdh.falah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tencent.mmkv.MMKV
import io.github.tmdh.falah.model.Prayer
import io.github.tmdh.falah.ui.AlarmScheduler
import io.github.tmdh.falah.ui.AndroidAlarmScheduler
import io.github.tmdh.falah.ui.FalahViewModel
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    private val viewModel: FalahViewModel by viewModels {
        FalahViewModelFactory(AndroidAlarmScheduler(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootDir = MMKV.initialize(this)
        println("MMKV root: $rootDir")

        setContent {
            AppTheme {
                FalahApp(falahViewModel = viewModel)
            }
        }
    }
}

class FalahViewModelFactory(private val alarmScheduler: AlarmScheduler) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FalahViewModel::class.java)) {
            return FalahViewModel(alarmScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FalahApp(modifier: Modifier = Modifier, falahViewModel: FalahViewModel) {
    val falahUiState by falahViewModel.uiState.collectAsState()
    var showTimePicker by remember {
        mutableStateOf(false)
    }
    var showTimePickerToUpdate by remember {
        mutableStateOf(false)
    }
    val timePickerState = rememberTimePickerState()
    var pickingPrayer by remember {
        mutableStateOf<Prayer?>(null)
    }

    Scaffold { contentPadding ->
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Prayer times",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            falahUiState.allPrayers.forEach { prayer ->
                PrayerCard(
                    prayer = prayer,
                    onBellClick = {
                        showTimePicker = true
                        pickingPrayer = prayer
                    },
                    onUpdateClick = {
                        showTimePicker = true
                        showTimePickerToUpdate = true
                        pickingPrayer = prayer
                    })
            }
        }
    }

    if (showTimePicker) {
        pickingPrayer?.let {
            TimePickerDialog(
                onCancel = {
                    showTimePicker = false
                    showTimePickerToUpdate = false
                },
                onConfirm = {
                    falahViewModel.updateAlarmTime(
                        prayer = it, time = Pair(timePickerState.hour, timePickerState.minute)
                    )
                    showTimePicker = false
                    showTimePickerToUpdate = false
                },
                onRemove = {
                    falahViewModel.updateAlarmTime(prayer = it, time = null)
                    showTimePicker = false
                    showTimePickerToUpdate = false
                },
                showTimePickerToUpdate = showTimePickerToUpdate
            ) {
                TimePicker(timePickerState)
            }
        }
    }
}

@Composable
fun PrayerCard(
    prayer: Prayer,
    onBellClick: (prayer: Prayer) -> Unit,
    onUpdateClick: (prayer: Prayer) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        val formatter = SimpleDateFormat("hh:mm")
        formatter.timeZone = TimeZone.getTimeZone("Asia/Dhaka")
        val startTimeString = formatter.format(prayer.startTime)
        val endTimeString = formatter.format(prayer.endTime)
        Row(
            Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(prayer.name)
            Spacer(Modifier.weight(1.0f))
            val alarmTime = prayer.alarmTime
            if (alarmTime != null) {
                AssistChip(onClick = { onUpdateClick(prayer) },
                    label = { Text("${alarmTime.first}:${alarmTime.second}") },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.notifications_48px),
                            contentDescription = null,
                            modifier = Modifier.width(18.dp)
                        )
                    })
            } else {
                IconButton(onClick = { onBellClick(prayer) }) {
                    Icon(
                        painterResource(id = R.drawable.notifications_off_48px),
                        contentDescription = null,
                        modifier = Modifier.width(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text("$startTimeString - $endTimeString")
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onRemove: () -> Unit,
    toggle: @Composable () -> Unit = {},
    showTimePickerToUpdate: Boolean,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    if (showTimePickerToUpdate) {
                        TextButton(onClick = onRemove) {
                            Text(text = "Remove alarm")
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}
