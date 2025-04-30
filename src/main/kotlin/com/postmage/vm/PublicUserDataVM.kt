package com.postmage.vm

import com.postmage.model.user_config.getLanguageType
import com.postmage.repo.PublicUserDataRepository
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.extensions.deleteFileAsync
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class PublicUserDataVM(
    private val repository: PublicUserDataRepository
) {

    suspend fun getPhoto(call: ApplicationCall) {
        runCatchableException {
            val result = repository.getPostPhoto(call.request.queryParameters["postId"] ?: "")

            result.data?.let {
                call.respondFile(it)
                call.response.status(HttpStatusCode.OK)

                it.deleteFileAsync(100)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getPublicPhoto(call: ApplicationCall) {
        runCatchableException {
            val userId = call.request.queryParameters["userId"] ?: ""
            val photoName = call.request.queryParameters["photoName"] ?: ""
            val result = repository.getPublicPhoto(userId = userId, photoName = photoName)

            result.data?.let {
                call.respondFile(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getUserProfileInfoWithUsername(call: ApplicationCall) {
        runCatchableException {
            val username = call.request.queryParameters["username"] ?: ""
            val result = repository.getUserProfileInfoWithUsername(username)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

    suspend fun getPostsWithUserName(call: ApplicationCall) {
        runCatchableException {
            val username = call.request.queryParameters["username"] ?: ""
            val result = repository.getUserPostsWithUserName(username)

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return@runCatchableException
            }
            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        }.catchException {
            runCallResponseException(call, it, call.request.getLanguageType())
        }
    }

}