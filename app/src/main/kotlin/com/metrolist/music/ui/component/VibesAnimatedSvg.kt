/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.component

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.metrolist.music.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Loads an animated SVG asset with full CSS / JS animation support into a hardware-accelerated transparent WebView.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VibesAnimatedSvg(
    assetFileName: String,
    modifier: Modifier = Modifier,
    enableTapAnimation: Boolean = false,
    onTap: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val htmlContent = remember(assetFileName) {
        val svgRaw = try {
            context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
        val cleanSvg = svgRaw
            .replace(Regex("<\\?xml[^>]*\\?>"), "")
            .replace(Regex("<svg([^>]*?)\\bwidth=\"[^\"]*\""), "<svg$1 width=\"100%\"")
            .replace(Regex("<svg([^>]*?)\\bheight=\"[^\"]*\""), "<svg$1 height=\"100%\"")
            .trim()

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; -webkit-tap-highlight-color: transparent; }
                html, body {
                    width: 100%;
                    height: 100%;
                    background-color: transparent !important;
                    background: transparent !important;
                    overflow: hidden;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                svg {
                    width: 100% !important;
                    height: 100% !important;
                    max-width: 100% !important;
                    max-height: 100% !important;
                    display: block;
                }
            </style>
        </head>
        <body style="background: transparent;">
            $cleanSvg
        </body>
        </html>
        """.trimIndent()
    }

    var webViewRef: WebView? = remember { null }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.then(
            if (enableTapAnimation) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        webViewRef?.evaluateJavascript(
                            """
                            (function() {
                                var root = document.querySelector('svg.vibes-logo') || document.querySelector('svg');
                                if (root) {
                                    root.classList.remove('play');
                                    void root.getBoundingClientRect();
                                    root.classList.add('play');
                                    setTimeout(function() { root.classList.remove('play'); }, 1100);
                                }
                            })();
                            """.trimIndent(),
                            null,
                        )
                        onTap?.invoke()
                    },
                )
            } else {
                Modifier
            }
        ),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    setBackgroundColor(0) // transparent
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    overScrollMode = View.OVER_SCROLL_NEVER

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        useWideViewPort = false
                        loadWithOverviewMode = false
                    }

                    webViewClient = object : android.webkit.WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.setBackgroundColor(0)
                        }
                    }

                    if (enableTapAnimation) {
                        setOnTouchListener { v, event ->
                            if (event.action == MotionEvent.ACTION_UP) {
                                v.performClick()
                            }
                            false
                        }
                    }

                    loadDataWithBaseURL("https://appassets.androidplatform.net/", htmlContent, "text/html; charset=utf-8", "UTF-8", null)
                    webViewRef = this
                }
            },
            update = { webView ->
                webViewRef = webView
            },
        )
    }
}

/**
 * Top App Bar Logo for Vibes with interactive tap animation.
 * Aspect ratio of vibes_full_logo is 1732:693 (~ 2.5 : 1).
 * Height scaled 1.5x to 48.dp.
 */
@Composable
fun VibesHeaderLogo(
    height: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val width = height * 2.5f
    VibesAnimatedSvg(
        assetFileName = "vibes_full_logo_tap_animation.svg",
        enableTapAnimation = true,
        modifier = modifier
            .size(width = width, height = height),
    )
}

private const val RIGHT_EYE_PATH = "M638.57 255.4C646.48 254.73 654.57 255.19 662.5 255.19C676.5 255.19 690.5 255.12 704.5 255.18C718.27 255.24 732.88 254.06 745.37 261.1C772.71 276.5 771.96 301.71 771.99 329.5C772.02 374.5 771.98 419.5 771.96 464.5C771.96 493.17 771.99 521.83 771.98 550.5C771.98 561.43 772.77 572.64 770.77 583.44C767.25 602.47 751.96 618.83 733.21 623.61C718.02 627.48 699.21 625.42 683.5 625.43C660.86 625.44 629.58 629.56 610.55 615.98C587.07 599.21 589.26 573.14 589.27 547.5C589.28 497.17 589.35 446.83 589.27 396.5C589.26 385.83 589.28 375.17 589.28 364.5C589.28 352.17 589.27 339.83 589.28 327.5C589.28 317.92 588.59 308.03 590.24 298.55C593.2 281.59 605.48 266.44 621.27 259.74C626.71 257.43 632.67 255.9 638.57 255.4Z"
private const val LEFT_EYE_PATH = "M403.77 413.44C417.94 411.49 433.21 413 447.5 413.02C474.22 413.07 504.56 408.07 524.26 430.24C538.36 446.1 536.57 467.76 536.54 487.5C536.5 508.17 536.61 528.83 536.54 549.5C536.51 560.01 537.47 570.92 535.65 581.32C532.68 598.26 521.88 612.49 506.39 619.89C492.45 626.55 476.49 624.77 461.5 624.85C449.5 624.92 437.5 624.86 425.5 624.85C399.79 624.83 378.01 624.87 363.64 599.83C357.08 588.39 357.14 575.24 357.1 562.5C357.07 549.5 357.09 536.5 357.1 523.5C357.1 510.83 357.1 498.17 357.1 485.5C357.11 475.29 356.23 464.71 358.29 454.64C361.45 439.15 372.88 424.32 387.53 418.07C392.75 415.84 398.15 414.21 403.77 413.44Z"
private const val SMILE_PATH = "M862.71 570.45C901.28 567.89 927.94 604.93 915.48 640.97C910.7 654.79 900.32 666.61 891.8 678.28C867.8 711.15 838.24 739.84 805.9 764.43C726.59 824.73 622.21 851.46 523.5 846.25C435.8 841.63 353.08 817.25 283.97 761.53C251.94 735.7 224.62 706.32 200.42 673.1C190.25 659.13 178.28 644.45 177.09 626.5C174.35 585.02 221.23 561.83 256.43 576.12C272.2 582.52 283.42 596.67 294.65 608.85C306.72 621.94 320.66 633.89 334.72 644.8C382.08 681.55 437.63 704.01 496.87 712.53C585.46 725.26 673.95 705.03 748.43 655.97C769.5 642.09 788.85 624.97 806.55 607.06C823.39 590.02 836.63 572.18 862.71 570.45Z"

/**
 * 120 FPS hardware-accelerated native Compose Splash Logo for Vibes.
 * Eliminates WebView cold-start lag completely while providing vibrant green glow & rhythmic beat.
 */
@Composable
fun VibesNativeSplashLogo(
    logoSize: Dp = 190.dp,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VibesSplashTransition")

    // Rhythmic 120 BPM beat scaling
    val beatScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "beatScale",
    )

    // Pulsing radial glow alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    // Radiating soundwave ring 1
    val ringScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringScale1",
    )
    val ringAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringAlpha1",
    )

    // Radiating soundwave ring 2 (delayed phase)
    val ringScale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, delayMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringScale2",
    )
    val ringAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, delayMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringAlpha2",
    )

    // Equalizer left & right eye scale bouncing
    val leftEyeScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "leftEyeScale",
    )

    val rightEyeScale by infiniteTransition.animateFloat(
        initialValue = 1.15f,
        targetValue = 0.86f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "rightEyeScale",
    )

    val rightEyePath = remember {
        androidx.core.graphics.PathParser.createPathFromPathData(RIGHT_EYE_PATH).asComposePath()
    }
    val leftEyePath = remember {
        androidx.core.graphics.PathParser.createPathFromPathData(LEFT_EYE_PATH).asComposePath()
    }
    val smilePath = remember {
        androidx.core.graphics.PathParser.createPathFromPathData(SMILE_PATH).asComposePath()
    }

    val spotifyGreen = androidx.compose.ui.graphics.Color(0xFF01F672)
    val eyeColor = androidx.compose.ui.graphics.Color(0xFF0A0909)

    Box(
        modifier = modifier.size(logoSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerOffset = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val baseRadius = (canvasWidth.coerceAtMost(canvasHeight) * 0.38f)

            // 1. Soft radial green glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        spotifyGreen.copy(alpha = glowAlpha),
                        spotifyGreen.copy(alpha = glowAlpha * 0.35f),
                        androidx.compose.ui.graphics.Color.Transparent,
                    ),
                    center = centerOffset,
                    radius = baseRadius * 1.55f,
                ),
                radius = baseRadius * 1.55f,
                center = centerOffset,
            )

            // 2. Radiating soundwave rings
            drawCircle(
                color = spotifyGreen.copy(alpha = ringAlpha1),
                radius = baseRadius * ringScale1,
                center = centerOffset,
                style = Stroke(width = 3.dp.toPx()),
            )
            drawCircle(
                color = spotifyGreen.copy(alpha = ringAlpha2),
                radius = baseRadius * ringScale2,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx()),
            )

            // 3. Face & Features with 120 BPM rhythmic beat
            withTransform({
                scale(beatScale, beatScale, pivot = centerOffset)
            }) {
                // Main circular green face
                drawCircle(
                    color = spotifyGreen,
                    radius = baseRadius,
                    center = centerOffset,
                )

                // SVG coordinate space is 1095 x 1095. Compute scale and offset to fit in baseRadius * 2
                val svgSize = 1095f
                val targetDiam = baseRadius * 2f
                val scaleFactor = targetDiam / svgSize
                val offsetX = centerOffset.x - (svgSize * scaleFactor) / 2f
                val offsetY = centerOffset.y - (svgSize * scaleFactor) / 2f

                // Draw Right Eye (with equalizer scale)
                withTransform({
                    translate(left = offsetX, top = offsetY)
                    scale(scaleX = scaleFactor, scaleY = scaleFactor * rightEyeScale, pivot = Offset(680f, 600f))
                }) {
                    drawPath(path = rightEyePath, color = eyeColor)
                }

                // Draw Left Eye (with equalizer scale)
                withTransform({
                    translate(left = offsetX, top = offsetY)
                    scale(scaleX = scaleFactor, scaleY = scaleFactor * leftEyeScale, pivot = Offset(450f, 600f))
                }) {
                    drawPath(path = leftEyePath, color = eyeColor)
                }

                // Draw Smile Mouth
                withTransform({
                    translate(left = offsetX, top = offsetY)
                    scale(scaleX = scaleFactor, scaleY = scaleFactor, pivot = Offset(547.5f, 700f))
                }) {
                    drawPath(path = smilePath, color = eyeColor)
                }
            }
        }
    }
}

/**
 * Static Splash Screen Logo using zero-overhead native Canvas drawing.
 * Renders the clean, iconic Vibes smile logo instantaneously.
 */
@Composable
fun VibesStaticSplashLogo(
    logoSize: Dp = 190.dp,
    modifier: Modifier = Modifier,
) {
    val spotifyGreen = androidx.compose.ui.graphics.Color(0xFF01F672)
    val eyeColor = androidx.compose.ui.graphics.Color(0xFF0A0909)

    val rightEyePath = remember {
        androidx.core.graphics.PathParser.createPathFromPathData(RIGHT_EYE_PATH).asComposePath()
    }
    val leftEyePath = remember {
        androidx.core.graphics.PathParser.createPathFromPathData(LEFT_EYE_PATH).asComposePath()
    }
    val smilePath = remember {
        androidx.core.graphics.PathParser.createPathFromPathData(SMILE_PATH).asComposePath()
    }

    Box(
        modifier = modifier.size(logoSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerOffset = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val baseRadius = (canvasWidth.coerceAtMost(canvasHeight) * 0.44f)

            // Main circular green face
            drawCircle(
                color = spotifyGreen,
                radius = baseRadius,
                center = centerOffset,
            )

            // SVG coordinate space is 1095 x 1095
            val svgSize = 1095f
            val targetDiam = baseRadius * 2f
            val scaleFactor = targetDiam / svgSize
            val offsetX = centerOffset.x - (svgSize * scaleFactor) / 2f
            val offsetY = centerOffset.y - (svgSize * scaleFactor) / 2f

            withTransform({
                translate(left = offsetX, top = offsetY)
                scale(scaleX = scaleFactor, scaleY = scaleFactor, pivot = Offset(svgSize / 2f, svgSize / 2f))
            }) {
                drawPath(path = rightEyePath, color = eyeColor)
                drawPath(path = leftEyePath, color = eyeColor)
                drawPath(path = smilePath, color = eyeColor)
            }
        }
    }
}

/**
 * Splash Screen Logo using vibes_circular_transparent.
 */
@Composable
fun VibesSplashLogo(
    size: Dp = 190.dp,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.vibes_circular_transparent),
        contentDescription = "Vibes",
        modifier = modifier.size(size),
    )
}

