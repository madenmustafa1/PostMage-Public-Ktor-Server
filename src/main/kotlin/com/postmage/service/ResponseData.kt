package com.postmage.service

import com.postmage.enums.StatusCodeUtil
import kotlinx.serialization.Serializable

data class ResponseData<T>(
    val status: Status,
    val data: T?,
    val message: ErrorMessage?
) {
    companion object {
        fun <T> success(data: T?): ResponseData<T> {
            return ResponseData(Status.SUCCESS, data, null)
        }

        fun <T> error(
            msg: ErrorMessage,
            data: T?
        ): ResponseData<T> {
            return ResponseData(Status.ERROR, data, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

@Serializable
data class ErrorMessage(
    val message: String?,
    val statusCode: Int = StatusCodeUtil.SERVER_ERROR
)
