package com.postmage.model.login.password

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequestModel(
    val mailAddress: String,
    val accessToken: String,
    val objectId: String,
    val reqPassword: String
)