package com.metrolist.music.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.metrolist.innertube.YouTube
import com.metrolist.music.R
import com.metrolist.music.constants.AccountNameKey
import com.metrolist.music.ui.theme.SpotifyDarkBase
import com.metrolist.music.ui.theme.SpotifyGreen
import com.metrolist.music.ui.theme.spotifyPressFeedback
import com.metrolist.music.utils.rememberPreference

@Composable
fun SpotifyImportScreen(
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current
    val (accountName) = rememberPreference(AccountNameKey, "")
    val isLoggedIn = accountName.isNotEmpty() && YouTube.cookie != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1015)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.spotify_import_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp))

            // Hero Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E222D),
                                Color(0xFF13151D),
                            )
                        )
                    )
                    .padding(24.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SpotifyGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SpotifyGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.size(68.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_spotify),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Bring Your Spotify Library",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp,
                        ),
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.spotify_import_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF8A90A0),
                            lineHeight = 20.sp,
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Step 1: Log in with YouTube Music
            ImportStepCard(
                stepNumber = "1",
                title = stringResource(R.string.spotify_import_step1_title),
                description = stringResource(R.string.spotify_import_step1_desc),
                isCompleted = isLoggedIn,
                actionContent = {
                    if (isLoggedIn) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SpotifyGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                                tint = SpotifyGreen,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Connected as $accountName",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = SpotifyGreen,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                    } else {
                        Button(
                            onClick = { navController.navigate("login") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpotifyGreen,
                                contentColor = Color.Black,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.spotifyPressFeedback(),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_youtube_music),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.login_with_youtube_music),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            // Step 2: TuneMyMusic Transfer
            ImportStepCard(
                stepNumber = "2",
                title = stringResource(R.string.spotify_import_step2_title),
                description = stringResource(R.string.spotify_import_step2_desc),
                isCompleted = false,
                actionContent = {
                    Button(
                        onClick = {
                            uriHandler.openUri("https://www.tunemymusic.com/transfer/spotify-to-youtube-music")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1DB954),
                            contentColor = Color.Black,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.spotifyPressFeedback(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.link),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.spotify_import_step2_btn),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            // Step 3: Return & Enjoy
            ImportStepCard(
                stepNumber = "3",
                title = stringResource(R.string.spotify_import_step3_title),
                description = stringResource(R.string.spotify_import_step3_desc),
                isCompleted = false,
                actionContent = {
                    OutlinedButton(
                        onClick = { navController.navigateUp() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                        ),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.spotifyPressFeedback(),
                    ) {
                        Text(
                            text = "Back to Vibes",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ImportStepCard(
    stepNumber: String,
    title: String,
    description: String,
    isCompleted: Boolean,
    actionContent: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF181B24),
        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isCompleted) SpotifyGreen else Color(0xFF262B3B),
                    modifier = Modifier.size(30.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp),
                            )
                        } else {
                            Text(
                                text = stepNumber,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF8A90A0),
                    lineHeight = 18.sp,
                ),
            )

            Spacer(Modifier.height(14.dp))

            actionContent()
        }
    }
}
