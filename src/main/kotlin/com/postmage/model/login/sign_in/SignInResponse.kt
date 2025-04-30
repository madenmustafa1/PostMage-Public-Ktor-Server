package com.postmage.model.login.sign_in


import kotlinx.serialization.Serializable

@Serializable
data class SignInResponseModel(
    val token: String?,
    val userId: String?,
    val isSuccess: Boolean
)