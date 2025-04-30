package com.postmage.model.hashtags

import com.postmage.model.posts.get_posts.GetUserPostModel
import kotlinx.serialization.Serializable

@Serializable
data class GetHashtagPostsResponseModel(
    val hashtagId: String,
    val hashtag: String,
    val posts: ArrayList<GetUserPostModel>,
)