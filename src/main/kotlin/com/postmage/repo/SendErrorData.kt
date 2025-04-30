package com.postmage.repo

import com.postmage.enums.StatusCodeUtil
import com.postmage.service.ErrorMessage
import com.postmage.service.ResponseData


fun <T>sendErrorData(message: String, statusCode: Int? = null) =
    ResponseData.error<T>(
        ErrorMessage(
            message = message,
            statusCode = statusCode ?: StatusCodeUtil.SERVER_ERROR
        ), null
    )
