package com.postmage.model.mail

import kotlinx.serialization.Serializable

@Serializable
data class SendMailModel(
    val title: String,
    val message: String,
    val mailAddress: String
)
