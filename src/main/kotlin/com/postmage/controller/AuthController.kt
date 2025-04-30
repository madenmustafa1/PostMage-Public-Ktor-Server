package com.postmage.controller

import com.postmage.enums.AppUserRole
import com.postmage.enums.StatusCodeUtil
import com.postmage.util.extensions.verifyToken
import com.postmage.plugins.koin
import com.postmage.repo.sendErrorData
import com.postmage.service.ErrorMessage
import com.postmage.util.http_util.GsonUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*


suspend fun accessManager(call: ApplicationCall, vararg role: AppUserRole, accessRoute: suspend() -> Unit) {
    val header = call.request.headers["Authorization"] ?: return sendAuthStatus(call)
    val res = header.verifyToken(userRole = role)
    if (!res) return sendAuthStatus(call)
    accessRoute()
}


private suspend fun sendAuthStatus(call: ApplicationCall) {
    call.response.status(HttpStatusCode.Unauthorized)
    call.respond(
        GsonUtil.gsonToJson(
            sendErrorData<ErrorMessage>(
                message = koin.appMessages.UNAUTHORIZED,
                statusCode = StatusCodeUtil.UNAUTHORIZED
            ),
        ),
    )
}