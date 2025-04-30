package com.postmage.model.login.sign_in

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestModel(
    val mail: String?,
    val password: String?,
)

