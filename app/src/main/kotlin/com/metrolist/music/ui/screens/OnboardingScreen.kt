package com.metrolist.music.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.metrolist.music.R
import com.metrolist.music.constants.OnboardingCompletedKey
import com.metrolist.music.ui.theme.SpotifyGreen
import com.metrolist.music.ui.theme.spotifyPressFeedback
import com.metrolist.music.utils.rememberPreference

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import com.metrolist.music.utils.safeDataStoreEdit
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    navController: NavController,
    onComplete: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val (_, setOnboardingCompleted) = rememberPreference(OnboardingCompletedKey, defaultValue = false)
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val deepBlack = Color(0xFF07080B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepBlack),
    ) {
        // Top Artwork with smooth gradient fade into the obsidian background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.56f)
                .align(Alignment.TopCenter),
        ) {
            Image(
                painter = painterResource(R.drawable.onboarding),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Multi-stop seamless vertical vignette fade
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.35f to Color.Transparent,
                            0.65f to deepBlack.copy(alpha = 0.55f),
                            0.82f to deepBlack.copy(alpha = 0.88f),
                            1.0f to deepBlack,
                        )
                    ),
            )
        }

        // Bottom Content & Actions
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600), initialOffsetY = { it / 4 }),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
            ) {
                // Free & Ad-Free Badge
                Surface(
                    shape = CircleShape,
                    color = SpotifyGreen.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, SpotifyGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.padding(bottom = 14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "✦  100% FREE  •  ZERO ADS FOREVER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SpotifyGreen,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp,
                            ),
                        )
                    }
                }

                // Main Headline
                Text(
                    text = "Music without limits.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center,
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "Stream over 100 million songs and high-definition music videos without paying a dime or sitting through a single ad.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF9EA6BA),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                Spacer(modifier = Modifier.height(22.dp))

                // 3 Value Pillars
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                ) {
                    FeaturePill(icon = R.drawable.play, text = "Ad-free")
                    FeaturePill(icon = R.drawable.music_note, text = "100M+ Songs")
                    FeaturePill(icon = R.drawable.local_fire_department, text = "Full Videos")
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary CTA Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            context.safeDataStoreEdit {
                                it[OnboardingCompletedKey] = true
                            }
                        }
                        setOnboardingCompleted(true)
                        if (onComplete != null) {
                            onComplete()
                        } else {
                            navController.navigateUp()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpotifyGreen,
                        contentColor = Color.Black,
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .spotifyPressFeedback(),
                ) {
                    Text(
                        text = "Start Listening",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 17.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: Int,
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF141720),
        border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = SpotifyGreen,
                modifier = Modifier.size(13.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                ),
            )
        }
    }
}
