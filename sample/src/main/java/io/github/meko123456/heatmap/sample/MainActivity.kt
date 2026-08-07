package io.github.meko123456.heatmap.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.meko123456.heatmap.ContributionHeatmap
import java.time.LocalDate
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val today = LocalDate.now().toEpochDay()
            val random = Random(7)
            val demo = (0..180L).mapNotNull { back ->
                val day = today - back
                val p = if (back < 21) 0.85 else 0.55
                if (random.nextDouble() < p) day to random.nextInt(1, 12) else null
            }.toMap()

            MaterialTheme(colorScheme = darkColorScheme()) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                    ) {
                        Text("heatmap-compose", style = MaterialTheme.typography.headlineMedium)

                        Section("Default — GitHub greens, 20 weeks") {
                            ContributionHeatmap(counts = demo, endDay = today)
                        }
                        Section("26 weeks") {
                            ContributionHeatmap(counts = demo, endDay = today, weeks = 26)
                        }
                        Section("Custom palette (oranges)") {
                            ContributionHeatmap(
                                counts = demo,
                                endDay = today,
                                levelColors = listOf(
                                    Color(0xFF662D00),
                                    Color(0xFF993D00),
                                    Color(0xFFE05D00),
                                    Color(0xFFFF9500),
                                ),
                            )
                        }
                        Section("Sparse data") {
                            ContributionHeatmap(
                                counts = demo.filterKeys { it % 5L == 0L },
                                endDay = today,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
    )
    Surface { content() }
}
