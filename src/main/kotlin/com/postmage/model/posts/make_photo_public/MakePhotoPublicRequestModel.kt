package com.postmage.model.posts.make_photo_public

import kotlinx.serialization.Serializable

@Serializable
data class MakePhotoPublicRequestModel(
    var postId: String? = null,
    var isVisible: Boolean = true
)
