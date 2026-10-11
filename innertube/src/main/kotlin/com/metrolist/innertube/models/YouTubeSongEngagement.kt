package com.metrolist.innertube.models

data class YouTubeSongEngagement(
    val likeCount: String? = null,
    val commentCount: String? = null,
    val commentsToken: String? = null,
)

data class CommentItem(
    val id: String = "",
    val authorName: String,
    val authorAvatar: String? = null,
    val content: String,
    val publishedTime: String? = null,
    val likeCount: String? = null,
    val isLiked: Boolean = false,
    val likeAction: String? = null,
    val unlikeAction: String? = null,
    val replyCount: String? = null,
    val replyToken: String? = null,
    val replies: List<CommentItem> = emptyList(),
)
