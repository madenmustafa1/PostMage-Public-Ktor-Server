package com.postmage.model.hashtags

import kotlinx.serialization.Serializable

@Serializable
data class PopularHashtagsModel(
    val hashtagId: String,
    val name: String,
    val creationTime: Long,
    val creatorId: String,
    val editTime: Long,
    val totalCount: Long,
)
