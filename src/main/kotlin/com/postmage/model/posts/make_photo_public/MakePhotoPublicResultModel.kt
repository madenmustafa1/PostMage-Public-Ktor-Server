package com.postmage.model.posts.make_photo_public

import kotlinx.serialization.Serializable

@Serializable
data class MakePhotoPublicResultModel(
    val isSuccess: Boolean = false,
    val postUrl: String? = null
)
