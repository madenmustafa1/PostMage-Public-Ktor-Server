package com.postmage.model.notification

import com.postmage.util.Constants

data class AppSendNotificationModel(
    val fromUserId: String,
    val toUserId: String,
    val body: String? = null,
    val title: String = Constants.APP_NAME,
    val notificationType: NotificationType,
    var notificationExtraData: NotificationExtraData
)