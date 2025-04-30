package com.postmage.model.posts.get_posts

import kotlinx.serialization.Serializable

@Serializable
data class GetUserPostPhotoModel(
    val id: String,
    val photoName: String,
)
