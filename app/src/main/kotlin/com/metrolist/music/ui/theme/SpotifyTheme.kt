/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Spotify Design Language & Apple Fluid Physics Foundations
 */

package com.metrolist.music.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// ============================================================================
// SPOTIFY COLOR SYSTEM
// ============================================================================
val SpotifyDarkBase = Color(0xFF0F1117)
val SpotifyDarkSurface = Color(0xFF151821)
val SpotifyElevated = Color(0xFF1D202C)
val SpotifyHighlight = Color(0xFF262B3B)
val SpotifyGreen = Color(0xFF1DB954)
val SpotifyBrightGreen = Color(0xFF1ED760)
val SpotifyTextPrimary = Color(0xFFFFFFFF)
val SpotifyTextSecondary = Color(0xFF8A90A0)
val SpotifyTextSubtle = Color(0xFF5A6070)
val SpotifyBorder = Color(0x1AFFFFFF)
val SpotifyGlassBackground = Color(0xF20F1117)

/**
 * Spotify dark color scheme configured according to Spotify design system
 */
fun spotifyDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = Color.Black,
    primaryContainer = SpotifyElevated,
    onPrimaryContainer = SpotifyGreen,
    inversePrimary = SpotifyBrightGreen,
    secondary = SpotifyBrightGreen,
    onSecondary = Color.Black,
    secondaryContainer = SpotifyHighlight,
    onSecondaryContainer = SpotifyTextPrimary,
    tertiary = SpotifyGreen,
    onTertiary = Color.Black,
    background = SpotifyDarkBase,
    onBackground = SpotifyTextPrimary,
    surface = SpotifyDarkBase,
    onSurface = SpotifyTextPrimary,
    surfaceVariant = SpotifyDarkSurface,
    onSurfaceVariant = SpotifyTextSecondary,
    surfaceTint = SpotifyGreen,
    inverseSurface = SpotifyTextPrimary,
    inverseOnSurface = SpotifyDarkBase,
    outline = Color(0xFF9EA6BA),
    outlineVariant = Color(0xFF50566A),
    scrim = Color.Black,
    surfaceContainerLowest = Color(0xFF0C0C0C),
    surfaceContainerLow = SpotifyDarkBase,
    surfaceContainer = SpotifyDarkSurface,
    surfaceContainerHigh = SpotifyElevated,
    surfaceContainerHighest = Color(0xFF1E212D),
)

// ============================================================================
// APPLE FLUID MOTION & TACTILE PRESS SYSTEM
// ============================================================================

/**
 * Tactile spring press feedback inspired by Apple's Designing Fluid Interfaces.
 * Intercepts touch-down immediately (zero latency) with scale compression and haptics,
 * returning smoothly with an interruptible spring on release.
 */
fun Modifier.spotifyPressFeedback(
    targetScale: Float = 0.96f,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    this
        .graphicsLayer {
            scaleX = scaleAnim.value
            scaleY = scaleAnim.value
        }
        .pointerInput(onClick) {
            detectTapGestures(
                onPress = {
                    coroutineScope.launch {
                        scaleAnim.animateTo(
                            targetValue = targetScale,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                        )
                    }
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    val released = tryAwaitRelease()
                    coroutineScope.launch {
                        scaleAnim.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        )
                    }
                    if (released && onClick != null) {
                        onClick()
                    }
                },
            )
        }
}

/**
 * Subtle top glass border highlight for frosted acrylic bars
 */
fun Modifier.spotifyGlassBorder(
    strokeWidth: Dp = 1.dp,
    color: Color = SpotifyBorder,
): Modifier = drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = strokeWidthPx,
    )
}

/**
 * Spotify rounded container modifier with elevated surface color and optional subtle border
 */
fun Modifier.spotifyCard(
    cornerRadius: Dp = 8.dp,
    backgroundColor: Color = SpotifyDarkSurface,
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .drawBehind {
        drawRect(backgroundColor)
    }
