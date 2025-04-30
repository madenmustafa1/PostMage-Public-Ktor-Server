package com.postmage.service.notification

import com.postmage.model.notification.AppSendNotificationModel
import com.postmage.model.notification.NotificationModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.service.ResponseData

interface NotificationInterface {
    suspend fun sendNotification(headers: UserRequestHeaders, model: AppSendNotificationModel)
    suspend fun getNotificationList(headers: UserRequestHeaders): ResponseData<ArrayList<NotificationModel>>
}