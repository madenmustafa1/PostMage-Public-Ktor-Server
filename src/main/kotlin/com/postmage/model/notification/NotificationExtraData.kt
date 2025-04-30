package com.postmage.model.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationExtraData(
    var message: String? = null,
    var postId: String? = null,
    var commentId: String? = null,
    var userId: String? = null,
    var groupId: String? = null,
    var name: String? = null
)