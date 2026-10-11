/**
 * Vibes Project (C) 2026
 * Licensed under GPL-3.0
 */

package com.metrolist.music.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.TimeUnit

object PortfolioEndpoint {
    private const val PREFS_NAME = "portfolio_endpoint_cache"
    private const val KEY_URL = "portfolio_url"
    private const val KEY_LAST_FETCH = "portfolio_last_fetch"
    private const val ENDPOINT_URL = "https://raw.githubusercontent.com/xpansieve/portfolio-endpoint/refs/heads/main/endpoint.json"
    const val DEFAULT_URL = "https://xpansieve.studio"
    private const val CACHE_DURATION_MS = 3L * 24 * 60 * 60 * 1000 // 3 days

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun getCachedUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL
    }

    suspend fun fetchUrlIfNeeded(context: Context, onUpdated: ((String) -> Unit)? = null): String = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedUrl = prefs.getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL
        val lastFetch = prefs.getLong(KEY_LAST_FETCH, 0L)
        val now = System.currentTimeMillis()

        if (now - lastFetch < CACHE_DURATION_MS && cachedUrl.isNotBlank() && cachedUrl != DEFAULT_URL) {
            return@withContext cachedUrl
        }

        try {
            val request = Request.Builder()
                .url(ENDPOINT_URL)
                .header("User-Agent", "Vibes-Android")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body.string()
                if (bodyString.isNotBlank()) {
                    val json = JSONObject(bodyString)
                    val fetchedUrl = json.optString("url", "").trim()
                    if (fetchedUrl.startsWith("http://") || fetchedUrl.startsWith("https://")) {
                        prefs.edit()
                            .putString(KEY_URL, fetchedUrl)
                            .putLong(KEY_LAST_FETCH, now)
                            .apply()
                        withContext(Dispatchers.Main) {
                            onUpdated?.invoke(fetchedUrl)
                        }
                        return@withContext fetchedUrl
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "PortfolioEndpoint: Failed to fetch endpoint.json")
        }

        return@withContext cachedUrl
    }
}
