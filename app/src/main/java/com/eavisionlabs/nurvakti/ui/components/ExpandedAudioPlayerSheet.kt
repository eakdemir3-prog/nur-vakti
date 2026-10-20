package com.eavisionlabs.nurvakti.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eavisionlabs.nurvakti.data.PlaybackStatus
import com.eavisionlabs.nurvakti.data.PlayerState
import com.eavisionlabs.nurvakti.ui.theme.EmeraldCardElevated
import com.eavisionlabs.nurvakti.ui.theme.EmeraldDeep
import com.eavisionlabs.nurvakti.ui.theme.GoldChampagne
import com.eavisionlabs.nurvakti.ui.theme.GoldLight
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedAudioPlayerSheet(
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onSeekRelative: (Int) -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetSleepTimer: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EmeraldDeep,
        dragHandle = { BottomSheetDefaults.DragHandle(color = GoldChampagne) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Surah Number & Reciter header
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(EmeraldCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${playerState.currentSurahNumber}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldChampagne
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${playerState.currentSurahNameTr} Suresi",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = playerState.currentSurahNameAr,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = GoldLight,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Kâri: ${playerState.currentReciterName}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Timeline Slider
            val maxMs = if (playerState.durationMs > 0) playerState.durationMs.toFloat() else 100f
            val currentMs = playerState.progressMs.toFloat().coerceIn(0f, maxMs)

            Slider(
                value = currentMs,
                onValueChange = { onSeekTo(it.toInt()) },
                valueRange = 0f..maxMs,
                colors = SliderDefaults.colors(
                    thumbColor = GoldChampagne,
                    activeTrackColor = GoldChampagne,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth().testTag("audio_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMillis(playerState.progressMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatMillis(playerState.durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Controls: Prev, Rewind 10s, Play/Pause, Forward 10s, Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPlayPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Önceki Sure",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { onSeekRelative(-10) }) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "10sn Geri",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Big Play/Pause Button
                FilledIconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(68.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = GoldChampagne,
                        contentColor = EmeraldDeep
                    )
                ) {
                    if (playerState.status == PlaybackStatus.BUFFERING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = EmeraldDeep,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (playerState.status == PlaybackStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Oynat / Duraklat",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                IconButton(onClick = { onSeekRelative(10) }) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "10sn İleri",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = onPlayNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Sonraki Sure",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Secondary Controls: Speed & Sleep Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showSpeedDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldChampagne)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Hız",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${playerState.playbackSpeed}x")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = { showSleepTimerDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldChampagne)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Uyku Zamanlayıcı",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (playerState.sleepTimerMinutesRemaining != null)
                            "${playerState.sleepTimerMinutesRemaining} dk" else "Uyku Modu"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Oynatma Hızı") },
            text = {
                Column {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = playerState.playbackSpeed == speed,
                                onClick = {
                                    onSetSpeed(speed)
                                    showSpeedDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${speed}x Hız")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }

    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("Uyku Zamanlayıcısı") },
            text = {
                Column {
                    listOf(
                        "Kapat" to null,
                        "15 Dakika" to 15,
                        "30 Dakika" to 30,
                        "45 Dakika" to 45,
                        "60 Dakika" to 60
                    ).forEach { (label, minutes) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = playerState.sleepTimerMinutesRemaining == minutes,
                                onClick = {
                                    onSetSleepTimer(minutes)
                                    showSleepTimerDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Tamam")
                }
            }
        )
    }
}

private fun formatMillis(millis: Int): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
