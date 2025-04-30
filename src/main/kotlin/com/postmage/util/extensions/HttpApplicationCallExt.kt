package com.postmage.util.extensions

import io.ktor.http.*
import io.ktor.server.request.*

suspend fun ApplicationRequest.toMap(): Map<String, Any>? {
    val contentType = call.request.contentType()

    return try {
        when {
            contentType.match(ContentType.Application.Json) -> {
                mapOf("content" to call.receive<String>())
            }

            contentType.match(ContentType.Application.FormUrlEncoded) -> {
                call.receiveParameters().entries().associate { it.key to it.value }
            }

            else -> null
        }

    } catch (e: Exception) {
        null
    }

}