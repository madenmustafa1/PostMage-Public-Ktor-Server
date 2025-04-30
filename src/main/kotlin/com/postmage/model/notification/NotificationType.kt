package com.postmage.model.notification

import kotlinx.serialization.Serializable

@Serializable
sealed class NotificationType(val value: Int) {
    object LIKE : NotificationType(1)
    object COMMENT : NotificationType(2)
    object FOLLOW : NotificationType(3)
    object GROUP_ADDED : NotificationType(4)
    object UNKNOWN : NotificationType(-1)
}