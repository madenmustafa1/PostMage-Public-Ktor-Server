package com.postmage.repo

import com.postmage.util.extensions.authToDataClass
import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.NotificationModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData
import com.postmage.service.notification.NotificationInterface
import com.postmage.service.notification.NotificationService
import com.postmage.util.AppMessages

class NotificationRepository(
    private val notificationService: NotificationService,
    private val appMessages: AppMessages
) : NotificationInterface {
    override suspend fun sendNotification(headers: UserRequestHeaders, model: AppSendNotificationModel) {
        try {
            with(model) {
                if (toUserId.isEmpty()) return
                if (fromUserId.isEmpty()) return
                //if (body.isEmpty()) return
                //if (title.isEmpty()) return
            }

            notificationService.sendNotification(headers, model)
        } catch (_: Exception) { }
    }

    override suspend fun getNotificationList(headers: UserRequestHeaders): ResponseData<ArrayList<NotificationModel>> {
        return try {
            notificationService.getNotificationList(headers)
        } catch (e: Exception) {
            sendErrorData(appMessages.SERVER_ERROR)
        }
    }

}