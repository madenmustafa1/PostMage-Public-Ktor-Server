package com.postmage.vm

import com.postmage.model.user_config.getLanguageType
import com.postmage.model.user_config.toHeaders
import com.postmage.repo.PlatformRepository
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCallResponseException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.extensions.deleteFileAsync
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class PlatformVM(
    private val repository: PlatformRepository
) {

    suspend fun getCurrentMobileVersion(call: ApplicationCall) {
        runCatchableException {
            val result = repository.getCurrentMobileVersion(call.request.toHeaders())

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