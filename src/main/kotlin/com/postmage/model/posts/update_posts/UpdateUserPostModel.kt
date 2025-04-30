package com.postmage.model.posts.update_posts


import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPostModel(
    var description: String? = null,
    var objectId: String? = null,
    var likeUserId: String? = null,
    var comment: UserCommentModel? = null,
    var isDeleted: Boolean? = null
)
