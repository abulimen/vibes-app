/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrolist.innertube.YouTube
import com.metrolist.innertube.pages.ChartsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor() : ViewModel() {
    private val _chartsPage = MutableStateFlow<ChartsPage?>(null)
    val chartsPage = _chartsPage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadCharts()
    }

    fun loadCharts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            YouTube.getChartsPage()
                .onSuccess { page ->
                    if (page.sections.isNotEmpty()) {
                        _chartsPage.value = page
                    } else {
                        fallbackExplore()
                    }
                }
                .onFailure {
                    fallbackExplore()
                }

            _isLoading.value = false
        }
    }

    private suspend fun fallbackExplore() {
        val sections = mutableListOf<ChartsPage.ChartSection>()
        YouTube.explore().onSuccess { explorePage ->
            if (explorePage.newReleaseAlbums.isNotEmpty()) {
                sections.add(
                    ChartsPage.ChartSection(
                        title = "New Releases",
                        items = explorePage.newReleaseAlbums,
                        chartType = ChartsPage.ChartType.NEW_RELEASES,
                    )
                )
            }
        }
        YouTube.newReleaseAlbums().onSuccess { albums ->
            if (albums.isNotEmpty() && sections.none { it.title == "New Releases" }) {
                sections.add(
                    ChartsPage.ChartSection(
                        title = "New Releases",
                        items = albums,
                        chartType = ChartsPage.ChartType.NEW_RELEASES,
                    )
                )
            }
        }
        if (sections.isNotEmpty()) {
            _chartsPage.value = ChartsPage(sections = sections, continuation = null)
        } else {
            _error.value = "Failed to load charts"
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            _chartsPage.value?.continuation?.let { continuation ->
                _isLoading.value = true
                YouTube.getChartsPage(continuation)
                    .onSuccess { newPage ->
                        _chartsPage.value = _chartsPage.value?.copy(
                            sections = _chartsPage.value?.sections.orEmpty() + newPage.sections,
                            continuation = newPage.continuation
                        )
                    }
                    .onFailure { e ->
                        _error.value = "Failed to load more: ${e.message}"
                    }
                _isLoading.value = false
            }
        }
    }
}
