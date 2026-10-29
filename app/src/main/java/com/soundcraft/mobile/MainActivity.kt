package com.soundcraft.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF101216)
private val Panel = Color(0xFF1B2028)
private val Raised = Color(0xFF252B35)
private val Accent = Color(0xFFE9A15B)
private val Muted = Color(0xFF9CA6B4)
private val Drums = Color(0xFF8A5D36)
private val Bass = Color(0xFF426F7B)
private val Synth = Color(0xFF725486)

private enum class ViewMode(val title: String) {
    ARRANGEMENT("Arrangement"), SEQUENCER("Séquenceur"), PIANO("Piano Roll"),
    RACK("Rack"), MIXER("Mixer"), AUDIO("Audio")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Accent, secondary = Bass, background = Bg, surface = Panel,
                onPrimary = Color(0xFF21160B), onBackground = Color.White, onSurface = Color.White
            )) { Studio() }
        }
    }
}

@Composable
private fun Studio() {
    var view by remember { mutableStateOf(ViewMode.ARRANGEMENT) }
    var playing by remember { mutableStateOf(false) }
    var tempo by remember { mutableFloatStateOf(120f) }
    var rackOpen by remember { mutableStateOf(true) }
    var track by remember { mutableStateOf("Synth") }
    val steps = remember { mutableStateListOf<Boolean>().apply { repeat(16) { add(it % 4 == 0) } } }
    val notes = remember { mutableStateListOf<Boolean>().apply { repeat(64) { add(it % 11 == 0) } } }
    val clips = remember { mutableStateListOf<Boolean>().apply { repeat(12) { add(it in 0..2 || it == 5 || it == 8) } } }
    val modules = remember { mutableStateListOf<Boolean>().apply { repeat(4) { add(true) } } }

    Surface(Modifier.fillMaxSize(), color = Bg) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val desktop = maxWidth >= 760.dp
            Column(Modifier.fillMaxSize().padding(if (desktop) 16.dp else 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("SOUNDCRAFT", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 2.4.sp)
                        Text("STUDIO / Untitled 01", color = Color.White, fontSize = if (desktop) 21.sp else 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (desktop) "DESKTOP WORKSPACE" else "TOUCH WORKSPACE", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("V0.2 UI • OFFLINE-FIRST", color = Muted, fontSize = 9.sp)
                    }
                }
                PanelCard("TRANSPORT", "Tempo • signature 4/4") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { playing = !playing }, modifier = Modifier.height(38.dp)) {
                            Text(if (playing) "■ STOP" else "▶ PLAY", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f)) {
                            Text("TEMPO", color = Muted, fontSize = 9.sp)
                            Text("${tempo.toInt()} BPM", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("4 / 4", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(value = tempo, onValueChange = { tempo = it }, valueRange = 60f..180f, modifier = Modifier.fillMaxWidth().height(24.dp))
                    Text(if (playing) "État de lecture simulé — moteur audio non connecté" else "Transport visuel — aucun son n’est généré", color = Muted, fontSize = 10.sp)
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ViewMode.values().forEach { item ->
                        Surface(color = if (view == item) Accent else Panel, shape = RoundedCornerShape(8.dp), modifier = Modifier.clickable { view = item }) {
                            Text(item.title, color = if (view == item) Color(0xFF21160B) else Color.White, fontSize = 12.sp, fontWeight = if (view == item) FontWeight.Bold else FontWeight.Medium, modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp))
                        }
                    }
                }
                if (desktop) {
                    Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.width(175.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PanelCard("PROJET", "Session actuelle") {
                                Text("Untitled 01", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("${tempo.toInt()} BPM • 4/4", color = Muted, fontSize = 11.sp)
                                Text("Sauvegarde à venir", color = Muted, fontSize = 10.sp)
                            }
                            PanelCard("PISTES", "Sélection") {
                                listOf("Drums", "Bass", "Synth", "Audio").forEach { name ->
                                    Surface(color = if (track == name) Color(0xFF3A3026) else Raised, shape = RoundedCornerShape(7.dp), modifier = Modifier.fillMaxWidth().clickable { track = name }) {
                                        Text(name, color = if (track == name) Accent else Color.White, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                                    }
                                }
                            }
                        }
                        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Workspace(view, track, steps, notes, clips, modules)
                        }
                        if (rackOpen) {
                            Column(Modifier.width(220.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Rack(modules, true)
                                PanelCard("MASTER", "Sortie audio") { Text("Moteur audio non connecté", color = Muted, fontSize = 10.sp) }
                            }
                        }
                    }
                } else {
                    Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Workspace(view, track, steps, notes, clips, modules)
                        if (view != ViewMode.RACK) Rack(modules, false)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("INTERFACE PROTOTYPE • AUDIO À VENIR", color = Muted, fontSize = 9.sp)
                    if (desktop) Text(if (rackOpen) "Masquer rack" else "Afficher rack", color = Accent, fontSize = 10.sp, modifier = Modifier.clickable { rackOpen = !rackOpen }.padding(5.dp))
                }
            }
        }
    }
}

@Composable
private fun Workspace(view: ViewMode, track: String, steps: SnapshotStateList<Boolean>, notes: SnapshotStateList<Boolean>, clips: List<Boolean>, modules: MutableList<Boolean>) {
    when (view) {
        ViewMode.ARRANGEMENT -> PanelCard("ARRANGEMENT", "Timeline • sélection : $track") {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                Text("PISTE", color = Muted, fontSize = 9.sp, modifier = Modifier.width(50.dp))
                (1..6).forEach { Text("B$it", color = Muted, fontSize = 9.sp, modifier = Modifier.weight(1f)) }
            }
            listOf("Drums", "Bass", "Synth", "Audio").forEachIndexed { row, name ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(name, color = if (name == track) Accent else Color.White, fontSize = 10.sp, modifier = Modifier.width(50.dp))
                    (0..5).forEach { col ->
                        val active = clips[(row * 3 + col / 2).coerceIn(0, clips.lastIndex)] && (col % 2 == 0 || row == 0)
                        val tint = when (row) { 0 -> Drums; 1 -> Bass; 2 -> Synth; else -> Color(0xFF4B5964) }
                        Box(Modifier.weight(1f).height(42.dp).background(if (active) tint else Raised, RoundedCornerShape(4.dp)).border(1.dp, if (active) tint else Color(0xFF363D47), RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                            if (active) Text("CLIP", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Text("Clips de démonstration uniquement : aucune donnée MIDI/audio n’y est encore attachée.", color = Muted, fontSize = 10.sp)
        }
        ViewMode.SEQUENCER -> PanelCard("STEP SEQUENCER", "Pattern A • 16 pas") {
            Text("KICK", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { i, active ->
                    Box(Modifier.weight(1f).height(46.dp).background(if (active) Accent else Raised, RoundedCornerShape(5.dp)).border(1.dp, if (active) Accent else Color(0xFF414956), RoundedCornerShape(5.dp)).clickable { steps[i] = !active }, contentAlignment = Alignment.Center) {
                        Text("${i + 1}", color = if (active) Color(0xFF21160B) else Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("Édition visuelle uniquement — aucune séquence sonore n’est jouée.", color = Muted, fontSize = 10.sp)
        }
        ViewMode.PIANO -> PanelCard("PIANO ROLL", "$track • grille MIDI conceptuelle") {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    listOf("C5", "B4", "A#4", "A4", "G4", "F4", "E4", "D4").forEach { pitch ->
                        Box(Modifier.width(35.dp).height(25.dp).background(Panel, RoundedCornerShape(2.dp)), contentAlignment = Alignment.Center) { Text(pitch, color = Color.White, fontSize = 8.sp) }
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    (0 until 8).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            (0 until 8).forEach { col ->
                                val index = row * 8 + col
                                val active = notes[index]
                                Box(Modifier.weight(1f).height(25.dp).background(if (active) Bass else Raised, RoundedCornerShape(2.dp)).clickable { notes[index] = !active })
                            }
                        }
                    }
                }
            }
            Text("Notes de démonstration éditables visuellement. MIDI/audio non implémentés.", color = Muted, fontSize = 10.sp)
        }
        ViewMode.RACK -> Rack(modules, false)
        ViewMode.MIXER -> Mixer()
        ViewMode.AUDIO -> PanelCard("AUDIO", "Enregistrement et édition") {
            Box(Modifier.fillMaxWidth().height(130.dp).background(Color(0xFF15191F), RoundedCornerShape(8.dp)).border(1.dp, Color(0xFF353D48), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WAVEFORM", color = Accent, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text("Aucun fichier chargé", color = Muted, fontSize = 11.sp)
                }
            }
            Text("Import, enregistrement, découpe et export seront développés avec le moteur audio.", color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun Rack(modules: MutableList<Boolean>, compact: Boolean) {
    PanelCard("RACK MODULAIRE", if (compact) "Panneau latéral" else "Chaîne de traitement") {
        val names = listOf("01  Synthesizer", "02  Filter", "03  Delay", "04  Reverb")
        val desc = listOf("Source sonore", "Filtrage", "Répétitions", "Ambiance")
        names.forEachIndexed { i, name ->
            Surface(color = if (modules[i]) Raised else Color(0xFF171A20), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().clickable { modules[i] = !modules[i] }) {
                Row(Modifier.padding(9.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(8.dp).background(if (modules[i]) Accent else Muted, RoundedCornerShape(4.dp)))
                    Column(Modifier.weight(1f)) {
                        Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(desc[i], color = Muted, fontSize = 9.sp)
                    }
                    Text(if (modules[i]) "ON" else "OFF", color = if (modules[i]) Accent else Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text("État des modules visuel uniquement : aucun traitement audio connecté.", color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun Mixer() {
    val levels = remember { mutableStateListOf(0.78f, 0.62f, 0.70f, 0.85f) }
    PanelCard("MIXER", "Contrôles visuels") {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("DRUMS", "BASS", "SYNTH", "MASTER").forEachIndexed { i, label ->
                Column(Modifier.weight(1f).background(Raised, RoundedCornerShape(7.dp)).padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(label, color = if (i == 3) Accent else Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Box(Modifier.width(12.dp).height(70.dp).background(Bg, RoundedCornerShape(3.dp)), contentAlignment = Alignment.BottomCenter) {
                        Box(Modifier.fillMaxWidth().height((70 * levels[i]).dp).background(if (i == 3) Accent else Bass, RoundedCornerShape(3.dp)))
                    }
                    Text("${(levels[i] * 100).toInt()}%", color = Color.White, fontSize = 9.sp)
                    Slider(value = levels[i], onValueChange = { levels[i] = it }, modifier = Modifier.height(24.dp))
                }
            }
        }
        Text("Les niveaux ne contrôlent pas encore un moteur audio.", color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun PanelCard(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = Panel, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                Text(subtitle, color = Muted, fontSize = 10.sp)
            }
            content()
        }
    }
}
