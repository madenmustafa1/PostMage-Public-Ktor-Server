package com.postmage.model.posts.add_posts

import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class PostHashtagsModel(
    val hashtag: String,
    val creationTime: Long = DateUtil.getTimeNow(),
)