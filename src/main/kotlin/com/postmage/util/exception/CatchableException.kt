package com.postmage.util.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.postmage.BUILD_TYPE
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.app.app_message.LanguageType
import com.postmage.model.app.build_type.AppBuildType
import com.postmage.plugins.koin
import com.postmage.repo.sendErrorData
import com.postmage.service.ErrorMessage
import com.postmage.util.http_util.GsonUtil
import com.postmage.util.http_util.sendException
import com.postmage.util.strings.AppMessageUtil
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*

suspend fun <T> runCatchableException(
    block: suspend () -> T
): Result<T> {
    val result = runCatching {
        block()
    }

    if (result.isFailure) {
        koin.exceptionLogger.execute(result.exceptionOrNull())

        if (BUILD_TYPE == AppBuildType.DEV) {
            println("@@@@@ Exception Message: " + result.exceptionOrNull()?.localizedMessage)
        }
    }

    return result
}

fun <T> Result<T>.catchException(onFailure: (Throwable) -> Unit): Result<T> {
    onFailure(exceptionOrNull() ?: return this)
    return this
}


fun runCallResponseException(
    call: ApplicationCall,
    throwable: Throwable,
    languageType: LanguageType
) {
    val appMessage = AppMessageUtil.getAppMessages(languageType)

    val statusCode = when (throwable) {
        is NullPointerException,
        is MismatchedInputException,
        is CannotTransformContentToTypeException -> StatusCodeUtil.BAD_REQUEST
        else -> StatusCodeUtil.SERVER_ERROR
    }

    val errorMessage = when (throwable) {
        is NullPointerException,
        is MismatchedInputException,
        is CannotTransformContentToTypeException -> appMessage.modelIsNotValid
        else -> appMessage.serverError
    }

    sendException(
        call = call,
        statusCode = statusCode,
        errorMessage = errorMessage
    )
}