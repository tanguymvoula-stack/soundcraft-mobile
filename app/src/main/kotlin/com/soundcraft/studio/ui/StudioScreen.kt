package com.soundcraft.studio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soundcraft.core.StepPattern
import com.soundcraft.core.Tempo
import com.soundcraft.studio.StudioUiState
import com.soundcraft.studio.StudioViewModel

private val CardShape = RoundedCornerShape(12.dp)
private val CellShape = RoundedCornerShape(6.dp)

@Composable
fun StudioScreen(viewModel: StudioViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Palette.Background)
            .safeDrawingPadding(),
    ) {
        // Seuil unique pour passer de la disposition téléphone à la disposition DeX/tablette.
        val wide = maxWidth >= 720.dp
        val pad = if (wide) 20.dp else 12.dp

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(pad),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Header()
            TransportCard(
                state = state,
                onTogglePlay = viewModel::togglePlay,
                onBpm = viewModel::setBpm,
            )
            SequencerCard(
                state = state,
                wide = wide,
                onToggleStep = viewModel::toggleStep,
            )
            Text(
                "Fondation : un son (kick), un séquenceur 16 pas. Le reste viendra ensuite.",
                color = Palette.TextMuted,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun Header() {
    Column {
        Text("SOUNDCRAFT STUDIO", color = Palette.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text("Untitled 01", color = Palette.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Card(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Palette.Surface, CardShape)
            .border(1.dp, Palette.Border, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, color = Palette.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        content()
    }
}

@Composable
private fun TransportCard(state: StudioUiState, onTogglePlay: () -> Unit, onBpm: (Float) -> Unit) {
    Card("TRANSPORT") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                Modifier
                    .size(width = 120.dp, height = 56.dp)
                    .background(if (state.playing) Palette.SurfaceHigh else Palette.Accent, CellShape)
                    .border(1.dp, Palette.Accent, CellShape)
                    .clickable(role = Role.Button, onClickLabel = if (state.playing) "Arrêter" else "Lancer", onClick = onTogglePlay),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (state.playing) "■  STOP" else "▶  PLAY",
                    color = if (state.playing) Palette.Accent else Palette.OnAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column {
                Text("TEMPO", color = Palette.TextMuted, fontSize = 12.sp)
                Text("${state.bpm.toInt()} BPM", color = Palette.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Slider(
            value = state.bpm,
            onValueChange = onBpm,
            valueRange = Tempo.MIN_BPM..Tempo.MAX_BPM,
            colors = SliderDefaults.colors(
                thumbColor = Palette.Accent,
                activeTrackColor = Palette.Accent,
                inactiveTrackColor = Palette.SurfaceHigh,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.engineError != null) {
            Text(state.engineError, color = Palette.Error, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SequencerCard(state: StudioUiState, wide: Boolean, onToggleStep: (Int) -> Unit) {
    Card("KICK  •  16 PAS") {
        val perRow = if (wide) StepPattern.STEP_COUNT else StepPattern.STEP_COUNT / 2
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (rowStart in 0 until StepPattern.STEP_COUNT step perRow) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in rowStart until rowStart + perRow) {
                        StepCell(
                            index = i,
                            active = state.pattern.steps[i],
                            playhead = state.currentStep == i,
                            height = if (wide) 64.dp else 52.dp,
                            onClick = { onToggleStep(i) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.StepCell(index: Int, active: Boolean, playhead: Boolean, height: Dp, onClick: () -> Unit) {
    // Les pas sont groupés par temps (4 pas) : les groupes pairs sont un peu plus clairs.
    val idle = if ((index / 4) % 2 == 0) Palette.SurfaceHigh else Palette.Border
    val description = "Pas ${index + 1}, ${if (active) "actif" else "inactif"}"
    Box(
        Modifier
            .weight(1f)
            .height(height)
            .background(if (active) Palette.Accent else idle, CellShape)
            .border(if (playhead) 3.dp else 1.dp, if (playhead) Palette.TextPrimary else Palette.Border, CellShape)
            .semantics { contentDescription = description }
            .clickable(role = Role.Checkbox, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "${index + 1}",
            color = if (active) Palette.OnAccent else Palette.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
