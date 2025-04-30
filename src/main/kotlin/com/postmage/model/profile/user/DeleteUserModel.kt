package com.postmage.model.profile.user

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserModel(
    val mail: String? = null,
    val password: String? = null
)