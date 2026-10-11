/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.constants.SleepTimerDefaultKey
import com.metrolist.music.constants.SleepTimerFadeOutKey
import com.metrolist.music.constants.SleepTimerStopAfterCurrentSongKey
import com.metrolist.music.ui.theme.SpotifyGreen
import com.metrolist.music.utils.rememberPreference
import com.metrolist.music.utils.safeDataStoreEdit
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ModernSleepTimerDialog(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playerConnection = LocalPlayerConnection.current ?: return
    val sleepTimer = playerConnection.service.sleepTimer

    val sleepTimerDefault by rememberPreference(SleepTimerDefaultKey, 30f)
    var sleepTimerValue by remember { mutableFloatStateOf(sleepTimerDefault) }
    val sleepTimerStopAfterCurrentSong by rememberPreference(SleepTimerStopAfterCurrentSongKey, false)
    val sleepTimerFadeOut by rememberPreference(SleepTimerFadeOutKey, false)

    val isTimerActive = remember(sleepTimer?.triggerTime, sleepTimer?.pauseWhenSongEnd) {
        sleepTimer?.isActive ?: false
    }

    var remainingMinutes by remember { mutableIntStateOf(0) }
    LaunchedEffect(isTimerActive) {
        if (isTimerActive) {
            while (isActive) {
                val leftMs = if (sleepTimer?.pauseWhenSongEnd == true) {
                    playerConnection.player.duration - playerConnection.player.currentPosition
                } else {
                    (sleepTimer?.triggerTime ?: 0L) - System.currentTimeMillis()
                }
                remainingMinutes = maxOf(1, ((leftMs + 59_999L) / 60_000L).toInt())
                delay(1000L)
            }
        }
    }

    val isAtDefault by remember {
        derivedStateOf { sleepTimerValue.roundToInt() == sleepTimerDefault.roundToInt() }
    }
    val sleepTimerDefaultSetTemplate = stringResource(R.string.sleep_timer_default_set)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF14161E),
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header with icon and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SpotifyGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.bedtime),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = SpotifyGreen,
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.sleep_timer),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Text(
                            text = if (isTimerActive) "Timer is running" else "Set duration to stop music",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Active Timer Banner (if running)
                AnimatedVisibility(
                    visible = isTimerActive,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = SpotifyGreen.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpotifyGreen.copy(alpha = 0.35f)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (sleepTimer?.pauseWhenSongEnd == true) {
                                        "Stopping after current song"
                                    } else {
                                        "Stops in ~${remainingMinutes} min"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SpotifyGreen,
                                )
                            }

                            Button(
                                onClick = {
                                    sleepTimer?.clear()
                                    Toast.makeText(context, "Sleep timer cancelled", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2A2D39),
                                    contentColor = Color.White,
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel_timer),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }

                // Big Duration Display
                Text(
                    text = pluralStringResource(
                        R.plurals.minute,
                        sleepTimerValue.roundToInt(),
                        sleepTimerValue.roundToInt(),
                    ),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Slider with custom styling
                Slider(
                    value = sleepTimerValue,
                    onValueChange = { sleepTimerValue = it },
                    valueRange = 5f..120f,
                    steps = (120 - 5) / 5 - 1,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = SpotifyGreen,
                        activeTrackColor = SpotifyGreen,
                        inactiveTrackColor = Color(0xFF2A2D3A),
                    ),
                )

                // Quick preset chips (15, 30, 45, 60 min)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val presets = listOf(15, 30, 45, 60)
                    presets.forEach { mins ->
                        val isSelected = sleepTimerValue.roundToInt() == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) SpotifyGreen else Color(0xFF1E212D)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) SpotifyGreen else Color(0xFF2E3242),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { sleepTimerValue = mins.toFloat() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f),
                            )
                        }
                    }
                }

                // Secondary Action Buttons: "End of song" and "Set as default"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // "End of song" button
                    OutlinedButton(
                        onClick = {
                            onDismissRequest()
                            sleepTimer?.start(minute = -1)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF323647)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music_note),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.end_of_song),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                        )
                    }

                    // "Set as default" button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                context.safeDataStoreEdit { settings ->
                                    settings[SleepTimerDefaultKey] = sleepTimerValue
                                }
                            }
                            Toast.makeText(
                                context,
                                String.format(sleepTimerDefaultSetTemplate, sleepTimerValue.roundToInt()),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAtDefault) SpotifyGreen.copy(alpha = 0.2f) else Color(0xFF1E212D),
                            contentColor = if (isAtDefault) SpotifyGreen else Color.White.copy(alpha = 0.9f),
                        ),
                        border = if (isAtDefault) {
                            androidx.compose.foundation.BorderStroke(1.dp, SpotifyGreen.copy(alpha = 0.6f))
                        } else {
                            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF323647))
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.set_as_default),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Buttons: Dismiss / Start Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(42.dp),
                    ) {
                        Text(
                            text = stringResource(android.R.string.cancel),
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            onDismissRequest()
                            sleepTimer?.start(
                                minute = sleepTimerValue.roundToInt(),
                                stopAfterCurrentSong = sleepTimerStopAfterCurrentSong,
                                fadeOut = sleepTimerFadeOut,
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpotifyGreen,
                            contentColor = Color.Black,
                        ),
                        modifier = Modifier.height(42.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.start_sleep_timer),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
