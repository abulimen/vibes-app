/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Spotify Home Feed Header: Dynamic Greeting & Quick-Access 6-Grid Tiles
 */

package com.metrolist.music.ui.screens

import java.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.metrolist.music.R
import com.metrolist.music.db.entities.Song
import com.metrolist.music.extensions.toMediaItem
import com.metrolist.music.playback.PlayerConnection
import com.metrolist.music.playback.queues.ListQueue
import com.metrolist.music.ui.theme.SpotifyElevated
import com.metrolist.music.ui.theme.SpotifyGreen
import com.metrolist.music.ui.theme.SpotifyTextPrimary
import com.metrolist.music.ui.theme.spotifyPressFeedback

@Composable
fun SpotifyHomeHeader(
    quickPicks: List<Song>?,
    playerConnection: PlayerConnection?,
    modifier: Modifier = Modifier,
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        // Spotify Bold Header Greeting
        Text(
            text = greeting,
            color = SpotifyTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.02).sp,
            modifier = Modifier.padding(bottom = 12.dp, top = 4.dp),
        )

        // 6-Grid Quick Access Tiles (2 columns x 3 rows)
        val items = remember(quickPicks) {
            quickPicks?.distinctBy { it.id }?.take(6) ?: emptyList()
        }

        if (items.isNotEmpty()) {
            val rows = remember(items) { items.chunked(2) }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            ) {
                rows.forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        rowItems.forEach { song ->
                            SpotifyShortcutTile(
                                song = song,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    playerConnection?.playQueue(
                                        ListQueue(
                                            title = song.title,
                                            items = listOf(song.toMediaItem()),
                                        ),
                                    )
                                },
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpotifyShortcutTile(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tileShape = RoundedCornerShape(4.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .clip(tileShape)
            .background(SpotifyElevated)
            .border(0.5.dp, Color(0x1AFFFFFF), tileShape)
            .spotifyPressFeedback(onClick = onClick),
    ) {
        // Artwork
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)),
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Title
        Text(
            text = song.title,
            color = SpotifyTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
        )

        // Small Spotify Green Play Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(SpotifyGreen),
        ) {
            Icon(
                painter = painterResource(R.drawable.play),
                contentDescription = "Play",
                tint = Color.Black,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
