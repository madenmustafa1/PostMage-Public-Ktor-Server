package com.postmage.model.posts.followed_users

import kotlinx.serialization.Serializable

@Serializable
data class PostOfFollowedUsers(
    var limit: Int = /* Max */ 100
)
