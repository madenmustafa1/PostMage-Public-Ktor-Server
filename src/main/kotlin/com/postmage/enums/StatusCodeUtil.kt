package com.postmage.enums

import io.ktor.http.*

object StatusCodeUtil {
    const val SERVER_ERROR = 500
    const val BAD_REQUEST = 400
    const val FORBIDDEN = 403
    const val UNAUTHORIZED = 401
    const val SUCCESS = 200

    fun errHandle(errCode: Int): HttpStatusCode {
        return when(errCode) {
            BAD_REQUEST -> HttpStatusCode.BadRequest
            UNAUTHORIZED -> HttpStatusCode.Unauthorized
            FORBIDDEN -> HttpStatusCode.Forbidden
            else -> HttpStatusCode.InternalServerError
        }
    }
}