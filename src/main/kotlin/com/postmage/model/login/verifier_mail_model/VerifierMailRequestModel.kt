package com.postmage.model.login.verifier_mail_model

data class VerifierMailRequestModel(
    val mail: String,
    val accessToken: String,
    val userId: String
)
