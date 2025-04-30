package com.postmage.model.posts.update_posts

import com.postmage.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class UserCommentModel(
    val userId: String,
    val comment: String = "",
    var photoName: String? = "",
    var nameSurname: String? = "",
    val creationTime: Long = DateUtil.getTimeNow()
)