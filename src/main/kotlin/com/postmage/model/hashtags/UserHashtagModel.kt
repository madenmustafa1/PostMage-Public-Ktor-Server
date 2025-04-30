package com.postmage.model.hashtags

import kotlinx.serialization.Serializable

@Serializable
data class UserHashtagModel(
    val objectId: String,
    val userId: String,
    val groupId: String = "",
    val postId: String,
    val creationTime: Long,
    val hashtag: String,
)
