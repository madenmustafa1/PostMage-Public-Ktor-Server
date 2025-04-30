package com.postmage.model.login.password

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordAccessTokenRequestModel(
    val mailAddress: String,
    val accessToken: String,
    val reqPassword: String
)
