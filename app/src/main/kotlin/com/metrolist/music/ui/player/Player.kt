/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.player

import androidx.activity.compose.BackHandler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.metrolist.music.ui.theme.SpotifyGreen
import com.metrolist.music.ui.theme.SpotifyTextPrimary
import com.metrolist.music.ui.theme.SpotifyTextSecondary
import com.metrolist.music.ui.theme.spotifyPressFeedback
import com.metrolist.music.ui.component.SoundwaveScrubber
import com.metrolist.music.ui.component.ClickableArtistText
import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import coil3.compose.AsyncImage
import com.metrolist.innertube.YouTube
import com.metrolist.innertube.models.CommentItem
import com.metrolist.innertube.models.YouTubeSongEngagement
import com.metrolist.innertube.utils.parseCookieString
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.metrolist.music.LocalNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.metrolist.music.LocalDatabase
import com.metrolist.music.LocalDownloadUtil
import com.metrolist.music.LocalListenTogetherManager
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.constants.CropAlbumArtKey
import com.metrolist.music.constants.DarkModeKey
import com.metrolist.music.constants.HidePlayerThumbnailKey
import com.metrolist.music.constants.HideStatusBarOnFullscreenKey
import com.metrolist.music.constants.KeepScreenOn
import com.metrolist.music.constants.PlayerBackgroundStyle
import com.metrolist.music.constants.PlayerBackgroundStyleKey
import com.metrolist.music.constants.PlayerButtonsStyle
import com.metrolist.music.constants.PlayerButtonsStyleKey
import com.metrolist.music.constants.PlayerHorizontalPadding
import com.metrolist.music.constants.QueuePeekHeight
import com.metrolist.music.constants.SleepTimerDefaultKey
import com.metrolist.music.constants.SleepTimerFadeOutKey
import com.metrolist.music.constants.SleepTimerStopAfterCurrentSongKey
import com.metrolist.music.constants.SliderStyle
import com.metrolist.music.constants.SliderStyleKey
import com.metrolist.music.constants.SquigglySliderKey
import com.metrolist.music.constants.ThumbnailCornerRadius
import com.metrolist.music.constants.UseNewPlayerDesignKey
import com.metrolist.music.db.entities.LyricsEntity
import com.metrolist.music.extensions.metadata
import com.metrolist.music.extensions.togglePlayPause
import com.metrolist.music.extensions.toggleRepeatMode
import com.metrolist.music.listentogether.RoomRole
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.ui.component.BottomSheet
import com.metrolist.music.ui.component.BottomSheetState
import com.metrolist.music.ui.component.LocalBottomSheetPageState
import com.metrolist.music.ui.component.LocalMenuState
import com.metrolist.music.ui.component.Lyrics
import com.metrolist.music.ui.component.ModernSleepTimerDialog
import com.metrolist.music.ui.component.PlayerSliderTrack
import com.metrolist.music.ui.component.ResizableIconButton
import com.metrolist.music.ui.component.SquigglySlider
import com.metrolist.music.ui.component.WavySlider
import com.metrolist.music.ui.component.rememberBottomSheetState
import com.metrolist.music.ui.menu.PlayerMenu
import com.metrolist.music.ui.screens.settings.DarkMode
import com.metrolist.music.ui.theme.PlayerColorExtractor
import com.metrolist.music.ui.theme.PlayerSliderColors
import com.metrolist.music.ui.utils.ShowMediaInfo
import com.metrolist.music.ui.utils.ShowOffsetDialog
import com.metrolist.music.utils.dataStore
import com.metrolist.music.utils.makeTimeString
import com.metrolist.music.utils.rememberEnumPreference
import com.metrolist.music.utils.rememberPreference
import com.metrolist.music.utils.safeDataStoreEdit
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt
import com.metrolist.music.ui.component.Icon as MIcon
import com.metrolist.music.constants.SleepTimerDefaultKey
import com.metrolist.music.constants.SleepTimerFadeOutKey
import com.metrolist.music.constants.SleepTimerStopAfterCurrentSongKey


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    pureBlack: Boolean,
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val menuState = LocalMenuState.current
    val sleepTimerDefaultSetTemplate = stringResource(R.string.sleep_timer_default_set)
    val copiedTitleStr = stringResource(R.string.copied_title)
    val copiedArtistStr = stringResource(R.string.copied_artist)
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) =
        rememberPreference(
            UseNewPlayerDesignKey,
            defaultValue = true,
        )
    val (hidePlayerThumbnail, onHidePlayerThumbnailChange) = rememberPreference(HidePlayerThumbnailKey, false)
    val (hideStatusBarOnFullscreen) = rememberPreference(HideStatusBarOnFullscreenKey, false)
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)

    var showInlineLyrics by rememberSaveable {
        mutableStateOf(false)
    }

    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var isFullscreenVideo by rememberSaveable {
        mutableStateOf(false)
    }

    var songEngagement by remember { mutableStateOf<YouTubeSongEngagement?>(null) }
    var showCommentsSheet by rememberSaveable { mutableStateOf(false) }
    var commentsList by remember { mutableStateOf<List<CommentItem>?>(null) }
    var isLoadingComments by remember { mutableStateOf(false) }
    val isVideoModeState = com.metrolist.music.LocalIsVideoMode.current
    var isVideoMode by isVideoModeState

    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT,
    )
    val playerButtonsStyle by rememberEnumPreference(
        key = PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT,
    )

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val useDarkTheme =
        remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }

    val shouldUseDarkButtonColors =
        remember(playerBackground, useDarkTheme) {
            when (playerBackground) {
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> true
                PlayerBackgroundStyle.DEFAULT -> useDarkTheme
            }
        }

    val isPlaying by playerConnection.isPlaying.collectAsState()
    val isKeepScreenOn by rememberPreference(KeepScreenOn, false)
    val keepScreenOn = isPlaying && isKeepScreenOn

    DisposableEffect(playerBackground, state.isExpanded, useDarkTheme, keepScreenOn, isFullScreen, hideStatusBarOnFullscreen) {
        val window = (context as? android.app.Activity)?.window
        if (window != null && state.isExpanded) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)

            when (playerBackground) {
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                    insetsController.isAppearanceLightStatusBars = false
                }

                PlayerBackgroundStyle.DEFAULT -> {
                    insetsController.isAppearanceLightStatusBars = !useDarkTheme
                }
            }

            if (isFullScreen && hideStatusBarOnFullscreen) {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            }

            if (keepScreenOn && state.isExpanded) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        onDispose {
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !useDarkTheme
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    BackHandler(enabled = state.isExpanded) {
        state.collapseSoft()
    }

    val onBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.onSurface
        }
    val useBlackBackground =
        remember(isSystemInDarkTheme, darkTheme, pureBlack) {
            val useDarkTheme =
                if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
            useDarkTheme && pureBlack
        }

    val playbackState by playerConnection.playbackState.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(mediaMetadata?.id, mediaMetadata?.title) {
        val title = mediaMetadata?.title.orEmpty().lowercase()
        val isVideoTitle = title.contains("video") || title.contains("lyrics") || title.contains("live")
        isVideoMode = isVideoTitle
    }

    LaunchedEffect(mediaMetadata?.id) {
        songEngagement = null
        commentsList = null
        val songId = mediaMetadata?.id ?: return@LaunchedEffect
        songEngagement = YouTube.songEngagement(songId).getOrNull()
    }

    LaunchedEffect(showCommentsSheet) {
        if (showCommentsSheet && commentsList == null) {
            val token = songEngagement?.commentsToken
            if (token != null) {
                isLoadingComments = true
                commentsList = YouTube.songComments(token).getOrNull()
                isLoadingComments = false
            }
        }
    }
    val automix by playerConnection.service.automixItems.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()

    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.DEFAULT)
    val squigglySlider by rememberPreference(SquigglySliderKey, defaultValue = false)

    // Listen Together state (reactive)
    val listenTogetherManager = LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsStateWithLifecycle(initialValue = RoomRole.NONE)
    val isListenTogetherGuest = listenTogetherRoleState?.value == RoomRole.GUEST

    // Cast state - safely access castConnectionHandler to prevent crashes during service lifecycle changes
    val castHandler =
        remember(playerConnection) {
            try {
                playerConnection.service.castConnectionHandler
            } catch (e: Exception) {
                null
            }
        }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castPosition by castHandler?.castPosition?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castDuration by castHandler?.castDuration?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsState() ?: remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isExpanded) {
        if (state.isExpanded) {
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Ignore if focus request fails
            }
        }
    }

    // Use Cast state when casting, otherwise local player
    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying

    // Use State objects for position/duration to pass to MiniPlayer without causing recomposition
    // These states persist across playback state changes to ensure continuous progress updates.
    // Seed from the player's current values so re-entering composition on resume shows the real
    // time immediately instead of flashing 0:00 until the first poll fires. runCatching guards the
    // player-not-ready race; the poll loop corrects duration if it isn't known yet.
    val positionState = remember { mutableLongStateOf(runCatching { playerConnection.player.currentPosition }.getOrDefault(0L)) }
    val durationState = remember {
        mutableLongStateOf(
            (mediaMetadata?.duration?.takeIf { it > 0 }?.toLong()?.times(1000L))
                ?: runCatching { playerConnection.player.duration }.getOrDefault(0L).coerceAtLeast(0L),
        )
    }

    // Convenience accessors for local use
    var position by positionState
    var duration by durationState

    val effectivePosition by remember {
        derivedStateOf {
            if (isCasting) {
                castPosition
            } else {
                position
            }
        }
    }

    var sliderPosition by remember {
        mutableStateOf<Long?>(null)
    }
    // Track when we last manually set position to avoid Cast overwriting it
    var lastManualSeekTime by remember { mutableLongStateOf(0L) }

    var gradientColors by remember {
        mutableStateOf<List<Color>>(emptyList())
    }
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }

    if (!canSkipNext && automix.isNotEmpty()) {
        playerConnection.service.addToQueueAutomix(automix[0], 0)
    }

    val defaultGradientColors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    val fallbackColor = MaterialTheme.colorScheme.surface.toArgb()

    LaunchedEffect(mediaMetadata?.id, playerBackground) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT) {
            val currentMetadata = mediaMetadata
            if (currentMetadata != null && currentMetadata.thumbnailUrl != null) {
                val cachedColors = gradientColorsCache[currentMetadata.id]
                if (cachedColors != null) {
                    gradientColors = cachedColors
                    return@LaunchedEffect
                }
                withContext(Dispatchers.IO) {
                    val request =
                        ImageRequest
                            .Builder(context)
                            .data(currentMetadata.thumbnailUrl)
                            .size(100, 100)
                            .allowHardware(false)
                            .memoryCacheKey("gradient_${currentMetadata.id}")
                            .build()

                    val result = runCatching { context.imageLoader.execute(request) }.getOrNull()
                    if (result != null) {
                        val bitmap = result.image?.toBitmap()
                        if (bitmap != null) {
                            val palette =
                                withContext(Dispatchers.Default) {
                                    Palette
                                        .from(bitmap)
                                        .maximumColorCount(8)
                                        .resizeBitmapArea(100 * 100)
                                        .generate()
                                }
                            val extractedColors =
                                PlayerColorExtractor.extractGradientColors(
                                    palette = palette,
                                    fallbackColor = fallbackColor,
                                )
                            gradientColorsCache[currentMetadata.id] = extractedColors
                            withContext(Dispatchers.Main) { gradientColors = extractedColors }
                        }
                    }
                }
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val TextBackgroundColor by animateColorAsState(
        targetValue =
            when (playerBackground) {
                PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
                PlayerBackgroundStyle.BLUR -> Color.White
                PlayerBackgroundStyle.GRADIENT -> Color.White
            },
        label = "TextBackgroundColor",
    )

    val icBackgroundColor by animateColorAsState(
        targetValue =
            when (playerBackground) {
                PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.surface
                PlayerBackgroundStyle.BLUR -> Color.Black
                PlayerBackgroundStyle.GRADIENT -> Color.Black
            },
        label = "icBackgroundColor",
    )

    val (textButtonColor, iconButtonColor) =
        when {
            playerBackground == PlayerBackgroundStyle.BLUR ||
                playerBackground == PlayerBackgroundStyle.GRADIENT -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(Color.White, Color.Black)
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
            }

            else -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        if (useDarkTheme) {
                            Pair(Color.White, Color.Black)
                        } else {
                            Pair(Color.Black, Color.White)
                        }
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
            }
        }

    // Separate colors for Previous/Next buttons in PRIMARY/TERTIARY modes
    val (sideButtonContainerColor, sideButtonContentColor) =
        when {
            playerBackground == PlayerBackgroundStyle.BLUR ||
                playerBackground == PlayerBackgroundStyle.GRADIENT -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(
                            Color.White.copy(alpha = 0.2f),
                            Color.White,
                        )
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }

            else -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        }

    val download by LocalDownloadUtil.current
        .getDownload(mediaMetadata?.id ?: "")
        .collectAsStateWithLifecycle(initialValue = null)

    val sleepTimerEnabled =
        remember(
            playerConnection.service.sleepTimer?.triggerTime,
            playerConnection.service.sleepTimer?.pauseWhenSongEnd,
        ) {
            playerConnection.service.sleepTimer?.isActive ?: false
        }

    var sleepTimerTimeLeft by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                sleepTimerTimeLeft =
                    if (playerConnection.service.sleepTimer?.pauseWhenSongEnd == true) {
                        playerConnection.player.duration - playerConnection.player.currentPosition
                    } else {
                        (playerConnection.service.sleepTimer?.triggerTime ?: 0L) - System.currentTimeMillis()
                    }
                delay(1000L)
            }
        }
    }

    val scope = rememberCoroutineScope()
    var showSleepTimerDialog by remember {
        mutableStateOf(false)
    }

    val sleepTimerDefault by rememberPreference(SleepTimerDefaultKey, 30f)
    var sleepTimerValue by remember { mutableFloatStateOf(sleepTimerDefault) }
    val isAtDefault by remember {
        derivedStateOf { sleepTimerValue.roundToInt() == sleepTimerDefault.roundToInt() }
    }
    LaunchedEffect(sleepTimerDefault) { sleepTimerValue = sleepTimerDefault }
    val sleepTimerStopAfterCurrentSong by rememberPreference(SleepTimerStopAfterCurrentSongKey, false)
    val sleepTimerFadeOut by rememberPreference(SleepTimerFadeOutKey, false)


    if (showSleepTimerDialog) {
        ModernSleepTimerDialog(
            onDismissRequest = { showSleepTimerDialog = false },
        )
    }

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    // Position update - only for local playback
    // When casting, we use castPosition directly to avoid sync issues
    // Use isPlaying instead of playbackState to ensure continuous updates during playback
    LaunchedEffect(isPlaying, isCasting) {
        if (!isCasting && isPlaying) {
            while (isActive) {
                delay(100) // Update more frequently for smoother progress bar
                if (sliderPosition == null) { // Only update if user isn't dragging
                    position = playerConnection.player.currentPosition
                    // Don't clobber a valid (metadata-derived) duration with 0/UNSET mid-resolve.
                    playerConnection.player.duration.takeIf { it > 0 }?.let { duration = it }
                }
            }
        }
    }

    // Also update position when playback state changes (e.g., song change, seek)
    LaunchedEffect(playbackState, mediaMetadata?.id) {
        if (!isCasting) {
            position = playerConnection.player.currentPosition
            // Prefer the song's known duration (from metadata, available instantly from the restored
            // queue) so the slider range is right even when restored paused / before the stream
            // resolves; fall back to the player's duration once it is known.
            duration = (mediaMetadata?.duration?.takeIf { it > 0 }?.toLong()?.times(1000L))
                ?: playerConnection.player.duration
        }
    }

    // Auto-switch from repeat one to repeat all when song ends naturally
    var previousMediaId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playbackState, mediaMetadata?.id) {
        val currentId = mediaMetadata?.id

        // Only switch from REPEAT_ONE to REPEAT_ALL when playback naturally ended
        // (i.e., the player transitioned to ENDED state and then moved to next track).
        // Do NOT switch on manual skips.
        if (currentId != null &&
            currentId != previousMediaId &&
            previousMediaId != null &&
            playbackState == Player.STATE_ENDED &&
            repeatMode == Player.REPEAT_MODE_ONE &&
            !isListenTogetherGuest) {
            playerConnection.player.setRepeatMode(Player.REPEAT_MODE_ALL)
        }

        previousMediaId = currentId
    }

    // When casting, use Cast position/duration directly
    // But wait a bit after manual seeks to let Cast catch up
    LaunchedEffect(isCasting, castPosition, castDuration) {
        if (isCasting && sliderPosition == null) {
            val timeSinceManualSeek = System.currentTimeMillis() - lastManualSeekTime
            if (timeSinceManualSeek > 1500) {
                // Only update from Cast if we haven't manually seeked recently
                position = castPosition
                if (castDuration > 0) duration = castDuration
            }
        }
    }

    val queueSheetState =
        rememberBottomSheetState(
            dismissedBound = 0.dp,
            expandedBound = state.expandedBound,
            collapsedBound = 0.dp,
            initialAnchor = 0,
        )

    val bottomSheetBackgroundColor =
        if (useBlackBackground) {
            Color.Black
        } else {
            Color(0xFF0E1015)
        }

    BottomSheet(
        state = state,
        modifier = modifier,
        background = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(bottomSheetBackgroundColor),
            )
        },
        onDismiss =
            if (!isListenTogetherGuest) {
                {
                    playerConnection.service.clearAutomix()
                    playerConnection.player.stop()
                    playerConnection.player.clearMediaItems()
                }
            } else {
                null
            },
        collapsedContent = {
            MiniPlayer(
                positionState = positionState,
                durationState = durationState,
                isExpanded = !state.isCollapsed,
                onClick = { state.expandSoft() },
            )
        },
    ) {
        val controlsContent: @Composable ColumnScope.(MediaMetadata) -> Unit = { mediaMetadata ->
            // 1. Song Details Row: Dedicated Full-Width Title and Artist
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
            ) {
                Text(
                    text = mediaMetadata.title,
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .basicMarquee(iterations = Int.MAX_VALUE, velocity = 30.dp)
                            .clickable {
                                val albumId =
                                    mediaMetadata.album?.id
                                        ?: currentSong?.album?.id
                                        ?: currentSong?.song?.albumId
                                if (albumId != null) {
                                    navController.navigate("album/$albumId")
                                    state.collapseSoft()
                                }
                            },
                )

                Spacer(modifier = Modifier.height(4.dp))

                ClickableArtistText(
                    artists = mediaMetadata.artists,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8A90A0),
                            textAlign = TextAlign.Center,
                        ),
                    color = Color(0xFF8A90A0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .basicMarquee(iterations = Int.MAX_VALUE, velocity = 25.dp),
                    onArtistClick = {
                        state.collapseSoft()
                    },
                )
            }

            Spacer(Modifier.height(10.dp))

            // 2. Action Buttons Row: Favorite & Likes on left, More options on right
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
            ) {
                // Left: Favorite Heart Button + YouTube Like Count
                val isEpisode = currentSong?.song?.isEpisode == true
                val isFavorite = if (isEpisode) currentSong?.song?.inLibrary != null else currentSong?.song?.liked == true
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = playerConnection::toggleLike,
                        modifier = Modifier.size(38.dp),
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    if (isFavorite) R.drawable.favorite else R.drawable.favorite_border,
                                ),
                            contentDescription = "Favorite",
                            tint = if (isFavorite) SpotifyGreen else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    songEngagement?.likeCount?.let { likes ->
                        if (likes.isNotBlank()) {
                            Text(
                                text = likes,
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.75f),
                                    ),
                                modifier = Modifier.padding(start = 2.dp),
                            )
                        }
                    }
                }

                // Right: Three dots More options button
                IconButton(
                    onClick = {
                        menuState.show {
                            PlayerMenu(
                                mediaMetadata = mediaMetadata,
                                playerBottomSheetState = state,
                                onToggleLyrics = { showInlineLyrics = !showInlineLyrics },
                                onShowSleepTimer = { showSleepTimerDialog = true },
                                onShowDetailsDialog = {
                                    mediaMetadata.id.let {
                                        bottomSheetPageState.show {
                                            ShowMediaInfo(it)
                                        }
                                    }
                                },
                                onDismiss = menuState::dismiss,
                            )
                        }
                    },
                    modifier = Modifier.size(38.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_horiz),
                        contentDescription = "More options",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 2. Soundwave Scrubber with Timestamps
            SoundwaveScrubber(
                position = sliderPosition ?: effectivePosition,
                duration = if (duration == C.TIME_UNSET) 0L else duration,
                onSeek = { seekPos ->
                    if (!isListenTogetherGuest) {
                        if (isCasting) {
                            castHandler?.seekTo(seekPos)
                            lastManualSeekTime = System.currentTimeMillis()
                        } else {
                            playerConnection.player.seekTo(seekPos)
                        }
                        position = seekPos
                    }
                },
                activeColor = Color.White,
                inactiveColor = Color.White.copy(alpha = 0.28f),
                audioSessionId = runCatching { playerConnection.player.audioSessionId }.getOrDefault(0),
                isPlaying = effectiveIsPlaying,
                mediaId = mediaMetadata.id,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(18.dp))

            // 3. Transport Controls Row: Shuffle, Prev, Glowing White Play/Pause, Next, Repeat
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
            ) {
                // Shuffle
                IconButton(
                    onClick = {
                        if (!isListenTogetherGuest) {
                            playerConnection.player.shuffleModeEnabled = !playerConnection.player.shuffleModeEnabled
                        }
                    },
                    enabled = !isListenTogetherGuest,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = "Shuffle",
                        tint = if (playerConnection.player.shuffleModeEnabled) SpotifyGreen else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp),
                    )
                }

                // Previous Track
                IconButton(
                    onClick = {
                        if (canSkipPrevious && !isListenTogetherGuest) {
                            playerConnection.seekToPrevious()
                        }
                    },
                    enabled = canSkipPrevious && !isListenTogetherGuest,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_previous),
                        contentDescription = "Previous",
                        tint = if (canSkipPrevious && !isListenTogetherGuest) Color.White else Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp),
                    )
                }

                // Center Play/Pause Button with Ambient Radial Glow
                val playBtnColor by animateColorAsState(
                    targetValue = if (effectiveIsPlaying) SpotifyGreen else Color.White,
                    animationSpec = tween(200),
                    label = "playerPlayBtnColor",
                )
                val playBtnGlowColors = if (effectiveIsPlaying) {
                    listOf(
                        SpotifyGreen.copy(alpha = 0.45f),
                        SpotifyGreen.copy(alpha = 0.15f),
                        Color.Transparent,
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color(0x331DB954),
                        Color.Transparent,
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(76.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(72.dp)
                                .background(
                                    brush =
                                        Brush.radialGradient(
                                            colors = playBtnGlowColors,
                                        ),
                                    shape = CircleShape,
                                ),
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(playBtnColor)
                                .clickable {
                                    if (isListenTogetherGuest) {
                                        playerConnection.toggleMute()
                                        return@clickable
                                    }
                                    if (isCasting) {
                                        if (castIsPlaying) castHandler?.pause() else castHandler?.play()
                                    } else if (playbackState == STATE_ENDED) {
                                        playerConnection.player.seekTo(0, 0)
                                        playerConnection.player.playWhenReady = true
                                    } else {
                                        playerConnection.player.togglePlayPause()
                                    }
                                }.focusRequester(focusRequester),
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    if (isListenTogetherGuest) {
                                        if (isMuted) R.drawable.volume_off else R.drawable.volume_up
                                    } else if (playbackState == STATE_ENDED) {
                                        R.drawable.replay
                                    } else if (effectiveIsPlaying) {
                                        R.drawable.pause
                                    } else {
                                        R.drawable.play
                                    }
                                ),
                            contentDescription = if (effectiveIsPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }

                // Next Track
                IconButton(
                    onClick = {
                        if (canSkipNext && !isListenTogetherGuest) {
                            playerConnection.seekToNext()
                        }
                    },
                    enabled = canSkipNext && !isListenTogetherGuest,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = "Next",
                        tint = if (canSkipNext && !isListenTogetherGuest) Color.White else Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp),
                    )
                }

                // Repeat Mode
                IconButton(
                    onClick = {
                        if (!isListenTogetherGuest) {
                            playerConnection.player.toggleRepeatMode()
                        }
                    },
                    enabled = !isListenTogetherGuest,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        painter =
                            painterResource(
                                when (repeatMode) {
                                    Player.REPEAT_MODE_ALL -> R.drawable.repeat
                                    Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                    else -> R.drawable.repeat
                                },
                            ),
                        contentDescription = "Repeat",
                        tint = if (repeatMode != Player.REPEAT_MODE_OFF) SpotifyGreen else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Two Equal-Sized Capsule Pills: Lyrics & Comments (Icons Only)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Pill 1: Lyrics
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (showInlineLyrics) SpotifyGreen.copy(alpha = 0.22f)
                            else Color(0xFF25272D)
                        )
                        .clickable {
                            showInlineLyrics = !showInlineLyrics
                        },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lyrics),
                        contentDescription = "Lyrics",
                        tint = if (showInlineLyrics) SpotifyGreen else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Pill 2: Comments
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (showCommentsSheet) SpotifyGreen.copy(alpha = 0.22f)
                            else Color(0xFF25272D)
                        )
                        .clickable {
                            showCommentsSheet = true
                        },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chat_bubble),
                        contentDescription = "Comments",
                        tint = if (showCommentsSheet) SpotifyGreen else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                val density = LocalDensity.current
                val verticalPadding =
                    max(
                        WindowInsets.systemBars.getTop(density),
                        WindowInsets.systemBars.getBottom(density),
                    )
                val verticalPaddingDp = with(density) { verticalPadding.toDp() }
                val verticalWindowInsets = WindowInsets(left = 0.dp, top = verticalPaddingDp, right = 0.dp, bottom = verticalPaddingDp)

                Row(
                    modifier =
                        Modifier
                            .windowInsetsPadding(
                                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).add(verticalWindowInsets),
                            ).padding(bottom = 24.dp)
                            .fillMaxSize(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .weight(1f)
                                .nestedScroll(state.preUpPostDownNestedScrollConnection),
                    ) {
                        SongVideoToggle(
                            isVideoMode = isVideoMode,
                            onModeChange = { newMode ->
                                isVideoMode = newMode
                            },
                            modifier = Modifier.padding(bottom = 6.dp),
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f),
                        ) {
                            if (isVideoMode && !state.isCollapsed && !isFullscreenVideo) {
                                mediaMetadata?.id?.let { videoId ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                    ) {
                                        YouTubeVideoPlayer(
                                            videoId = videoId,
                                            startSeconds = (effectivePosition / 1000).toInt(),
                                            isPlaying = effectiveIsPlaying,
                                            currentPositionMs = effectivePosition,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp)),
                                        )

                                        IconButton(
                                            onClick = { isFullscreenVideo = true },
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(10.dp)
                                                .size(36.dp)
                                                .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.fullscreen),
                                                contentDescription = "Fullscreen",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                }
                            } else {
                            val currentSliderPosition by rememberUpdatedState(sliderPosition)
                            val sliderPositionProvider = remember { { currentSliderPosition } }
                            val isExpandedProvider = remember(state) { { state.isExpanded } }
                            AnimatedContent(
                                targetState = showInlineLyrics,
                                label = "Lyrics",
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                            ) { showLyrics ->
                                if (showLyrics) {
                                    InlineLyricsView(
                                        mediaMetadata = mediaMetadata,
                                        showLyrics = showLyrics,
                                        positionProvider = { effectivePosition },
                                    )
                                } else {
                                    Thumbnail(
                                        sliderPositionProvider = sliderPositionProvider,
                                        modifier = Modifier.animateContentSize(),
                                        isPlayerExpanded = isExpandedProvider,
                                        isLandscape = true,
                                        isListenTogetherGuest = isListenTogetherGuest,
                                        onCollapse = { state.collapseSoft() },
                                        onQueueClick = { queueSheetState.expandSoft() },
                                    )
                                }
                            }
                        }
                    }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .weight(if (showInlineLyrics) 0.65f else 1f, false)
                                .animateContentSize()
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                    ) {
                        Spacer(Modifier.weight(1f))

                        mediaMetadata?.let {
                            controlsContent(it)
                        }

                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                            .animateContentSize(),
                ) {
                    ThumbnailHeader(
                        textColor = Color.White,
                        headerTitleText = if (showInlineLyrics) "Lyrics" else stringResource(R.string.now_playing),
                        onCollapse = { state.collapseSoft() },
                        onQueueClick = { queueSheetState.expandSoft() },
                        modifier = Modifier.statusBarsPadding(),
                    )

                    SongVideoToggle(
                        isVideoMode = isVideoMode,
                        onModeChange = { newMode ->
                            isVideoMode = newMode
                        },
                        modifier = Modifier.padding(bottom = 6.dp),
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (isVideoMode && !state.isCollapsed && !isFullscreenVideo) {
                            mediaMetadata?.id?.let { videoId ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                ) {
                                    YouTubeVideoPlayer(
                                        videoId = videoId,
                                        startSeconds = (effectivePosition / 1000).toInt(),
                                        isPlaying = effectiveIsPlaying,
                                        currentPositionMs = effectivePosition,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp)),
                                    )

                                    IconButton(
                                        onClick = { isFullscreenVideo = true },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(12.dp)
                                            .size(38.dp)
                                            .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.fullscreen),
                                            contentDescription = "Fullscreen",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp),
                                        )
                                    }
                                }
                            }
                        } else {
                            val currentSliderPosition by rememberUpdatedState(sliderPosition)
                            val sliderPositionProvider = remember { { currentSliderPosition } }
                            val isExpandedProvider = remember(state) { { state.isExpanded } }
                            AnimatedContent(
                                targetState = showInlineLyrics,
                                label = "Lyrics",
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                            ) { showLyrics ->
                                if (showLyrics) {
                                    InlineLyricsView(
                                        mediaMetadata = mediaMetadata,
                                        showLyrics = showLyrics,
                                        positionProvider = { effectivePosition },
                                    )
                                } else {
                                    Thumbnail(
                                        sliderPositionProvider = sliderPositionProvider,
                                        modifier = Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection),
                                        isPlayerExpanded = isExpandedProvider,
                                        isListenTogetherGuest = isListenTogetherGuest,
                                        onCollapse = { state.collapseSoft() },
                                        onQueueClick = { queueSheetState.expandSoft() },
                                        showHeader = false,
                                    )
                                }
                            }
                        }
                    }

                    mediaMetadata?.let {
                        controlsContent(it)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isFullScreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Queue(
                state = queueSheetState,
                playerBottomSheetState = state,
                background =
                    if (useBlackBackground) {
                        Color.Black
                    } else {
                        Color(0xFF0E1015)
                    },
                onBackgroundColor = onBackgroundColor,
                TextBackgroundColor = TextBackgroundColor,
                textButtonColor = textButtonColor,
                iconButtonColor = iconButtonColor,
                pureBlack = pureBlack,
                showInlineLyrics = showInlineLyrics,
                playerBackground = playerBackground,
                onToggleLyrics = {
                    showInlineLyrics = !showInlineLyrics
                },
            )
        }

        if (showCommentsSheet) {
            PlayerCommentsSheet(
                commentCount = songEngagement?.commentCount,
                comments = commentsList,
                isLoading = isLoadingComments,
                onDismiss = { showCommentsSheet = false },
            )
        }

        if (isFullscreenVideo && isVideoMode) {
            mediaMetadata?.let { metadata ->
                FullscreenVideoPlayer(
                    videoId = metadata.id,
                    metadata = metadata,
                    effectivePosition = effectivePosition,
                    effectiveIsPlaying = effectiveIsPlaying,
                    duration = if (duration == C.TIME_UNSET) 0L else duration,
                    onDismiss = { isFullscreenVideo = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InlineLyricsView(
    mediaMetadata: MediaMetadata?,
    showLyrics: Boolean,
    positionProvider: () -> Long,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue = null)
    val queueWindows by playerConnection.queueWindows.collectAsStateWithLifecycle(initialValue = emptyList())
    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsStateWithLifecycle(initialValue = -1)
    val lyrics = remember(currentLyrics) { currentLyrics?.lyrics?.trim() }
    val context = LocalContext.current
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()

    var appInForeground by remember {
        mutableStateOf(
            ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
        )
    }
    DisposableEffect(Unit) {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        val observer =
            LifecycleEventObserver { _, _ ->
                appInForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val nextMetadata =
        remember(queueWindows, currentWindowIndex) {
            if (currentWindowIndex >= 0 && currentWindowIndex + 1 < queueWindows.size) {
                queueWindows[currentWindowIndex + 1].mediaItem.metadata
            } else {
                null
            }
        }

    LaunchedEffect(mediaMetadata?.id, currentLyrics) {
        if (mediaMetadata != null && currentLyrics == null) {
            delay(500)
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val entryPoint =
                        EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            com.metrolist.music.di.LyricsHelperEntryPoint::class.java,
                        )
                    val lyricsHelper = entryPoint.lyricsHelper()
                    val fetchedLyricsWithProvider = lyricsHelper.getLyrics(mediaMetadata)
                    database.query {
                        upsert(LyricsEntity(mediaMetadata.id, fetchedLyricsWithProvider.lyrics, fetchedLyricsWithProvider.provider))
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    // Prefetch lyrics for the next queue item only while the lyrics pane is visible, the app is in the
    // foreground, and the current track's lyrics row has finished loading (avoids competing with the
    // active fetch).
    LaunchedEffect(
        nextMetadata?.id,
        showLyrics,
        appInForeground,
        mediaMetadata?.id,
        currentLyrics,
    ) {
        if (!showLyrics || !appInForeground || nextMetadata == null) return@LaunchedEffect
        val loadedForCurrent =
            currentLyrics?.let { lyrics ->
                mediaMetadata == null || lyrics.id == mediaMetadata.id
            } == true
        if (mediaMetadata != null && !loadedForCurrent) return@LaunchedEffect
        val nextId = nextMetadata.id
        delay(400)
        if (!showLyrics || !appInForeground || !isActive) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val existing = database.lyrics(nextId).first()
                if (existing != null) return@withContext
                val entryPoint =
                    EntryPointAccessors.fromApplication(
                        context.applicationContext,
                        com.metrolist.music.di.LyricsHelperEntryPoint::class.java,
                    )
                val lyricsHelper = entryPoint.lyricsHelper()
                val fetched = lyricsHelper.getLyrics(nextMetadata)
                database.query {
                    upsert(LyricsEntity(nextId, fetched.lyrics, fetched.provider))
                }
            } catch (_: Exception) {
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            lyrics == null -> {
                ContainedLoadingIndicator()
            }

            lyrics == LyricsEntity.LYRICS_NOT_FOUND -> {
                Text(
                    text = stringResource(R.string.lyrics_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }

            else -> {
                val lyricsContent: @Composable () -> Unit = {
                    Lyrics(
                        sliderPositionProvider = positionProvider,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        showLyrics = showLyrics,
                    )
                }
                ProvideTextStyle(
                    value =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        ),
                ) {
                    lyricsContent()
                }
            }
        }
    }
}

@Composable
private fun PlayerMoreMenuButton(
    mediaMetadata: MediaMetadata,
    state: BottomSheetState,
    textButtonColor: Color,
    iconButtonColor: Color,
) {
    val navController = LocalNavController.current
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(textButtonColor)
                .clickable {
                    menuState.show {
                        PlayerMenu(
                            mediaMetadata = mediaMetadata,
                            playerBottomSheetState = state,
                            onShowDetailsDialog = {
                                mediaMetadata.id.let {
                                    bottomSheetPageState.show {
                                        ShowMediaInfo(it)
                                    }
                                }
                            },
                            onDismiss = menuState::dismiss,
                        )
                    }
                },
    ) {
        Image(
            painter = painterResource(R.drawable.more_horiz),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconButtonColor),
        )
    }
}

/**
 * Spotify-styled pill toggle between Song (audio) and Video playback
 */
@Composable
fun SongVideoToggle(
    isVideoMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0x28FFFFFF))
            .padding(3.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Song Pill
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (!isVideoMode) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onModeChange(false) }
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.music_note),
                        contentDescription = null,
                        tint = if (!isVideoMode) Color.White else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Song",
                        fontSize = 12.sp,
                        fontWeight = if (!isVideoMode) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isVideoMode) Color.White else Color.White.copy(alpha = 0.6f),
                    )
                }
            }

            // Video Pill
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isVideoMode) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onModeChange(true) }
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = if (isVideoMode) Color.White else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Video",
                        fontSize = 12.sp,
                        fontWeight = if (isVideoMode) FontWeight.Bold else FontWeight.Medium,
                        color = if (isVideoMode) Color.White else Color.White.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

/**
 * Embedded hardware-accelerated YouTube music video player
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeVideoPlayer(
    videoId: String,
    startSeconds: Int = 0,
    isPlaying: Boolean = true,
    currentPositionMs: Long = 0L,
    isMini: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var webViewRef: WebView? = remember { null }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                webViewRef?.onResume()
                webViewRef?.invalidate()
                val sec = currentPositionMs / 1000.0
                webViewRef?.evaluateJavascript(
                    """
                    if (window.player && player.playVideo) {
                        player.playVideo();
                        if (player.getCurrentTime) {
                            var cur = player.getCurrentTime();
                            var diff = $sec - cur;
                            if (diff > 1.0 && diff <= 2.5) {
                                if (player.setPlaybackRate) player.setPlaybackRate(2.0);
                            } else if (diff > 0.3 && diff <= 1.0) {
                                if (player.setPlaybackRate) player.setPlaybackRate(1.5);
                            } else if (Math.abs(diff) > 2.5 && !window.isSeeking && player.getPlayerState && player.getPlayerState() !== 3) {
                                window.isSeeking = true;
                                player.seekTo($sec, true);
                                if (player.setPlaybackRate) player.setPlaybackRate(1.0);
                                setTimeout(function() { window.isSeeking = false; }, 2000);
                            }
                        }
                    }
                    """.trimIndent(),
                    null,
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(videoId) {
        val sec = currentPositionMs / 1000.0
        webViewRef?.evaluateJavascript(
            """
            window.targetVideoId = '$videoId';
            if (window.player && player.loadVideoById) {
                window.isSeeking = false;
                player.loadVideoById({
                    videoId: '$videoId',
                    startSeconds: $sec
                });
                player.mute();
                ${if (isPlaying) "player.playVideo();" else "player.pauseVideo();"}
            }
            """.trimIndent(),
            null,
        )
    }

    LaunchedEffect(isPlaying) {
        webViewRef?.evaluateJavascript(
            if (isPlaying) {
                "if (window.player && player.playVideo) { player.mute(); player.playVideo(); }"
            } else {
                "if (window.player && player.pauseVideo) { player.pauseVideo(); }"
            },
            null,
        )
    }

    LaunchedEffect(isMini) {
        webViewRef?.evaluateJavascript(
            if (isMini) {
                """
                (function() {
                    function killCc() {
                        try {
                            if (window.player) {
                                if (player.unloadModule) { player.unloadModule('captions'); player.unloadModule('cc'); }
                                if (player.setOption) {
                                    player.setOption('captions', 'track', {});
                                    player.setOption('cc', 'track', {});
                                    player.setOption('captions', 'fontSize', -3);
                                }
                            }
                        } catch(e) {}
                    }
                    killCc();
                    var intv = setInterval(killCc, 250);
                    setTimeout(function() { clearInterval(intv); }, 4000);
                })();
                """.trimIndent()
            } else {
                "if (window.player && player.loadModule) { player.loadModule('captions'); }"
            },
            null,
        )
    }

    LaunchedEffect(currentPositionMs / 500) {
        val sec = currentPositionMs / 1000.0
        webViewRef?.evaluateJavascript(
            """
            if (window.player && player.getCurrentTime && player.getPlayerState && player.getPlayerState() !== 0) {
                var cur = player.getCurrentTime();
                var diff = $sec - cur;
                if (diff > 1.0 && diff <= 2.5) {
                    if (player.setPlaybackRate && player.getPlaybackRate && player.getPlaybackRate() !== 2.0) {
                        player.setPlaybackRate(2.0);
                    }
                } else if (diff > 0.3 && diff <= 1.0) {
                    if (player.setPlaybackRate && player.getPlaybackRate && player.getPlaybackRate() !== 1.5) {
                        player.setPlaybackRate(1.5);
                    }
                } else if (diff < -0.3 && diff >= -2.5) {
                    if (player.setPlaybackRate && player.getPlaybackRate && player.getPlaybackRate() !== 0.75) {
                        player.setPlaybackRate(0.75);
                    }
                } else if (Math.abs(diff) <= 0.2) {
                    if (player.setPlaybackRate && player.getPlaybackRate && player.getPlaybackRate() !== 1.0) {
                        player.setPlaybackRate(1.0);
                    }
                } else if (Math.abs(diff) > 2.5 && !window.isSeeking && player.getPlayerState() !== 3) {
                    window.isSeeking = true;
                    player.seekTo($sec, true);
                    if (player.setPlaybackRate) player.setPlaybackRate(1.0);
                    setTimeout(function() { window.isSeeking = false; }, 2000);
                }
            }
            """.trimIndent(),
            null,
        )
    }

    val embedHtml = remember(videoId, isMini) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                html, body {
                    width: 100%; height: 100%;
                    background: #000;
                    overflow: hidden;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    pointer-events: none !important;
                    user-select: none;
                }
                #ytplayer {
                    width: 100%; height: 100%;
                    border: none;
                    pointer-events: none !important;
                    ${if (isMini) "transform: scale(1.25) translateY(-6%);" else ""}
                }
                .ytp-chrome-top, .ytp-show-cards-title, .ytp-title-channel, .ytp-watermark,
                .ytp-pause-overlay, .ytp-ce-element, .ytp-suggestion-set, .ytp-gradient-top,
                .ytp-chrome-bottom, .caption-window, .ytp-caption-window-bottom {
                    display: none !important;
                    opacity: 0 !important;
                    pointer-events: none !important;
                }
            </style>
        </head>
        <body>
            <div id="ytplayer"></div>
            <script>
                var tag = document.createElement('script');
                tag.src = "https://www.youtube.com/iframe_api";
                var firstScriptTag = document.getElementsByTagName('script')[0];
                firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                var player;
                window.isSeeking = false;
                window.targetVideoId = '$videoId';
                function disableCaptions() {
                    try {
                        if (player) {
                            if (player.unloadModule) {
                                player.unloadModule('captions');
                                player.unloadModule('cc');
                            }
                            if (player.setOption) {
                                player.setOption('captions', 'track', {});
                                player.setOption('cc', 'track', {});
                                player.setOption('captions', 'fontSize', -100);
                            }
                        }
                    } catch(e) {}
                }

                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('ytplayer', {
                        videoId: '$videoId',
                        playerVars: {
                            autoplay: 1,
                            controls: 0,
                            disablekb: 1,
                            fs: 0,
                            playsinline: 1,
                            rel: 0,
                            modestbranding: 1,
                            iv_load_policy: 3,
                            cc_load_policy: ${if (isMini) 0 else 1},
                            hl: 'en',
                            start: $startSeconds
                        },
                        events: {
                            'onReady': onPlayerReady,
                            'onStateChange': onPlayerStateChange
                        }
                    });
                }
                function onPlayerReady(event) {
                    player.mute();
                    if (window.targetVideoId && player.getVideoData && player.getVideoData().video_id !== window.targetVideoId) {
                        player.loadVideoById({
                            videoId: window.targetVideoId,
                            startSeconds: $startSeconds
                        });
                    }
                    if ($isMini) {
                        disableCaptions();
                        var intv = setInterval(disableCaptions, 250);
                        setTimeout(function() { clearInterval(intv); }, 4000);
                    } else if (player.loadModule) {
                        player.loadModule('captions');
                    }
                    ${if (isPlaying) "player.playVideo();" else "player.pauseVideo();"}
                }
                function onPlayerStateChange(event) {
                    if (event.data === 1) {
                        window.isSeeking = false;
                    } else if (event.data === 0) {
                        if (player && player.pauseVideo) {
                            player.pauseVideo();
                        }
                    }
                    if ($isMini) {
                        disableCaptions();
                    }
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier
            .then(if (!isMini) Modifier.aspectRatio(16f / 9f) else Modifier)
            .background(Color.Black),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                )
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                setBackgroundColor(android.graphics.Color.BLACK)
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                isClickable = false
                isFocusable = false
                webChromeClient = WebChromeClient()
                loadDataWithBaseURL("https://www.youtube-nocookie.com", embedHtml, "text/html", "UTF-8", null)
                webViewRef = this
            }
        },
        update = { webView ->
            webViewRef = webView
        },
        onRelease = { webView ->
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        },
    )
}

/**
 * YouTube Comments Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerCommentsSheet(
    commentCount: String?,
    comments: List<CommentItem>?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF12141C),
        contentColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                        ),
                    )
                    if (!commentCount.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = commentCount,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f),
                            ),
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Body
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                ) {
                    CircularProgressIndicator(
                        color = SpotifyGreen,
                        modifier = Modifier.size(36.dp),
                    )
                }
            } else if (comments.isNullOrEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                ) {
                    Text(
                        text = "No comments available for this track",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp),
                ) {
                    items(comments) { comment ->
                        CommentRow(comment = comment)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: CommentItem,
    isReply: Boolean = false,
) {
    var showReplies by rememberSaveable(comment.id) { mutableStateOf(false) }
    var repliesList by remember { mutableStateOf<List<CommentItem>?>(null) }
    var isLoadingReplies by remember { mutableStateOf(false) }
    var isLikedState by rememberSaveable(comment.id) { mutableStateOf(comment.isLiked) }
    var likeCountState by rememberSaveable(comment.id) { mutableStateOf(comment.likeCount) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AsyncImage(
            model = comment.authorAvatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(if (isReply) 26.dp else 34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isReply) 12.sp else 13.sp,
                        color = Color.White,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                comment.publishedTime?.let { time ->
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.45f),
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isReply) 12.sp else 13.sp,
                    color = Color(0xFFD6D8E0),
                    lineHeight = 18.sp,
                ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            val isLoggedIn = "SAPISID" in parseCookieString(YouTube.cookie.orEmpty())
                            if (!isLoggedIn) {
                                Toast.makeText(context, "Log in with YouTube Music to like comments", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            val targetAction = if (isLikedState) comment.unlikeAction else comment.likeAction
                            if (targetAction.isNullOrBlank()) {
                                Toast.makeText(context, "Action not available", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            val prevLiked = isLikedState
                            val prevCount = likeCountState
                            val newLiked = !prevLiked
                            isLikedState = newLiked
                            likeCountState = if (newLiked) {
                                val c = prevCount?.toLongOrNull()
                                if (c != null) (c + 1).toString() else (prevCount ?: "1")
                            } else {
                                val c = prevCount?.toLongOrNull()
                                if (c != null) {
                                    val dec = maxOf(0L, c - 1)
                                    if (dec == 0L) null else dec.toString()
                                } else prevCount
                            }
                            coroutineScope.launch {
                                val result = YouTube.performCommentAction(targetAction)
                                if (result.isFailure) {
                                    isLikedState = prevLiked
                                    likeCountState = prevCount
                                    Toast.makeText(context, "Failed to update like", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Icon(
                        painter = painterResource(if (isLikedState) R.drawable.favorite else R.drawable.favorite_border),
                        contentDescription = if (isLikedState) "Unlike comment" else "Like comment",
                        tint = if (isLikedState) SpotifyGreen else Color.White.copy(alpha = 0.45f),
                        modifier = Modifier.size(13.dp),
                    )
                    if (!likeCountState.isNullOrBlank()) {
                        Text(
                            text = likeCountState.orEmpty(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = if (isLikedState) SpotifyGreen else Color.White.copy(alpha = 0.45f),
                            ),
                        )
                    }
                }

                val hasReplies = !comment.replyToken.isNullOrBlank() || !comment.replyCount.isNullOrBlank()
                if (hasReplies) {
                    val label = comment.replyCount ?: "Replies"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                val token = comment.replyToken
                                if (!showReplies && repliesList == null && token != null) {
                                    isLoadingReplies = true
                                    coroutineScope.launch {
                                        repliesList = com.metrolist.innertube.YouTube.commentReplies(token).getOrNull()
                                        isLoadingReplies = false
                                    }
                                }
                                showReplies = !showReplies
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                    ) {
                        Icon(
                            painter = painterResource(if (showReplies) R.drawable.expand_less else R.drawable.expand_more),
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = if (showReplies) "Hide replies" else label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpotifyGreen,
                            ),
                        )
                    }
                }
            }

            if (isLoadingReplies) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    CircularProgressIndicator(
                        color = SpotifyGreen,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            if (showReplies && !repliesList.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repliesList!!.forEach { replyItem ->
                        CommentRow(comment = replyItem, isReply = true)
                    }
                }
            }
        }
    }
}

/**
 * Immersive Fullscreen Video Player with Custom In-App Overlay Controls
 */
@Composable
fun FullscreenVideoPlayer(
    videoId: String,
    metadata: MediaMetadata,
    effectivePosition: Long,
    effectiveIsPlaying: Boolean,
    duration: Long,
    onDismiss: () -> Unit,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val playbackState by playerConnection.playbackState.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var userSeekingPosition by remember { mutableStateOf<Long?>(null) }
    val displayPosition = userSeekingPosition ?: effectivePosition

    // Auto-hide controls after 3.5 seconds of user inactivity when playing
    LaunchedEffect(showControls, effectiveIsPlaying, userSeekingPosition) {
        if (showControls && effectiveIsPlaying && userSeekingPosition == null) {
            delay(3500L)
            showControls = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        BackHandler {
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    showControls = !showControls
                },
            contentAlignment = Alignment.Center,
        ) {
            // Fullscreen YouTube Player (Centered 16:9 ratio, fills width)
            YouTubeVideoPlayer(
                videoId = videoId,
                startSeconds = (effectivePosition / 1000).toInt(),
                isPlaying = effectiveIsPlaying,
                currentPositionMs = effectivePosition,
                isMini = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )

            // Custom Overlay Controls with Smooth Animation
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(250)),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.85f),
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.88f),
                                ),
                            ),
                        ),
                ) {
                    // Top Bar: Back/Exit Fullscreen, Title & Artist
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.TopStart),
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Exit Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = metadata.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (metadata.artists.isNotEmpty()) {
                                Text(
                                    text = metadata.artists.joinToString { it.name },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fullscreen_exit),
                                contentDescription = "Exit Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }

                    // Center Transport Controls: Previous, Glow Play/Pause, Next
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                    ) {
                        IconButton(
                            onClick = {
                                if (canSkipPrevious) {
                                    playerConnection.seekToPrevious()
                                }
                            },
                            enabled = canSkipPrevious,
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.skip_previous),
                                contentDescription = "Previous",
                                tint = if (canSkipPrevious) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(30.dp),
                            )
                        }

                        Spacer(Modifier.width(28.dp))

                        // Glowing Center Play/Pause Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(76.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = if (effectiveIsPlaying) {
                                                listOf(
                                                    SpotifyGreen.copy(alpha = 0.45f),
                                                    SpotifyGreen.copy(alpha = 0.15f),
                                                    Color.Transparent,
                                                )
                                            } else {
                                                listOf(
                                                    Color.White.copy(alpha = 0.35f),
                                                    Color(0x331DB954),
                                                    Color.Transparent,
                                                )
                                            },
                                        ),
                                        shape = CircleShape,
                                    ),
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(if (effectiveIsPlaying) SpotifyGreen else Color.White)
                                    .clickable {
                                        if (playbackState == STATE_ENDED) {
                                            playerConnection.player.seekTo(0, 0)
                                            playerConnection.player.playWhenReady = true
                                        } else {
                                            playerConnection.player.togglePlayPause()
                                        }
                                    },
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (playbackState == STATE_ENDED) {
                                            R.drawable.replay
                                        } else if (effectiveIsPlaying) {
                                            R.drawable.pause
                                        } else {
                                            R.drawable.play
                                        },
                                    ),
                                    contentDescription = if (effectiveIsPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }

                        Spacer(Modifier.width(28.dp))

                        IconButton(
                            onClick = {
                                if (canSkipNext) {
                                    playerConnection.seekToNext()
                                }
                            },
                            enabled = canSkipNext,
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.skip_next),
                                contentDescription = "Next",
                                tint = if (canSkipNext) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }

                    // Bottom Bar: Elapsed Time, Progress Bar, Total Duration, Exit Fullscreen
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                    ) {
                        Slider(
                            value = (displayPosition.toFloat() / duration.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f),
                            onValueChange = { frac ->
                                userSeekingPosition = (frac * duration).toLong()
                            },
                            onValueChangeFinished = {
                                userSeekingPosition?.let { targetMs ->
                                    playerConnection.player.seekTo(targetMs)
                                    userSeekingPosition = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = makeTimeString(displayPosition),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                ),
                            )

                            Text(
                                text = makeTimeString(duration),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

