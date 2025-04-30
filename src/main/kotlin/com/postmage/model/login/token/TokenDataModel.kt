package com.postmage.model.login.token

data class TokenDataModel(
    val iss: String,
    val exp: Long,
    val uuid: String,
    val userId: String,
    val userRole: Int
)
