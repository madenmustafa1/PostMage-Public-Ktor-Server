package com.postmage.model.login.password

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordSendMailRequestModel(
    val mailAddress: String,
)