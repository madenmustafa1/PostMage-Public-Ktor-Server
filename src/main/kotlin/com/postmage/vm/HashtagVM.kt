package com.postmage.vm

import com.postmage.model.hashtags.GetHashtagPostsRequestModel
import com.postmage.model.user_config.getLanguageType
import com.postmage.model.user_config.toHeaders
import com.postmage.repo.HashtagRepository
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class HashtagVM(
    private val repository: HashtagRepository
) {

    suspend fun getPopularHashtags(call: ApplicationCall) = runCatchableException {
        val result = repository.getPopularHashtags(call.request.toHeaders())
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

    suspend fun getHashtagPosts(call: ApplicationCall) = runCatchableException {
        val model = GetHashtagPostsRequestModel()

        model.hashtagId = call.request.queryParameters["hashtagId"]
        model.hashtagName = call.request.queryParameters["hashtagName"]
        model.postSorted = call.request.queryParameters["postSorted"]?.toIntOrNull()

        val result = repository.getHashtagPosts(call.request.toHeaders(), model)
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