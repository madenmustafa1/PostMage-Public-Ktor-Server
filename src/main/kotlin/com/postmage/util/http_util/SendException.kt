package com.postmage.util.http_util

import com.postmage.enums.StatusCodeUtil
import com.postmage.repo.sendErrorData
import com.postmage.service.ErrorMessage
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun sendException(
    call: ApplicationCall,
    statusCode: Int,
    errorMessage: String
) {
    CoroutineScope(Dispatchers.Unconfined).launch {
        call.response.status(StatusCodeUtil.errHandle(statusCode))
        call.respond(
            GsonUtil.gsonToJson(
                sendErrorData<ErrorMessage>(errorMessage, statusCode)
            )
        )
    }
}