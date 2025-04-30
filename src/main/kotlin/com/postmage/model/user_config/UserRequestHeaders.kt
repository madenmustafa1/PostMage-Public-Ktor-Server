package com.postmage.model.user_config

import com.postmage.util.extensions.authToDataClass
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.login.token.TokenDataModel
import com.postmage.util.Constants
import io.ktor.server.request.*

data class UserRequestHeaders(
    val tokenData: TokenDataModel,
    val language: LanguageType
)

fun ApplicationRequest.toHeaders(): UserRequestHeaders {
    return UserRequestHeaders(
        tokenData = (headers["Authorization"] ?: "").authToDataClass()!!,
        language = LanguageType.getLanguageType(headers["Accept-Language"])
    )
}

fun ApplicationRequest.getLanguageType() = LanguageType.getLanguageType(headers["Accept-Language"])

fun ApplicationRequest.verifyClientTokenSecret(): Boolean {
    return headers["Token-Secret"] == Constants.CLIENT_TOKEN_SECRET
}

