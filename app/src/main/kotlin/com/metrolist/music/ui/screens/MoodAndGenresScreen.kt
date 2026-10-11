/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.metrolist.music.LocalPlayerAwareWindowInsets
import com.metrolist.music.R
import com.metrolist.music.ui.component.IconButton
import com.metrolist.music.ui.component.NavigationTitle
import com.metrolist.music.ui.component.shimmer.ListItemPlaceHolder
import com.metrolist.music.ui.component.shimmer.ShimmerHost
import com.metrolist.music.ui.theme.spotifyPressFeedback
import com.metrolist.music.ui.utils.backToMain
import com.metrolist.music.viewmodels.MoodAndGenresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAndGenresScreen(
    navController: NavController,
    viewModel: MoodAndGenresViewModel = hiltViewModel(),
) {
    val localConfiguration = LocalConfiguration.current
    val itemsPerRow = if (localConfiguration.orientation == ORIENTATION_LANDSCAPE) 3 else 2

    val moodAndGenresList by viewModel.moodAndGenres.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
    ) {
        if (moodAndGenresList == null) {
            item(key = "mood_and_genres_shimmer") {
                ShimmerHost(
                    modifier = Modifier.animateItem()
                ) {
                    repeat(8) {
                        ListItemPlaceHolder()
                    }
                }
            }
        }

        moodAndGenresList?.forEachIndexed { index, moodAndGenres ->
            item(key = "mood_and_genres_section_$index") {
                Column(
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 6.dp),
                ) {
                    NavigationTitle(
                        title = moodAndGenres.title,
                    )
                    moodAndGenres.items.chunked(itemsPerRow).forEach { row ->
                        Row {
                            row.forEach {
                                MoodAndGenresButton(
                                    title = it.title,
                                    onClick = {
                                        navController.navigate("youtube_browse/${it.endpoint.browseId}?params=${it.endpoint.params}")
                                    },
                                    modifier =
                                    Modifier
                                        .weight(1f)
                                        .padding(6.dp),
                                )
                            }

                            repeat(itemsPerRow - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.mood_and_genres)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
fun MoodAndGenresButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lower = title.lowercase()
    val (gradientColors, iconRes) = when {
        lower.contains("chill") || lower.contains("relax") || lower.contains("calm") || lower.contains("peace") ->
            listOf(Color(0xFF27856A), Color(0xFF1E3264)) to R.drawable.tune
        lower.contains("focus") || lower.contains("study") || lower.contains("work") || lower.contains("deep") ->
            listOf(Color(0xFF503750), Color(0xFF1E3264)) to R.drawable.tune
        lower.contains("workout") || lower.contains("gym") || lower.contains("fitness") || lower.contains("energy") ->
            listOf(Color(0xFFBA5D07), Color(0xFFE8115B)) to R.drawable.local_fire_department
        lower.contains("party") || lower.contains("dance") || lower.contains("club") || lower.contains("edm") ->
            listOf(Color(0xFF8D67AB), Color(0xFFE91429)) to R.drawable.local_fire_department
        lower.contains("sleep") || lower.contains("bedtime") || lower.contains("night") || lower.contains("ambient") ->
            listOf(Color(0xFF14387F), Color(0xFF0C1A38)) to R.drawable.bedtime
        lower.contains("romance") || lower.contains("love") || lower.contains("date") ->
            listOf(Color(0xFFB02897), Color(0xFFE8115B)) to R.drawable.favorite
        lower.contains("rock") || lower.contains("metal") || lower.contains("punk") ->
            listOf(Color(0xFFE91429), Color(0xFF5A0000)) to R.drawable.music_note
        lower.contains("hip-hop") || lower.contains("rap") || lower.contains("trap") || lower.contains("r&b") ->
            listOf(Color(0xFFBC5900), Color(0xFF5C2D00)) to R.drawable.music_note
        lower.contains("pop") || lower.contains("hits") || lower.contains("top") || lower.contains("charts") ->
            listOf(Color(0xFF006450), Color(0xFF01F672)) to R.drawable.radio
        lower.contains("jazz") || lower.contains("blues") ->
            listOf(Color(0xFF1D7561), Color(0xFF0D4037)) to R.drawable.music_note
        lower.contains("classical") || lower.contains("piano") || lower.contains("acoustic") ->
            listOf(Color(0xFF8C67AB), Color(0xFF49306B)) to R.drawable.album
        lower.contains("feel good") || lower.contains("happy") || lower.contains("joy") ->
            listOf(Color(0xFFE8115B), Color(0xFFFFA42B)) to R.drawable.local_fire_department
        else -> {
            val palettePool = listOf(
                listOf(Color(0xFF006450), Color(0xFF1E3264)),
                listOf(Color(0xFF8400E7), Color(0xFF1E3264)),
                listOf(Color(0xFFE8115B), Color(0xFFBA5D07)),
                listOf(Color(0xFF1E3264), Color(0xFF431B69)),
                listOf(Color(0xFF27856A), Color(0xFF0C1A38)),
                listOf(Color(0xFFBC5900), Color(0xFFE91429)),
                listOf(Color(0xFF503750), Color(0xFF8D67AB)),
                listOf(Color(0xFF006450), Color(0xFF01F672)),
            )
            val index = (kotlin.math.abs(title.hashCode()) % palettePool.size)
            palettePool[index] to R.drawable.music_note
        }
    }

    Box(
        modifier = modifier
            .height(MoodAndGenresButtonHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(gradientColors))
            .spotifyPressFeedback(onClick = onClick),
    ) {
        // Decorative corner watermark icon rotated like Spotify genre tiles
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.28f),
            modifier = Modifier
                .size(46.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = 10.dp)
                .rotate(-15f),
        )

        // Bold Title on top-left
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 12.dp, start = 12.dp, end = 28.dp),
        )
    }
}

val MoodAndGenresButtonHeight = 84.dp
