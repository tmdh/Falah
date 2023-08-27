package io.github.tmdh.falah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tmdh.falah.ui.FalahViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                FalahApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FalahApp(modifier: Modifier = Modifier, falahViewModel: FalahViewModel = FalahViewModel()) {
    val falahUiState by falahViewModel.uiState.collectAsState()

    Scaffold { contentPadding ->
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Prayer times",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            falahUiState.allPrayers.forEach { prayer ->
                PrayerCard(
                    prayerName = prayer.name,
                    startTime = prayer.startTime,
                    endTime = prayer.endTime
                )
            }
        }
    }
}

@Composable
fun PrayerCard(prayerName: String, startTime: Date, endTime: Date) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        val formatter = SimpleDateFormat("hh:mm")
        formatter.timeZone = TimeZone.getTimeZone("Asia/Dhaka")
        val startTimeString = formatter.format(startTime)
        val endTimeString = formatter.format(endTime)
        Row(
            Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(prayerName)
            Spacer(Modifier.weight(1.0f))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painterResource(id = R.drawable.notifications_off_48px),
                    contentDescription = null,
                    modifier = Modifier.width(24.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("$startTimeString - $endTimeString")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        FalahApp()
    }
}