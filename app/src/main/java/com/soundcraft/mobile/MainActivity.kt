package com.soundcraft.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val StudioBackground = Color(0xFF101216)
private val PanelBackground = Color(0xFF1B2028)
private val Accent = Color(0xFF65E6B4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Accent,
                background = StudioBackground,
                surface = PanelBackground,
                onPrimary = Color(0xFF07120E),
                onBackground = Color(0xFFF1F4F7),
                onSurface = Color(0xFFF1F4F7)
            )) {
                SoundCraftStudio()
            }
        }
    }
}

@Composable
private fun SoundCraftStudio() {
    var isPlaying by remember { mutableStateOf(false) }
    val steps = remember { mutableStateListOf<Boolean>().apply { repeat(16) { add(it % 4 == 0) } } }

    Surface(modifier = Modifier.fillMaxSize(), color = StudioBackground) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val desktopLayout = maxWidth >= 700.dp
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("SOUNDCRAFT", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("MOBILE STUDIO", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(if (desktopLayout) "DESKTOP / DEX LAYOUT" else "TOUCH LAYOUT", color = Accent, fontSize = 11.sp)
                }

                if (desktopLayout) {
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(modifier = Modifier.weight(1.15f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            ModulePanel("RACK", "Instrument slots") {
                                Text("01  •  Synthesizer", color = Color.White)
                                Text("02  •  Sampler (planned)", color = Color.LightGray)
                                Text("03  •  Effects (planned)", color = Color.LightGray)
                            }
                            ModulePanel("MIXER", "Master output") {
                                Text("Master", color = Color.White)
                                Text("Audio engine not implemented yet", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1.85f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            TransportPanel(isPlaying = isPlaying, onToggle = { isPlaying = !isPlaying })
                            SequencerPanel(steps = steps)
                        }
                    }
                } else {
                    TransportPanel(isPlaying = isPlaying, onToggle = { isPlaying = !isPlaying })
                    SequencerPanel(steps = steps)
                    ModulePanel("RACK", "Instrument slots") {
                        Text("01  •  Synthesizer", color = Color.White)
                        Text("Sampler and effects will be added in later versions", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                Text("Prototype 0.1.0 • Offline-first • No AI in the app", color = Color(0xFF89929F), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TransportPanel(isPlaying: Boolean, onToggle: () -> Unit) {
    ModulePanel("TRANSPORT", "Tempo 120 BPM  •  4/4") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onToggle) { Text(if (isPlaying) "Stop" else "Play preview") }
            Text(if (isPlaying) "Preview state active" else "Ready", color = Accent, fontSize = 12.sp)
        }
        Text("Transport UI only — audio playback is not connected yet.", color = Color.LightGray, fontSize = 12.sp)
    }
}

@Composable
private fun SequencerPanel(steps: SnapshotStateList<Boolean>) {
    ModulePanel("STEP SEQUENCER", "Track 1 • 16 steps • tap/click cells") {
        for (row in 0 until 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 8) {
                    val index = row * 8 + col
                    val active = steps[index]
                    Box(
                        modifier = Modifier.weight(1f).height(38.dp)
                            .background(if (active) Accent else Color(0xFF303743), RoundedCornerShape(5.dp))
                            .border(1.dp, if (active) Accent else Color(0xFF414956), RoundedCornerShape(5.dp))
                            .clickable { steps[index] = !active },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = if (active) Color(0xFF07120E) else Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModulePanel(title: String, subtitle: String, content: @Composable () -> Unit) {
    Surface(color = PanelBackground, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text(subtitle, color = Color(0xFF9CA6B4), fontSize = 11.sp)
            }
            content()
        }
    }
}
