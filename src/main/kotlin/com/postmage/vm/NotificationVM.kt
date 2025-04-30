package com.postmage.vm

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.NotificationExtraData
import com.postmage.model.notification.NotificationType
import com.postmage.model.user_config.toHeaders
import com.postmage.plugins.koin
import com.postmage.repo.NotificationRepository
import com.postmage.util.AppMessages
import com.postmage.util.http_util.sendException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*

class NotificationVM(
    private val repository: NotificationRepository,
    private val appMessages: AppMessages
) {

    suspend fun sendNotification(call: ApplicationCall) {
        try {
            val model = AppSendNotificationModel(
                "648b6e572e99690df44318e4",
                "65bcda6100bc566c62846ab1",
                "Body",
                "Title",
                notificationType = NotificationType.LIKE,
                notificationExtraData = NotificationExtraData()
            )

            repository.sendNotification(call.request.toHeaders(), model)
            /*
            val body = call.receiveNullable<AppSendNotificationModel>() ?: throw Exception()
            repository.sendNotification(call.request.headers["Authorization"]!!, body)
             */
            call.response.status(HttpStatusCode.OK)
        } catch (e: NullPointerException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.UNAUTHORIZED,
                errorMessage = koin.appMessages.UNAUTHORIZED
            )
        } catch (e: MismatchedInputException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: CannotTransformContentToTypeException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: Exception) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.SERVER_ERROR,
                errorMessage = koin.appMessages.SERVER_ERROR
            )
        }
    }

    suspend fun getNotificationList(call: ApplicationCall) {
        try {
            val result = repository.getNotificationList(call.request.toHeaders())

            result.data?.let {
                call.respond(it)
                call.response.status(HttpStatusCode.OK)
                return
            }

            sendException(
                call = call,
                statusCode = result.message?.statusCode ?: 500,
                errorMessage = result.message?.message ?: ""
            )
        } catch (e: NullPointerException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.UNAUTHORIZED,
                errorMessage = koin.appMessages.UNAUTHORIZED
            )
        } catch (e: MismatchedInputException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: CannotTransformContentToTypeException) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.BAD_REQUEST,
                errorMessage = koin.appMessages.MODEL_IS_NOT_VALID
            )
        } catch (e: Exception) {
            sendException(
                call = call,
                statusCode = StatusCodeUtil.SERVER_ERROR,
                errorMessage = koin.appMessages.SERVER_ERROR
            )
        }
    }
}