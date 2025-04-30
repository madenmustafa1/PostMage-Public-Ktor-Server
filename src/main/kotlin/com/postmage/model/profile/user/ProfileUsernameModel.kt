package com.postmage.model.profile.user

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUsernameModel(
    var accessibility: Boolean? = null,
    var userName: String? = null,
    var editTime: Long? = null,
    var previousUsernames: ArrayList<ProfilePreviousUsernameModel>? = arrayListOf()
)

@Serializable
data class ProfilePreviousUsernameModel(
    val editTime: Long?,
    val userName: String?,
)
