package com.postmage.model.login.sign_up

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponseModel(
    val token: String?,
    val userId: String?,
    val isSuccess: Boolean
)