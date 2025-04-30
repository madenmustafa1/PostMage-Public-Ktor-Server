package com.postmage.model.notification.fcm

data class FCMMessage(
    val topic: String,
    val notification: FCMNotification
)